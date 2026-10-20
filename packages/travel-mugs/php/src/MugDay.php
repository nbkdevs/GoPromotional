<?php

declare(strict_types=1);

namespace GoPromotional\MugDay;

use InvalidArgumentException;

/**
 * Caffeine and coffee intake tracker: typical caffeine per drink, daily totals
 * against a limit, caffeine still in your system (half-life model), when it
 * should drop below a "sleep-ready" level, and reusable-cup stats. The JSON
 * format matches the npm, PyPI, crates.io and RubyGems versions of gopromotional-mugday.
 */
final class MugDay
{
    /** Typical caffeine per serving (mg) and serving size (ml). Real drinks vary. */
    public const DRINKS = [
        'espresso' => ['mg' => 63, 'ml' => 30],
        'double-espresso' => ['mg' => 126, 'ml' => 60],
        'americano' => ['mg' => 126, 'ml' => 350],
        'latte' => ['mg' => 126, 'ml' => 350],
        'cappuccino' => ['mg' => 126, 'ml' => 350],
        'flat-white' => ['mg' => 126, 'ml' => 220],
        'filter-coffee' => ['mg' => 140, 'ml' => 300],
        'instant-coffee' => ['mg' => 100, 'ml' => 300],
        'cold-brew' => ['mg' => 155, 'ml' => 350],
        'decaf-coffee' => ['mg' => 3, 'ml' => 300],
        'tea' => ['mg' => 75, 'ml' => 300],
        'green-tea' => ['mg' => 30, 'ml' => 300],
        'cola' => ['mg' => 40, 'ml' => 330],
        'energy-drink' => ['mg' => 80, 'ml' => 250],
        'hot-chocolate' => ['mg' => 5, 'ml' => 300],
        'dark-chocolate-50g' => ['mg' => 25, 'ml' => 0],
    ];
    /** Daily guidance presets (mg): EFSA/NHS adult and pregnancy levels. */
    public const LIMITS = ['adult' => 400, 'pregnant' => 200, 'single_dose' => 200];
    public const DEFAULT_HALF_LIFE_HOURS = 5;

    /** @var array<int, array{drink:string, mg:int|float, time:string, reusable:bool}> */
    public array $entries = [];

    public function __construct(public int|float $limit = 400, public int|float $halfLifeHours = self::DEFAULT_HALF_LIFE_HOURS, array $entries = [])
    {
        foreach ($entries as $e) {
            $this->add($e['drink'], $e['time'] ?? null, $e['mg'] ?? null, (bool) ($e['reusable'] ?? false));
        }
    }

    /** Parse "YYYY-MM-DDTHH:MM" (or with a space) to minutes since 1970-01-01 (naive local time). */
    public static function toMinutes(string $s): int
    {
        $s = trim($s);
        if (!preg_match('/^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})$/', $s, $m)) {
            throw new InvalidArgumentException("Invalid time \"$s\" (expected YYYY-MM-DDTHH:MM)");
        }
        $min = intdiv(gmmktime((int) $m[4], (int) $m[5], 0, (int) $m[2], (int) $m[3], (int) $m[1]), 60);
        if (self::fromMinutes($min) !== "$m[1]-$m[2]-$m[3]T$m[4]:$m[5]") {
            throw new InvalidArgumentException("Invalid time \"$s\"");
        }
        return $min;
    }

    public static function fromMinutes(int $min): string
    {
        return gmdate('Y-m-d\TH:i', $min * 60);
    }

    public static function nowLocal(): string
    {
        return date('Y-m-d\TH:i');
    }

    private static function norm(string $drink): string
    {
        return preg_replace('/[\s_]+/', '-', strtolower(trim($drink)));
    }

    public static function lookupDrink(string $name): array
    {
        $k = self::norm($name);
        if (!isset(self::DRINKS[$k])) {
            throw new InvalidArgumentException("Unknown drink \"$name\". Known: " . implode(', ', array_keys(self::DRINKS)) . ' (or pass mg yourself)');
        }
        return ['drink' => $k] + self::DRINKS[$k];
    }

