
const KEY = 'sip_quick_v1';
const $ = (id) => document.getElementById(id);
const toast = (msg) => { const el=$('toast'); el.textContent=msg; el.classList.add('show'); setTimeout(()=>el.classList.remove('show'),1400); };
function dayKey(d = new Date()) { return d.toISOString().slice(0,10); }

async function state() {
  const all = (await chrome.storage.local.get(KEY))[KEY] || { goal: 2000, days: {}, history: {} };
  // history[day] = array of amounts for undo
  if (!all.history) all.history = {};
  if (!all.days) all.days = {};
  const day = dayKey();
  if (all.days[day] == null) all.days[day] = 0;
  if (!all.history[day]) all.history[day] = [];
  return all;
}
async function persist(all) { await chrome.storage.local.set({ [KEY]: all }); }

function render(all) {
  const day = dayKey();
  const total = all.days[day] || 0;
  const pct = Math.min(100, Math.round(total / Math.max(all.goal, 1) * 100));
  $('total').textContent = String(total);
  $('pct').textContent = pct + '%';
  $('goalLabel').textContent = total >= all.goal
    ? `Goal reached · ${all.goal} ml`
    : `Goal ${all.goal} ml · ${all.goal - total} ml to go`;
  $('bar').style.width = pct + '%';
  $('goal').value = all.goal;

  const ul = $('week');
  ul.replaceChildren();
  for (let i = 0; i < 7; i++) {
    const d = new Date(); d.setDate(d.getDate() - i);
    const key = dayKey(d);
    const ml = all.days[key] || 0;
    const label = i === 0 ? 'Today' : key.slice(5);
    const li = document.createElement('li');
    const name = document.createElement('span');
    name.textContent = label;
    const amount = document.createElement('strong');
    amount.textContent = `${ml} ml`;
    li.append(name, amount);
    ul.appendChild(li);
  }
}

async function add(ml) {
  const all = await state();
  const day = dayKey();
  all.days[day] = (all.days[day] || 0) + ml;
  all.history[day].push(ml);
  await persist(all);
  render(all);
}

document.querySelectorAll('[data-ml]').forEach((btn) => {
  btn.addEventListener('click', () => add(Number(btn.dataset.ml)));
});
$('addCustom').addEventListener('click', () => {
  const ml = Number($('custom').value);
  if (ml > 0) { add(ml); $('custom').value = ''; }
});
$('undo').addEventListener('click', async () => {
  const all = await state();
  const day = dayKey();
  const last = all.history[day].pop();
  if (!last) return;
  all.days[day] = Math.max(0, (all.days[day] || 0) - last);
  await persist(all);
  render(all);
});
$('saveGoal').addEventListener('click', async () => {
  const all = await state();
  const g = Number($('goal').value);
  if (g >= 500 && g <= 6000) { all.goal = g; await persist(all); render(all); toast('Goal saved'); }
});
$('reset').addEventListener('click', async () => {
  const all = await state();
  const day = dayKey();
  all.days[day] = 0;
  all.history[day] = [];
  await persist(all);
  render(all);
});
$('copyStats').addEventListener('click', async () => {
  const all = await state();
  const lines = [`Hydration goal: ${all.goal} ml`];
  for (let i = 0; i < 7; i++) {
    const d = new Date(); d.setDate(d.getDate() - i);
    const key = dayKey(d);
    lines.push(`${key}: ${all.days[key] || 0} ml`);
  }
  try { await navigator.clipboard.writeText(lines.join('\n')); toast('Stats copied'); }
  catch { toast('Copy failed'); }
});

state().then(render);
