
const KEY = 'mug_tap_v2';
const $ = (id) => document.getElementById(id);
const toast = (msg) => { const el=$('toast'); el.textContent=msg; el.classList.add('show'); setTimeout(()=>el.classList.remove('show'),1400); };
function dayStr(d = new Date()) { return d.toISOString().slice(0,10); }

async function load() {
  return (await chrome.storage.local.get(KEY))[KEY] || { logs: [] };
}
async function save(logs) {
  await chrome.storage.local.set({ [KEY]: { logs } });
  render(logs);
}

function weekStartKey() {
  const d = new Date(); d.setDate(d.getDate() - 6);
  return dayStr(d);
}

function calcStreak(logs) {
  let streak = 0;
  for (let i = 0; i < 60; i++) {
    const d = new Date(); d.setDate(d.getDate() - i);
    const key = dayStr(d);
    const hit = logs.some(l => l.day === key && l.reusable);
    if (hit) streak += 1;
    else if (i === 0) continue; // today can still be empty
    else break;
  }
  // if today empty, don't break streak until tomorrow; recount excluding today empty case
  const today = dayStr();
  if (!logs.some(l => l.day === today && l.reusable)) {
    streak = 0;
    for (let i = 1; i < 60; i++) {
      const d = new Date(); d.setDate(d.getDate() - i);
      const key = dayStr(d);
      if (logs.some(l => l.day === key && l.reusable)) streak += 1;
      else break;
    }
  }
  return streak;
}

function render(logs) {
  const today = dayStr();
  const from = weekStartKey();
  const reusableToday = logs.filter(l => l.day === today && l.reusable).length;
  const reusableWeek = logs.filter(l => l.day >= from && l.reusable).length;
  const disposableWeek = logs.filter(l => l.day >= from && !l.reusable).length;
  const totalWeek = reusableWeek + disposableWeek;
  $('today').textContent = String(reusableToday);
  $('week').textContent = `Last 7 days: ${reusableWeek} reusable · ${disposableWeek} disposable`;
  $('rate').textContent = totalWeek
    ? `Reusable rate this week: ${Math.round(reusableWeek / totalWeek * 100)}%`
    : 'Reusable rate this week: —';
  $('streak').textContent = `streak ${calcStreak(logs)}`;

  const ul = $('log');
  ul.replaceChildren();
  const recent = logs.slice(-10).reverse();
  if (!recent.length) {
    const li = document.createElement('li');
    const empty = document.createElement('span');
    empty.className = 'muted';
    empty.textContent = 'No drinks logged yet.';
    li.appendChild(empty);
    ul.appendChild(li);
    return;
  }
  recent.forEach((l) => {
    const li = document.createElement('li');
    const kind = document.createElement('span');
    kind.textContent = (l.reusable ? 'Reusable' : 'Disposable') + (l.note ? ` · ${l.note}` : '');
    const day = document.createElement('span');
    day.className = 'muted tiny';
    day.textContent = l.day;
    li.append(kind, day);
    ul.appendChild(li);
  });
}

async function add(reusable) {
  const data = await load();
  data.logs.push({
    reusable,
    day: dayStr(),
    at: Date.now(),
    note: $('drink').value.trim()
  });
  $('drink').value = '';
  await save(data.logs);
}

$('reusable').addEventListener('click', () => add(true));
$('disposable').addEventListener('click', () => add(false));
$('undo').addEventListener('click', async () => {
  const data = await load();
  data.logs.pop();
  await save(data.logs);
});
$('copySummary').addEventListener('click', async () => {
  const data = await load();
  const from = weekStartKey();
  const week = data.logs.filter(l => l.day >= from);
  const reusable = week.filter(l => l.reusable).length;
  const disposable = week.filter(l => !l.reusable).length;
  const text = [
    `Mug habit · last 7 days`,
    `Reusable: ${reusable}`,
    `Disposable: ${disposable}`,
    `Rate: ${reusable + disposable ? Math.round(reusable / (reusable + disposable) * 100) : 0}%`,
    `Streak: ${calcStreak(data.logs)} day(s)`
  ].join('\n');
  try { await navigator.clipboard.writeText(text); toast('Summary copied'); }
  catch { toast('Copy failed'); }
});
$('clearWeek').addEventListener('click', async () => {
  if (!confirm('Clear logs from the last 7 days?')) return;
  const data = await load();
  const from = weekStartKey();
  await save(data.logs.filter(l => l.day < from));
});

load().then(d => render(d.logs));
