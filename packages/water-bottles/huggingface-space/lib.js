/*! gopromotional-siplog v1.0.1 | MIT | https://www.gopromotional.co.uk/branded-products/branded-water-bottles */
// Hydration helpers: a daily water goal estimate, amount parsing and a small
// day-by-day water log with progress, 7-day history and streaks.
(function (root, factory) {
  if (typeof module === 'object' && module.exports) module.exports = factory();
  else root.SipLog = factory();
})(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  const DAY_MS = 86400000;
  const ML_PER_US_FL_OZ = 29.5735;
  const ML_PER_ACTIVE_MINUTE = 12;
  const HOT_WEATHER_ML = 500;

  function toDay(date) {
    const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(String(date).trim());
    if (!m) throw new TypeError(`Invalid date "${date}" (expected YYYY-MM-DD)`);
    const t = Date.UTC(Number(m[1]), Number(m[2]) - 1, Number(m[3]));
    if (new Date(t).toISOString().slice(0, 10) !== m[0]) throw new RangeError(`Invalid calendar date "${date}"`);
    return Math.round(t / DAY_MS);
  }
  const fromDay = (day) => new Date(day * DAY_MS).toISOString().slice(0, 10);
  function todayISO() {
    const now = new Date();
    return fromDay(Math.floor((now.getTime() - now.getTimezoneOffset() * 60000) / DAY_MS));
  }

  /**
   * Rough daily water goal in ml (a general guide, not medical advice):
   * weight × mlPerKg + activeMinutes × 12 ml (+500 ml in hot weather), rounded to the nearest 50 ml.
   */
  function dailyGoal({ weightKg, activityMinutes = 0, hot = false, mlPerKg = 33 } = {}) {
    if (typeof weightKg !== 'number' || !(weightKg > 0) || weightKg > 400) throw new RangeError('weightKg must be between 0 and 400');
    if (typeof activityMinutes !== 'number' || activityMinutes < 0) throw new RangeError('activityMinutes must be >= 0');
    const raw = weightKg * mlPerKg + activityMinutes * ML_PER_ACTIVE_MINUTE + (hot ? HOT_WEATHER_ML : 0);
    return Math.floor(raw / 50 + 0.5) * 50;
  }

  /** Parse "500", "500ml", "0.5l", "1.5 L", "33cl" or "16oz" into whole millilitres. */
  function parseAmount(input) {
    if (typeof input === 'number') {
      if (!(input > 0)) throw new RangeError('amount must be positive');
      return Math.floor(input + 0.5);
    }
    const m = /^\s*(\d+(?:\.\d+)?)\s*(ml|cl|l|oz)?\s*$/i.exec(String(input));
    if (!m) throw new TypeError(`Cannot read amount "${input}" (try 250, 500ml, 0.5l, 33cl or 16oz)`);
    const n = Number(m[1]);
    const unit = (m[2] || 'ml').toLowerCase();
    const factor = { ml: 1, cl: 10, l: 1000, oz: ML_PER_US_FL_OZ }[unit];
    const ml = Math.floor(n * factor + 0.5);
    if (!(ml > 0)) throw new RangeError('amount must be positive');
    return ml;
  }

  /** A day-by-day hydration log. Entries are { date: 'YYYY-MM-DD', ml }. */
  class HydrationLog {
    constructor({ goal = 2000, entries = [] } = {}) {
      this.goal = goal;
      this.entries = entries.map((e) => ({ date: fromDay(toDay(e.date)), ml: parseAmount(e.ml) }));
    }
    static fromJSON(data) { return new HydrationLog(typeof data === 'string' ? JSON.parse(data) : data || {}); }
    toJSON() { return { goal: this.goal, entries: this.entries.map((e) => ({ ...e })) }; }

    add(amount, date = todayISO()) {
      const entry = { date: fromDay(toDay(date)), ml: parseAmount(amount) };
      this.entries.push(entry);
      return entry;
    }
    /** Remove the most recent entry for a day; returns it, or null. */
    undo(date = todayISO()) {
      const d = fromDay(toDay(date));
      for (let i = this.entries.length - 1; i >= 0; i--) {
        if (this.entries[i].date === d) return this.entries.splice(i, 1)[0];
      }
      return null;
    }
    clearDay(date = todayISO()) {
      const d = fromDay(toDay(date));
      const before = this.entries.length;
      this.entries = this.entries.filter((e) => e.date !== d);
      return before - this.entries.length;
    }
    total(date = todayISO()) {
      const d = fromDay(toDay(date));
      return this.entries.reduce((s, e) => (e.date === d ? s + e.ml : s), 0);
    }
    progress(date = todayISO(), goal = this.goal) {
      const total = this.total(date);
      return {
        date: fromDay(toDay(date)), total, goal,
        percent: goal > 0 ? Math.floor((total * 100) / goal + 0.5) : 0,
        remaining: Math.max(0, goal - total), reached: total >= goal
      };
    }
    /** Totals for the 7 days ending on `endDate`, newest first. */
    week(endDate = todayISO()) {
      const end = toDay(endDate);
      return Array.from({ length: 7 }, (_, i) => ({ date: fromDay(end - i), total: this.total(fromDay(end - i)) }));
    }
    /** Consecutive days meeting the goal, ending today (or yesterday if today is not done yet). */
    streak(endDate = todayISO(), goal = this.goal) {
      let day = toDay(endDate);
      if (this.total(fromDay(day)) < goal) day -= 1;
      let n = 0;
      while (n < 3660 && this.total(fromDay(day)) >= goal) { n += 1; day -= 1; }
      return n;
    }
    average(endDate = todayISO(), days = 7) {
      const end = toDay(endDate);
      let sum = 0;
      for (let i = 0; i < days; i++) sum += this.total(fromDay(end - i));
      return Math.floor(sum / days + 0.5);
    }
  }

  return { dailyGoal, parseAmount, HydrationLog, toDay, fromDay, todayISO, ML_PER_US_FL_OZ };
});