    public static function fromJson(string $json): self
    {
        $d = json_decode($json, true, 512, JSON_THROW_ON_ERROR) ?: [];
        return new self($d['limit'] ?? self::LIMITS['adult'], $d['halfLifeHours'] ?? self::DEFAULT_HALF_LIFE_HOURS, $d['entries'] ?? []);
    }

    public function toJson(): string
    {
        return json_encode(['limit' => $this->limit, 'halfLifeHours' => $this->halfLifeHours, 'entries' => $this->entries], JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES);
    }

    /** Log a drink; $mg overrides the typical value (handy for unknown drinks). Entries stay in time order. */
    public function add(string $drink, ?string $time = null, int|float|null $mg = null, bool $reusable = false): array
    {
        $name = self::norm($drink);
        if ($mg === null) {
            ['drink' => $name, 'mg' => $mg] = self::lookupDrink($drink);
        }
        if ($mg < 0 || $mg > 1000) {
            throw new InvalidArgumentException('mg must be between 0 and 1000');
        }
        $entry = ['drink' => $name, 'mg' => $mg, 'time' => self::fromMinutes(self::toMinutes($time ?? self::nowLocal())), 'reusable' => $reusable];
        $this->entries[] = $entry;
        usort($this->entries, fn ($a, $b) => self::toMinutes($a['time']) <=> self::toMinutes($b['time']));
        return $entry;
    }

    public function undo(): ?array
    {
        return array_pop($this->entries);
    }

    public function forDay(string $date): array
    {
        return array_values(array_filter($this->entries, fn ($e) => substr($e['time'], 0, 10) === $date));
    }

    /** Total caffeine for a calendar day compared with the limit. */
    public function day(?string $date = null, int|float|null $limit = null): array
    {
        $date ??= substr(self::nowLocal(), 0, 10);
        $limit ??= $this->limit;
        $items = $this->forDay($date);
        $total = array_sum(array_column($items, 'mg'));
        return ['date' => $date, 'drinks' => count($items), 'total' => $total, 'limit' => $limit, 'remaining' => max(0, $limit - $total), 'over' => max(0, $total - $limit)];
    }

    /** Estimated caffeine (mg) still in the body at $at, by exponential decay. */
    public function inSystem(?string $at = null): int
    {
        $t = self::toMinutes($at ?? self::nowLocal());
        $sum = 0.0;
        foreach ($this->entries as $e) {
            $dt = $t - self::toMinutes($e['time']);
            if ($dt >= 0) {
                $sum += $e['mg'] * 0.5 ** ($dt / 60 / $this->halfLifeHours);
            }
        }
        return (int) floor($sum + 0.5);
    }

    /** Earliest time (rounded up to 15 min) when caffeine falls below $threshold mg, if nothing more is drunk. */
    public function sleepReady(float $threshold = 50): ?string
    {
        if (!$this->entries) {
            return null;
        }
        $last = self::toMinutes(end($this->entries)['time']);
        $level = 0.0;
        foreach ($this->entries as $e) {
            $level += $e['mg'] * 0.5 ** (($last - self::toMinutes($e['time'])) / 60 / $this->halfLifeHours);
        }
        if ($level <= $threshold) {
            return self::fromMinutes($last);
        }
        $minutes = $this->halfLifeHours * 60 * log($level / $threshold, 2);
        return self::fromMinutes((int) (ceil(($last + $minutes) / 15) * 15));
    }

    /** Reusable vs disposable cups over the 7 days ending on $endDate. */
    public function cups(?string $endDate = null): array
    {
        $endDate ??= substr(self::nowLocal(), 0, 10);
        $end = intdiv(self::toMinutes("{$endDate}T00:00"), 1440);
        $inWeek = array_filter($this->entries, function ($e) use ($end) {
            $d = intdiv(self::toMinutes(substr($e['time'], 0, 10) . 'T00:00'), 1440);
            return $d <= $end && $d > $end - 7;
        });
        $reusable = count(array_filter($inWeek, fn ($e) => $e['reusable']));
        $n = count($inWeek);
        return ['reusable' => $reusable, 'disposable' => $n - $reusable, 'rate' => $n ? (int) floor($reusable * 100 / $n + 0.5) : 0];
    }
}
