/*! gopromotional-event-planner v1.0.1 | MIT | https://www.gopromotional.co.uk/ */
// Event planning helpers: countdown, planning timeline, giveaway quantities and
// the latest order date for printed merchandise. Works in Node.js and browsers.
(function (root, factory) {
  if (typeof module === 'object' && module.exports) module.exports = factory();
  else root.EventPlanner = factory();
})(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  const DAY_MS = 86400000;

  /** Parse "YYYY-MM-DD" into a day number (days since 1970-01-01, UTC). */
  function toDay(date) {
    const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(String(date).trim());
    if (!m) throw new TypeError(`Invalid date "${date}" (expected YYYY-MM-DD)`);
    const [y, mo, d] = [Number(m[1]), Number(m[2]), Number(m[3])];
    const t = Date.UTC(y, mo - 1, d);
    const back = new Date(t);
    if (back.getUTCFullYear() !== y || back.getUTCMonth() !== mo - 1 || back.getUTCDate() !== d) {
      throw new RangeError(`Invalid calendar date "${date}"`);
    }
    return Math.round(t / DAY_MS);
  }

  /** Format a day number as "YYYY-MM-DD". */
  function fromDay(day) {
    return new Date(day * DAY_MS).toISOString().slice(0, 10);
  }

  /** ISO weekday: 1 = Monday … 7 = Sunday. */
  function weekday(day) {
    const w = (((day + 4) % 7) + 7) % 7; // 1970-01-01 was a Thursday (4)
    return w === 0 ? 7 : w;
  }

  function todayISO() {
    const now = new Date();
    return fromDay(Math.floor((now.getTime() - now.getTimezoneOffset() * 60000) / DAY_MS));
  }

  /** Days until an event, split into weeks + days. */
  function countdown(eventDate, today = todayISO()) {
    const days = toDay(eventDate) - toDay(today);
    const abs = Math.abs(days);
    return {
      eventDate: fromDay(toDay(eventDate)),
      today: fromDay(toDay(today)),
      days,
      weeks: Math.floor(abs / 7),
      remainderDays: abs % 7,
      status: days > 0 ? 'upcoming' : days === 0 ? 'today' : 'past'
    };
  }

  /** Default planning template: days before the event (negative = after). */
  const TIMELINE_TEMPLATE = Object.freeze([
    { offsetDays: 56, category: 'planning', task: 'Confirm budget and attendee estimate' },
    { offsetDays: 49, category: 'venue', task: 'Book the venue' },
    { offsetDays: 42, category: 'merchandise', task: 'Order branded merchandise and giveaways' },
    { offsetDays: 35, category: 'comms', task: 'Send invitations or open registration' },
    { offsetDays: 28, category: 'programme', task: 'Confirm speakers and agenda' },
    { offsetDays: 21, category: 'venue', task: 'Arrange catering' },
    { offsetDays: 14, category: 'merchandise', task: 'Approve print proofs and signage' },
    { offsetDays: 7, category: 'comms', task: 'Send a reminder to attendees' },
    { offsetDays: 3, category: 'logistics', task: 'Confirm deliveries and the staffing rota' },
    { offsetDays: 1, category: 'logistics', task: 'Pack the event kit and giveaways' },
    { offsetDays: 0, category: 'logistics', task: 'Event day: set up the stand and registration' },
    { offsetDays: -2, category: 'comms', task: 'Send thank-you and follow-up messages' }
  ].map(Object.freeze));

  /**
   * Planning timeline with a due date per task, sorted by due date.
   * If `today` is given, each task gets `overdue` and `daysLeft`.
   */
  function timeline(eventDate, { today = null, template = TIMELINE_TEMPLATE } = {}) {
    const ev = toDay(eventDate);
    const t = today == null ? null : toDay(today);
    return template
      .map((item) => {
        const due = ev - item.offsetDays;
        const row = { task: item.task, category: item.category, offsetDays: item.offsetDays, due: fromDay(due) };
        if (t != null) { row.daysLeft = due - t; row.overdue = due < t; }
        return row;
      })
      .sort((a, b) => (a.due < b.due ? -1 : a.due > b.due ? 1 : 0));
  }

  function ceilClean(x) {
    // Guard against binary noise such as 300.00000000000006 before rounding up.
    return Math.ceil(Math.round(x * 1e6) / 1e6);
  }

  /**
   * Estimate how many giveaway items to order.
   * base = attendees × days × uptake × perPerson (rounded up); buffer = base × buffer (rounded up).
   */
  function giveawayQuantity({ attendees, days = 1, uptake = 0.6, perPerson = 1, buffer = 0.1 } = {}) {
    const nums = { attendees, days, uptake, perPerson, buffer };
    for (const [k, v] of Object.entries(nums)) {
      if (typeof v !== 'number' || !Number.isFinite(v) || v < 0) throw new RangeError(`${k} must be a non-negative number`);
    }
    if (uptake > 1) throw new RangeError('uptake must be between 0 and 1');
    const base = ceilClean(attendees * days * uptake * perPerson);
    const extra = ceilClean(base * buffer);
    return { base, buffer: extra, total: base + extra };
  }

  /** Step back `n` working days (Mon–Fri) from a day number. */
  function subtractWorkingDays(day, n) {
    let d = day;
    let left = n;
    while (left > 0) {
      d -= 1;
      if (weekday(d) <= 5) left -= 1;
    }
    return d;
  }

  /**
   * Latest date to place an order for printed merchandise so it arrives in time.
   * Counts production + delivery + safety margin in working days before the event.
   */
  function orderByDate(eventDate, { productionDays = 10, deliveryDays = 3, safetyDays = 2, today = null } = {}) {
    for (const [k, v] of Object.entries({ productionDays, deliveryDays, safetyDays })) {
      if (!Number.isInteger(v) || v < 0) throw new RangeError(`${k} must be a non-negative integer`);
    }
    const workingDays = productionDays + deliveryDays + safetyDays;
    const by = subtractWorkingDays(toDay(eventDate), workingDays);
    const out = { orderBy: fromDay(by), workingDays };
    if (today != null) { out.daysFromToday = by - toDay(today); out.late = out.daysFromToday < 0; }
    return out;
  }

  return {
    countdown, timeline, giveawayQuantity, orderByDate, subtractWorkingDays,
    toDay, fromDay, weekday, todayISO, TIMELINE_TEMPLATE
  };
});
