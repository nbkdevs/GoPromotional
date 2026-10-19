/*! gopromotional-mugday v1.0.1 | MIT | https://www.gopromotional.co.uk/branded-products/travel-mugs */
// Caffeine and coffee intake tracker: typical caffeine per drink, daily totals
// against a limit, an estimate of caffeine still in your system (half-life
// model), when it should drop below a "sleep-ready" level, and reusable-cup stats.
(function (root, factory) {
  if (typeof module === 'object' && module.exports) module.exports = factory();
  else root.MugDay = factory();
})(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  /** Typical caffeine per serving (mg) and serving size (ml). Real drinks vary by shop, bean and strength. */
  const DRINKS = Object.freeze({
    'espresso': { mg: 63, ml: 30 },
    'double-espresso': { mg: 126, ml: 60 },
    'americano': { mg: 126, ml: 350 },
    'latte': { mg: 126, ml: 350 },
    'cappuccino': { mg: 126, ml: 350 },
    'flat-white': { mg: 126, ml: 220 },
    'filter-coffee': { mg: 140, ml: 300 },
    'instant-coffee': { mg: 100, ml: 300 },
    'cold-brew': { mg: 155, ml: 350 },
    'decaf-coffee': { mg: 3, ml: 300 },
    'tea': { mg: 75, ml: 300 },
    'green-tea': { mg: 30, ml: 300 },
    'cola': { mg: 40, ml: 330 },
    'energy-drink': { mg: 80, ml: 250 },
    'hot-chocolate': { mg: 5, ml: 300 },
    'dark-chocolate-50g': { mg: 25, ml: 0 }
  });
  /** Daily guidance figures (mg) used as presets: EFSA/NHS adult and pregnancy levels. */
  const LIMITS = Object.freeze({ adult: 400, pregnant: 200, single_dose: 200 });
  const DEFAULT_HALF_LIFE_HOURS = 5;

  const pad = (n) => String(n).padStart(2, '0');
  /** Parse "YYYY-MM-DDTHH:MM" (or with a space) to minutes since 1970-01-01 (naive local time). */
  function toMinutes(s) {
    const m = /^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})$/.exec(String(s).trim());
    if (!m) throw new TypeError(`Invalid time "${s}" (expected YYYY-MM-DDTHH:MM)`);
    const [y, mo, d, h, mi] = m.slice(1).map(Number);
    const min = Math.round(Date.UTC(y, mo - 1, d, h, mi) / 60000);
    if (fromMinutes(min) !== `${m[1]}-${m[2]}-${m[3]}T${m[4]}:${m[5]}`) throw new RangeError(`Invalid time "${s}"`);
    return min;
  }
  function fromMinutes(min) {
    const d = new Date(min * 60000);
    return `${d.getUTCFullYear()}-${pad(d.getUTCMonth() + 1)}-${pad(d.getUTCDate())}T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}`;
  }
  function nowLocal() {
    const n = new Date();
    return `${n.getFullYear()}-${pad(n.getMonth() + 1)}-${pad(n.getDate())}T${pad(n.getHours())}:${pad(n.getMinutes())}`;
  }
  const dateOf = (time) => fromMinutes(toMinutes(time)).slice(0, 10);
  const round = (x) => Math.floor(x + 0.5);

  function lookupDrink(name) {
    const k = String(name).trim().toLowerCase().replace(/[\s_]+/g, '-');
    const d = DRINKS[k];
    if (!d) throw new RangeError(`Unknown drink "${name}". Known: ${Object.keys(DRINKS).join(', ')} (or pass mg yourself)`);
    return { drink: k, ...d };
  }

  class CaffeineLog {
    constructor({ limit = LIMITS.adult, halfLifeHours = DEFAULT_HALF_LIFE_HOURS, entries = [] } = {}) {
      this.limit = limit; this.halfLifeHours = halfLifeHours;
      this.entries = [];
      for (const e of entries) this.add(e.drink, { time: e.time, mg: e.mg, reusable: e.reusable });
    }
    static fromJSON(data) { return new CaffeineLog(typeof data === 'string' ? JSON.parse(data) : data || {}); }
    toJSON() { return { limit: this.limit, halfLifeHours: this.halfLifeHours, entries: this.entries.map((e) => ({ ...e })) }; }

    /** Log a drink. `mg` overrides the typical value (handy for unknown drinks). */
    add(drink, { time = nowLocal(), mg, reusable = false } = {}) {
      let caffeine = mg;
      let name = String(drink).trim().toLowerCase().replace(/[\s_]+/g, '-');
      if (caffeine === undefined || caffeine === null) ({ mg: caffeine, drink: name } = lookupDrink(drink));
      if (typeof caffeine !== 'number' || !(caffeine >= 0) || caffeine > 1000) throw new RangeError('mg must be between 0 and 1000');
      const entry = { drink: name, mg: caffeine, time: fromMinutes(toMinutes(time)), reusable: !!reusable };
      this.entries.push(entry);
      this.entries.sort((a, b) => toMinutes(a.time) - toMinutes(b.time));
      return entry;
    }
    undo() { return this.entries.pop() || null; }
    forDay(date) { return this.entries.filter((e) => e.time.slice(0, 10) === date); }

    /** Total caffeine for a calendar day and how it compares with the limit. */
    day(date = nowLocal().slice(0, 10), limit = this.limit) {
      const items = this.forDay(date);
      const total = items.reduce((s, e) => s + e.mg, 0);
      return { date, drinks: items.length, total, limit, remaining: Math.max(0, limit - total), over: Math.max(0, total - limit) };
    }
    /** Estimated caffeine (mg) still in the body at `at`, using exponential decay with the log's half-life. */
    inSystem(at = nowLocal()) {
      const t = toMinutes(at);
      let sum = 0;
      for (const e of this.entries) {
        const dt = t - toMinutes(e.time);
        if (dt >= 0) sum += e.mg * Math.pow(0.5, dt / 60 / this.halfLifeHours);
      }
      return round(sum);
    }
    /**
     * Earliest time (rounded up to 15 minutes) when caffeine should fall below `threshold` mg,
     * assuming nothing more is drunk after the last logged entry.
     */
    sleepReady(threshold = 50) {
      if (!this.entries.length) return null;
      const last = toMinutes(this.entries[this.entries.length - 1].time);
      let level = 0;
      for (const e of this.entries) level += e.mg * Math.pow(0.5, (last - toMinutes(e.time)) / 60 / this.halfLifeHours);
      if (level <= threshold) return fromMinutes(last);
      const minutes = this.halfLifeHours * 60 * Math.log2(level / threshold);
      return fromMinutes(Math.ceil((last + minutes) / 15) * 15);
    }
    /** Reusable vs disposable cups over the 7 days ending on `endDate`. */
    cups(endDate = nowLocal().slice(0, 10)) {
      const end = toMinutes(`${endDate}T00:00`) / 1440;
      const inWeek = this.entries.filter((e) => { const d = toMinutes(`${e.time.slice(0, 10)}T00:00`) / 1440; return d <= end && d > end - 7; });
      const reusable = inWeek.filter((e) => e.reusable).length;
      const disposable = inWeek.length - reusable;
      return { reusable, disposable, rate: inWeek.length ? round((reusable * 100) / inWeek.length) : 0 };
    }
  }

  return { DRINKS, LIMITS, DEFAULT_HALF_LIFE_HOURS, CaffeineLog, lookupDrink, toMinutes, fromMinutes, nowLocal, dateOf };
});
