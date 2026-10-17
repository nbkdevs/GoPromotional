
const KEY = 'pack_quick_v2';
const WORK = ['Laptop', 'Charger', 'Badge / ID', 'Notebook', 'Pen', 'Headphones', 'Water bottle', 'Snacks'];
const TRAVEL = ['Passport / ID', 'Tickets', 'Phone charger', 'Toothbrush', 'Medications', 'Clothes', 'Headphones', 'Snacks'];
const $ = (id) => document.getElementById(id);
const toast = (msg) => { const el=$('toast'); el.textContent=msg; el.classList.add('show'); setTimeout(()=>el.classList.remove('show'),1400); };

async function load() {
  let data = (await chrome.storage.local.get(KEY))[KEY];
  if (!data || !data.lists || !data.lists.length) {
    data = {
      activeId: 'default',
      lists: [{ id: 'default', name: 'Today’s bag', items: [] }]
    };
    await chrome.storage.local.set({ [KEY]: data });
  }
  return data;
}
async function save(data) { await chrome.storage.local.set({ [KEY]: data }); }
function active(data) { return data.lists.find(l => l.id === data.activeId) || data.lists[0]; }

function renderLists(data) {
  const sel = $('listSelect');
  sel.innerHTML = '';
  data.lists.forEach((l) => {
    const opt = document.createElement('option');
    opt.value = l.id; opt.textContent = l.name;
    if (l.id === data.activeId) opt.selected = true;
    sel.appendChild(opt);
  });
  $('listName').value = active(data).name;
}

function renderItems(data) {
  const list = active(data);
  const filter = $('filter').value.trim().toLowerCase();
  const packed = list.items.filter(i => i.done).length;
  $('progress').textContent = `${packed}/${list.items.length} packed`;
  const ul = $('items');
  ul.innerHTML = '';
  const items = list.items
    .map((it, idx) => ({ ...it, idx }))
    .filter(it => !filter || it.title.toLowerCase().includes(filter));
  if (!items.length) {
    ul.innerHTML = '<li><span class="muted">No items yet. Add something or use a template.</span></li>';
    return;
  }
  items.sort((a,b) => Number(a.done) - Number(b.done)).forEach((it) => {
    const li = document.createElement('li');
    const cb = document.createElement('input');
    cb.type = 'checkbox'; cb.checked = !!it.done;
    cb.addEventListener('change', async () => {
      list.items[it.idx].done = cb.checked;
      await save(data); renderItems(data);
    });
    const span = document.createElement('span');
    span.textContent = it.title;
    if (it.done) span.style.opacity = '0.55';
    const del = document.createElement('button');
    del.className = 'ghost'; del.type = 'button'; del.textContent = '✕';
    del.addEventListener('click', async () => {
      list.items.splice(it.idx, 1);
      await save(data); renderItems(data);
    });
    li.append(cb, span, del);
    ul.appendChild(li);
  });
}

async function refresh() {
  const data = await load();
  renderLists(data);
  renderItems(data);
}

async function seed(template) {
  const data = await load();
  const list = active(data);
  const have = new Set(list.items.map(i => i.title));
  template.forEach((title) => { if (!have.has(title)) list.items.push({ title, done: false }); });
  await save(data); renderItems(data);
}

$('listSelect').addEventListener('change', async () => {
  const data = await load();
  data.activeId = $('listSelect').value;
  await save(data); refresh();
});
$('newList').addEventListener('click', async () => {
  const data = await load();
  const id = 'l' + Date.now();
  data.lists.push({ id, name: 'New list', items: [] });
  data.activeId = id;
  await save(data); refresh();
});
$('renameList').addEventListener('click', async () => {
  const name = $('listName').value.trim();
  if (!name) return;
  const data = await load();
  active(data).name = name;
  await save(data); renderLists(data);
});
$('addItem').addEventListener('click', async () => {
  const title = $('itemInput').value.trim();
  if (!title) return;
  const data = await load();
  active(data).items.push({ title, done: false });
  $('itemInput').value = '';
  await save(data); renderItems(data);
});
$('filter').addEventListener('input', async () => renderItems(await load()));
$('seedWork').addEventListener('click', () => seed(WORK));
$('seedTravel').addEventListener('click', () => seed(TRAVEL));
$('clearDone').addEventListener('click', async () => {
  const data = await load();
  const list = active(data);
  list.items = list.items.filter(i => !i.done);
  await save(data); renderItems(data);
});
$('deleteList').addEventListener('click', async () => {
  const data = await load();
  if (data.lists.length === 1) { toast('Keep at least one list'); return; }
  if (!confirm('Delete this list?')) return;
  data.lists = data.lists.filter(l => l.id !== data.activeId);
  data.activeId = data.lists[0].id;
  await save(data); refresh();
});
$('copyList').addEventListener('click', async () => {
  const data = await load();
  const list = active(data);
  const lines = [
    list.name,
    ...list.items.map(i => `${i.done ? '[x]' : '[ ]'} ${i.title}`)
  ];
  try { await navigator.clipboard.writeText(lines.join('\n')); toast('List copied'); }
  catch { toast('Copy failed'); }
});

refresh();
