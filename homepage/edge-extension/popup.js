
const KEY = 'event_snapshot_v1';
const STARTER = [
  { title: 'Confirm venue', priority: 'high' },
  { title: 'Send invitations', priority: 'high' },
  { title: 'Lock headcount', priority: 'normal' },
  { title: 'Prepare agenda', priority: 'normal' },
  { title: 'Check AV / equipment', priority: 'normal' },
  { title: 'Plan guest arrival flow', priority: 'normal' }
];

const $ = (id) => document.getElementById(id);
const toast = (msg) => {
  const el = $('toast');
  el.textContent = msg;
  el.classList.add('show');
  setTimeout(() => el.classList.remove('show'), 1400);
};

async function load() {
  return (await chrome.storage.local.get(KEY))[KEY] || {
    eventName: '', eventDate: '', eventTime: '', venue: '', notes: '', tasks: []
  };
}
async function save(partial) {
  const cur = await load();
  const next = { ...cur, ...partial };
  await chrome.storage.local.set({ [KEY]: next });
  updateMeta(next);
  return next;
}

function updateMeta(data) {
  const done = data.tasks.filter(t => t.done).length;
  $('doneChip').textContent = `${done}/${data.tasks.length} done`;
  const dateStr = data.eventDate;
  if (!dateStr) {
    $('countdown').textContent = '—';
    $('countdownLabel').textContent = 'set a date';
    return;
  }
  const today = new Date(); today.setHours(0,0,0,0);
  const target = new Date(dateStr + 'T00:00:00');
  const days = Math.round((target - today) / 86400000);
  if (days > 0) {
    $('countdown').textContent = String(days);
    $('countdownLabel').textContent = days === 1 ? 'day to go' : 'days to go';
  } else if (days === 0) {
    $('countdown').textContent = '0';
    $('countdownLabel').textContent = 'today';
  } else {
    $('countdown').textContent = String(Math.abs(days));
    $('countdownLabel').textContent = 'days ago';
  }
}

function renderTasks(tasks) {
  const ul = $('tasks');
  ul.innerHTML = '';
  if (!tasks.length) {
    ul.innerHTML = '<li><span class="muted">No tasks yet. Add one or load the starter list.</span></li>';
    return;
  }
  const ordered = [...tasks].sort((a, b) => Number(a.done) - Number(b.done) || (a.priority === 'high' ? -1 : 1));
  ordered.forEach((t) => {
    const realIndex = tasks.indexOf(t);
    const li = document.createElement('li');
    const cb = document.createElement('input');
    cb.type = 'checkbox';
    cb.checked = !!t.done;
    cb.addEventListener('change', async () => {
      tasks[realIndex].done = cb.checked;
      await save({ tasks });
      renderTasks(tasks);
    });
    const span = document.createElement('span');
    span.textContent = (t.priority === 'high' ? '● ' : '') + t.title;
    if (t.done) span.style.opacity = '0.55';
    if (t.priority === 'high' && !t.done) span.style.fontWeight = '650';
    const del = document.createElement('button');
    del.className = 'ghost';
    del.type = 'button';
    del.textContent = '✕';
    del.addEventListener('click', async () => {
      tasks.splice(realIndex, 1);
      await save({ tasks });
      renderTasks(tasks);
    });
    li.append(cb, span, del);
    ul.appendChild(li);
  });
}

function buildSummary(data) {
  const open = data.tasks.filter(t => !t.done).map(t => `- [ ] ${t.title}`).join('\n') || '- [ ] (none)';
  const done = data.tasks.filter(t => t.done).map(t => `- [x] ${t.title}`).join('\n');
  return [
    data.eventName || 'Untitled event',
    data.eventDate ? `Date: ${data.eventDate}${data.eventTime ? ' ' + data.eventTime : ''}` : null,
    data.venue ? `Venue: ${data.venue}` : null,
    '',
    'Open tasks:',
    open,
    done ? '\nDone:\n' + done : null,
    data.notes ? '\nNotes:\n' + data.notes : null
  ].filter(Boolean).join('\n');
}

async function refresh() {
  const data = await load();
  $('eventName').value = data.eventName || '';
  $('eventDate').value = data.eventDate || '';
  $('eventTime').value = data.eventTime || '';
  $('venue').value = data.venue || '';
  $('notes').value = data.notes || '';
  renderTasks(data.tasks || []);
  updateMeta(data);
}

['eventName','eventDate','eventTime','venue','notes'].forEach((id) => {
  $(id).addEventListener('change', async () => {
    await save({
      eventName: $('eventName').value.trim(),
      eventDate: $('eventDate').value,
      eventTime: $('eventTime').value.trim(),
      venue: $('venue').value.trim(),
      notes: $('notes').value.trim()
    });
  });
});

$('addTask').addEventListener('click', async () => {
  const title = $('taskInput').value.trim();
  if (!title) return;
  const data = await load();
  data.tasks.push({ title, done: false, priority: $('priority').value });
  await save({ tasks: data.tasks });
  $('taskInput').value = '';
  renderTasks(data.tasks);
});

$('seed').addEventListener('click', async () => {
  const data = await load();
  const have = new Set(data.tasks.map(t => t.title));
  STARTER.forEach((t) => { if (!have.has(t.title)) data.tasks.push({ ...t, done: false }); });
  await save({ tasks: data.tasks });
  renderTasks(data.tasks);
});

$('clearDone').addEventListener('click', async () => {
  const data = await load();
  data.tasks = data.tasks.filter(t => !t.done);
  await save({ tasks: data.tasks });
  renderTasks(data.tasks);
});

$('resetAll').addEventListener('click', async () => {
  if (!confirm('Clear all event data?')) return;
  await chrome.storage.local.set({ [KEY]: { eventName:'', eventDate:'', eventTime:'', venue:'', notes:'', tasks:[] } });
  refresh();
});

$('copySummary').addEventListener('click', async () => {
  const data = await load();
  const text = buildSummary(data);
  try {
    await navigator.clipboard.writeText(text);
    toast('Summary copied');
  } catch {
    toast('Copy failed');
  }
});

refresh();
