<?php

declare(strict_types=1);

namespace GoPromotional\PackList;

use InvalidArgumentException;

/**
 * Packing checklists: built-in templates (work day, travel, trade show, gym),
 * per-night quantities, merging without duplicates, progress, and a Markdown
 * format that round-trips, so a checklist can live in a plain .md file.
 * Items are arrays: ['name' => string, 'category' => string, 'qty' => int, 'packed' => bool].
 */
final class PackList
{
    /** Template rows: [name, category, perNight]. */
    public const TEMPLATES = [
        'work-day' => [
            ['Laptop', 'Work', false], ['Laptop charger', 'Work', false], ['Phone charger', 'Work', false], ['Badge / ID', 'Work', false],
            ['Notebook', 'Work', false], ['Pen', 'Work', false], ['Headphones', 'Work', false], ['Water bottle', 'Work', false],
            ['Reusable coffee cup', 'Work', false], ['Lunch / snacks', 'Work', false],
        ],
        'travel' => [
            ['Passport / ID', 'Documents', false], ['Tickets / boarding pass', 'Documents', false], ['Bank cards', 'Documents', false],
            ['T-shirts', 'Clothes', true], ['Underwear', 'Clothes', true], ['Socks', 'Clothes', true],
            ['Trousers / skirt', 'Clothes', false], ['Jumper', 'Clothes', false], ['Sleepwear', 'Clothes', false],
            ['Toothbrush', 'Toiletries', false], ['Toothpaste', 'Toiletries', false], ['Deodorant', 'Toiletries', false], ['Medications', 'Toiletries', false],
            ['Phone charger', 'Tech', false], ['Power bank', 'Tech', false], ['Plug adapter', 'Tech', false], ['Headphones', 'Tech', false],
            ['Reusable water bottle', 'Other', false], ['Tote bag', 'Other', false],
        ],
        'trade-show' => [
            ['Pop-up banner / stand', 'Stand', false], ['Table cloth', 'Stand', false], ['Branded giveaways', 'Stand', false],
            ['Business cards', 'Stand', false], ['Brochures / flyers', 'Stand', false], ['Lead capture sheet or scanner', 'Stand', false],
            ['Extension lead', 'Kit', false], ['Laptop / tablet', 'Kit', false], ['Phone charger', 'Kit', false], ['Gaffer tape', 'Kit', false],
            ['Scissors', 'Kit', false], ['Cable ties', 'Kit', false], ['Pens', 'Kit', false],
            ['Water bottles', 'Team', false], ['Snacks', 'Team', false], ['First-aid kit', 'Team', false],
        ],
        'gym' => [
            ['Gym clothes', 'Gym', false], ['Trainers', 'Gym', false], ['Towel', 'Gym', false], ['Water bottle', 'Gym', false], ['Padlock', 'Gym', false],
            ['Headphones', 'Gym', false], ['Shower gel', 'Gym', false], ['Deodorant', 'Gym', false], ['Snack', 'Gym', false],
        ],
    ];

    private static function key(string $name): string
    {
        return mb_strtolower(trim($name));
    }

    /**
     * Build a checklist from templates plus extras. Per-night items get nights + 1
     * (one spare); duplicates are merged by name (first wins).
     *
     * @param string[] $templates
     * @param string[] $extras
     */
    public static function buildList(array $templates = ['travel'], int $nights = 0, array $extras = []): array
    {
        if ($nights < 0 || $nights > 365) {
            throw new InvalidArgumentException('nights must be an integer from 0 to 365');
        }
        $seen = [];
        $items = [];
        $push = function (array $it) use (&$seen, &$items): void {
            $k = self::key($it['name']);
            if ($k === '' || isset($seen[$k])) {
                return;
            }
            $seen[$k] = true;
            $items[] = $it;
        };
        foreach ($templates as $t) {
            if (!isset(self::TEMPLATES[$t])) {
                throw new InvalidArgumentException("Unknown template \"$t\". Choose from: " . implode(', ', array_keys(self::TEMPLATES)));
            }
            foreach (self::TEMPLATES[$t] as [$name, $category, $perNight]) {
                $push(['name' => $name, 'category' => $category, 'qty' => $perNight && $nights > 0 ? $nights + 1 : 1, 'packed' => false]);
            }
        }
        foreach ($extras as $e) {
            $push(['name' => trim((string) $e), 'category' => 'Extras', 'qty' => 1, 'packed' => false]);
        }
        return $items;
    }

