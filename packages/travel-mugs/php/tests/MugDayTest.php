<?php

declare(strict_types=1);

use GoPromotional\MugDay\MugDay;
use PHPUnit\Framework\TestCase;

final class MugDayTest extends TestCase
{
    private static function sample(): MugDay
    {
        $log = new MugDay();
        $log->add('flat-white', '2026-10-19T08:00', reusable: true);
        $log->add('tea', '2026-10-19T11:00', reusable: true);
        $log->add('Filter coffee', '2026-10-19T14:00');
        return $log;
    }

    public function testDrinks(): void
    {
        $this->assertCount(16, MugDay::DRINKS);
        $this->assertSame(['drink' => 'flat-white', 'mg' => 126, 'ml' => 220], MugDay::lookupDrink('Flat White'));
        $this->expectExceptionMessage('Unknown drink');
        MugDay::lookupDrink('mystery');
    }

    public function testDay(): void
    {
        $log = self::sample();
        $this->assertSame(['date' => '2026-10-19', 'drinks' => 3, 'total' => 341, 'limit' => 400, 'remaining' => 59, 'over' => 0], $log->day('2026-10-19'));
        $this->assertSame(['date' => '2026-10-19', 'drinks' => 3, 'total' => 341, 'limit' => 200, 'remaining' => 0, 'over' => 141], $log->day('2026-10-19', MugDay::LIMITS['pregnant']));
        $this->assertSame(0, $log->day('2026-10-18')['total']);
    }

    public function testInSystemAndSleepReady(): void
    {
        $log = self::sample();
        $this->assertSame(0, $log->inSystem('2026-10-19T07:59'));
        $this->assertSame(244, $log->inSystem('2026-10-19T14:00'));
        $this->assertSame(81, $log->inSystem('2026-10-19T22:00'));
        $this->assertSame('2026-10-20T01:30', $log->sleepReady(50));
        $this->assertSame('2026-10-19T20:30', $log->sleepReady(100));
        $this->assertSame('2026-10-19T14:00', $log->sleepReady(500));
        $this->assertNull((new MugDay())->sleepReady());
    }

    public function testCupsUndoJson(): void
    {
        $log = self::sample();
        $log->add('Office machine', '2026-10-12T09:00', 90);
        $this->assertSame(['reusable' => 2, 'disposable' => 1, 'rate' => 67], $log->cups('2026-10-19'));
        $this->assertSame(['reusable' => 0, 'disposable' => 1, 'rate' => 0], $log->cups('2026-10-12'));
        $this->assertSame('filter-coffee', $log->undo()['drink']);
        $back = MugDay::fromJson($log->toJson());
        $this->assertSame(201, $back->day('2026-10-19')['total']);
        $this->expectException(InvalidArgumentException::class);
        $log->add('espresso', '2026-13-01T08:00');
    }
}
