<?php

declare(strict_types=1);

use GoPromotional\PackList\PackList as PL;
use PHPUnit\Framework\TestCase;

final class PackListTest extends TestCase
{
    private static function byName(array $items, string $n): array
    {
        return array_values(array_filter($items, fn ($i) => $i['name'] === $n))[0];
    }

    public function testTemplateSizes(): void
    {
        $this->assertSame(['work-day' => 10, 'travel' => 19, 'trade-show' => 16, 'gym' => 9], array_map('count', PL::TEMPLATES));
    }

    public function testPerNight(): void
    {
        $items = PL::buildList(['travel'], 3);
        $this->assertSame(4, self::byName($items, 'T-shirts')['qty']);
        $this->assertSame(1, self::byName($items, 'Jumper')['qty']);
        $this->assertSame(1, self::byName(PL::buildList(['travel']), 'Socks')['qty']);
    }

    public function testMerge(): void
    {
        $items = PL::buildList(['work-day', 'travel'], 0, ['Umbrella', 'laptop']);
        $this->assertCount(28, $items);
        $this->assertCount(1, array_filter($items, fn ($i) => $i['name'] === 'Phone charger'));
        $this->assertSame('Work', self::byName($items, 'Phone charger')['category']);
        $this->assertSame(['name' => 'Umbrella', 'category' => 'Extras', 'qty' => 1, 'packed' => false], end($items));
        $this->expectExceptionMessage('Unknown template');
        PL::buildList(['moon']);
    }

    public function testMarkProgressFilter(): void
    {
        $items = PL::buildList(['gym']);
        PL::mark($items, 'towel', true);
        PL::mark($items, 'Trainers');
        $this->assertSame(['packed' => 2, 'total' => 9, 'percent' => 22], PL::progress($items));
        $this->assertCount(7, PL::remaining($items));
        $this->assertSame(['Shower gel'], array_column(PL::filterItems($items, 'sh'), 'name'));
        try {
            PL::mark($items, 'e');
            $this->fail('expected ambiguity error');
        } catch (InvalidArgumentException $e) {
            $this->assertStringContainsString('matches', $e->getMessage());
        }
        $this->expectExceptionMessage('No item');
        PL::mark($items, 'kayak');
    }

    public function testMarkdownRoundTrip(): void
    {
        $items = PL::buildList(['travel'], 2);
        PL::mark($items, 'Passport / ID', true);
        $md = PL::toMarkdown($items, 'Lisbon');
        $this->assertStringStartsWith("# Lisbon\n\n## Documents\n- [x] Passport / ID\n", $md);
        $this->assertStringContainsString('- [ ] 3 × T-shirts', $md);
        $back = PL::parseMarkdown($md);
        $this->assertSame('Lisbon', $back['title']);
        $this->assertSame($items, $back['items']);
    }
}