    /** Index of the item matching $name: exact name first, then a unique substring. */
    public static function find(array $items, string $name): int
    {
        $k = self::key($name);
        foreach ($items as $i => $it) {
            if (self::key($it['name']) === $k) {
                return $i;
            }
        }
        $partial = array_keys(array_filter($items, fn ($it) => str_contains(self::key($it['name']), $k)));
        if (count($partial) === 1) {
            return $partial[0];
        }
        if (count($partial) > 1) {
            $names = implode(', ', array_map(fn ($i) => $items[$i]['name'], $partial));
            throw new InvalidArgumentException("\"$name\" matches " . count($partial) . " items: $names");
        }
        throw new InvalidArgumentException("No item matches \"$name\"");
    }

    /** Mark an item packed (or toggle when $packed is null). Returns the updated item. */
    public static function mark(array &$items, string $name, ?bool $packed = null): array
    {
        $i = self::find($items, $name);
        $items[$i]['packed'] = $packed ?? !$items[$i]['packed'];
        return $items[$i];
    }

    public static function progress(array $items): array
    {
        $packed = count(array_filter($items, fn ($i) => $i['packed']));
        $total = count($items);
        return ['packed' => $packed, 'total' => $total, 'percent' => $total ? (int) floor($packed * 100 / $total + 0.5) : 0];
    }

    public static function remaining(array $items): array
    {
        return array_values(array_filter($items, fn ($i) => !$i['packed']));
    }

    public static function filterItems(array $items, string $q): array
    {
        $k = self::key($q);
        return array_values(array_filter($items, fn ($i) => str_contains(self::key($i['name']), $k) || str_contains(self::key($i['category']), $k)));
    }

    /** @return string[] categories in first-seen order */
    public static function categories(array $items): array
    {
        return array_values(array_unique(array_map(fn ($i) => $i['category'], $items)));
    }

    private static function label(array $i): string
    {
        return $i['qty'] > 1 ? "{$i['qty']} × {$i['name']}" : $i['name'];
    }

    /** Render as Markdown checkboxes grouped by category; parseMarkdown() reads it back. */
    public static function toMarkdown(array $items, string $title = 'Packing list'): string
    {
        $out = ["# $title", ''];
        foreach (self::categories($items) as $c) {
            $out[] = "## $c";
            foreach ($items as $i) {
                if ($i['category'] === $c) {
                    $out[] = '- [' . ($i['packed'] ? 'x' : ' ') . '] ' . self::label($i);
                }
            }
            $out[] = '';
        }
        return implode("\n", $out);
    }

    public static function toText(array $items): string
    {
        $blocks = [];
        foreach (self::categories($items) as $c) {
            $lines = [$c];
            foreach ($items as $i) {
                if ($i['category'] === $c) {
                    $lines[] = '  ' . ($i['packed'] ? '[x]' : '[ ]') . ' ' . self::label($i);
                }
            }
            $blocks[] = implode("\n", $lines);
        }
        return implode("\n", $blocks);
    }

    /** @return array{title:string, items:array} */
    public static function parseMarkdown(string $md): array
    {
        $title = 'Packing list';
        $category = 'Items';
        $items = [];
        foreach (preg_split('/\r?\n/', $md) as $raw) {
            $line = trim($raw);
            if (preg_match('/^#\s+(.+)$/u', $line, $m)) {
                $title = trim($m[1]);
            } elseif (preg_match('/^##\s+(.+)$/u', $line, $m)) {
                $category = trim($m[1]);
            } elseif (preg_match('/^[-*]\s+\[([ xX])\]\s+(.+)$/u', $line, $m)) {
                $name = trim($m[2]);
                $qty = 1;
                if (preg_match('/^(\d+)\s*[×x]\s+(.+)$/u', $name, $q)) {
                    $qty = (int) $q[1];
                    $name = trim($q[2]);
                }
                $items[] = ['name' => $name, 'category' => $category, 'qty' => $qty, 'packed' => $m[1] !== ' '];
            }
        }
        return ['title' => $title, 'items' => $items];
    }
}
