//! # gopromotional-packlist
//!
//! Packing checklists: built-in templates (work day, travel, trade show, gym), per-night
//! quantities, merging without duplicates, progress, and a Markdown format that round-trips
//! so a checklist can live in a plain `.md` file.
//!
//! ```
//! use gopromotional_packlist::{build_list, mark, progress, to_markdown};
//! let mut items = build_list(&["travel"], 3, &[]).unwrap();
//! mark(&mut items, "passport", Some(true)).unwrap();
//! assert_eq!(progress(&items).packed, 1);
//! assert!(to_markdown(&items, "Trip").contains("- [ ] 4 × T-shirts"));
//! ```
use serde::Serialize;

/// A template row: name, category and whether the quantity scales per night.
#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize)]
pub struct TemplateItem { pub name: &'static str, pub category: &'static str, pub per_night: bool }

const fn ti(name: &'static str, category: &'static str, per_night: bool) -> TemplateItem { TemplateItem { name, category, per_night } }

pub const WORK_DAY: &[TemplateItem] = &[
    ti("Laptop", "Work", false), ti("Laptop charger", "Work", false), ti("Phone charger", "Work", false), ti("Badge / ID", "Work", false),
    ti("Notebook", "Work", false), ti("Pen", "Work", false), ti("Headphones", "Work", false), ti("Water bottle", "Work", false),
    ti("Reusable coffee cup", "Work", false), ti("Lunch / snacks", "Work", false),
];
pub const TRAVEL: &[TemplateItem] = &[
    ti("Passport / ID", "Documents", false), ti("Tickets / boarding pass", "Documents", false), ti("Bank cards", "Documents", false),
    ti("T-shirts", "Clothes", true), ti("Underwear", "Clothes", true), ti("Socks", "Clothes", true),
    ti("Trousers / skirt", "Clothes", false), ti("Jumper", "Clothes", false), ti("Sleepwear", "Clothes", false),
    ti("Toothbrush", "Toiletries", false), ti("Toothpaste", "Toiletries", false), ti("Deodorant", "Toiletries", false), ti("Medications", "Toiletries", false),
    ti("Phone charger", "Tech", false), ti("Power bank", "Tech", false), ti("Plug adapter", "Tech", false), ti("Headphones", "Tech", false),
    ti("Reusable water bottle", "Other", false), ti("Tote bag", "Other", false),
];
pub const TRADE_SHOW: &[TemplateItem] = &[
    ti("Pop-up banner / stand", "Stand", false), ti("Table cloth", "Stand", false), ti("Branded giveaways", "Stand", false),
    ti("Business cards", "Stand", false), ti("Brochures / flyers", "Stand", false), ti("Lead capture sheet or scanner", "Stand", false),
    ti("Extension lead", "Kit", false), ti("Laptop / tablet", "Kit", false), ti("Phone charger", "Kit", false), ti("Gaffer tape", "Kit", false),
    ti("Scissors", "Kit", false), ti("Cable ties", "Kit", false), ti("Pens", "Kit", false),
    ti("Water bottles", "Team", false), ti("Snacks", "Team", false), ti("First-aid kit", "Team", false),
];
pub const GYM: &[TemplateItem] = &[
    ti("Gym clothes", "Gym", false), ti("Trainers", "Gym", false), ti("Towel", "Gym", false), ti("Water bottle", "Gym", false), ti("Padlock", "Gym", false),
    ti("Headphones", "Gym", false), ti("Shower gel", "Gym", false), ti("Deodorant", "Gym", false), ti("Snack", "Gym", false),
];
/// All templates by name.
pub const TEMPLATES: &[(&str, &[TemplateItem])] = &[("work-day", WORK_DAY), ("travel", TRAVEL), ("trade-show", TRADE_SHOW), ("gym", GYM)];

pub fn template(name: &str) -> Option<&'static [TemplateItem]> { TEMPLATES.iter().find(|(n, _)| *n == name).map(|(_, t)| *t) }

/// A checklist item.
#[derive(Debug, Clone, PartialEq, Eq, Serialize)]
pub struct Item { pub name: String, pub category: String, pub qty: u32, pub packed: bool }
impl Item {
    pub fn new(name: &str, category: &str) -> Self { Self { name: name.trim().into(), category: category.into(), qty: 1, packed: false } }
    pub fn label(&self) -> String { if self.qty > 1 { format!("{} × {}", self.qty, self.name) } else { self.name.clone() } }
}

fn key(s: &str) -> String { s.trim().to_lowercase() }

/// Build a checklist. Per-night items get nights + 1 (one spare); duplicates are merged by name (first wins).
pub fn build_list(templates: &[&str], nights: u32, extras: &[&str]) -> Result<Vec<Item>, String> {
    if nights > 365 { return Err("nights must be an integer from 0 to 365".into()); }
    let mut items: Vec<Item> = Vec::new();
    let push = |it: Item, items: &mut Vec<Item>| { let k = key(&it.name); if !k.is_empty() && !items.iter().any(|i| key(&i.name) == k) { items.push(it); } };
    for t in templates {
        let tpl = template(t).ok_or_else(|| format!("Unknown template \"{t}\". Choose from: work-day, travel, trade-show, gym"))?;
        for r in tpl {
            let mut it = Item::new(r.name, r.category);
            if r.per_night && nights > 0 { it.qty = nights + 1; }
            push(it, &mut items);
        }
    }
    for e in extras { push(Item::new(e, "Extras"), &mut items); }
    Ok(items)
}

/// Index of an item: exact name first, then a unique substring.
pub fn find(items: &[Item], name: &str) -> Result<usize, String> {
    let k = key(name);
    if let Some(i) = items.iter().position(|i| key(&i.name) == k) { return Ok(i); }
    let partial: Vec<usize> = items.iter().enumerate().filter(|(_, i)| key(&i.name).contains(&k)).map(|(n, _)| n).collect();
    match partial.len() {
        1 => Ok(partial[0]),
        0 => Err(format!("No item matches \"{name}\"")),
        n => Err(format!("\"{name}\" matches {n} items: {}", partial.iter().map(|&i| items[i].name.as_str()).collect::<Vec<_>>().join(", "))),
    }
}

/// Mark an item packed; `None` toggles.
pub fn mark<'a>(items: &'a mut [Item], name: &str, packed: Option<bool>) -> Result<&'a Item, String> {
    let i = find(items, name)?;
    items[i].packed = packed.unwrap_or(!items[i].packed);
    Ok(&items[i])
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize)]
pub struct Progress { pub packed: usize, pub total: usize, pub percent: u32 }

pub fn progress(items: &[Item]) -> Progress {
    let packed = items.iter().filter(|i| i.packed).count();
    let percent = if items.is_empty() { 0 } else { (packed as f64 * 100.0 / items.len() as f64 + 0.5).floor() as u32 };
    Progress { packed, total: items.len(), percent }
}
pub fn remaining(items: &[Item]) -> Vec<&Item> { items.iter().filter(|i| !i.packed).collect() }
pub fn filter_items<'a>(items: &'a [Item], q: &str) -> Vec<&'a Item> {
    let q = key(q);
    items.iter().filter(|i| key(&i.name).contains(&q) || key(&i.category).contains(&q)).collect()
}
pub fn categories(items: &[Item]) -> Vec<String> {
    let mut out: Vec<String> = Vec::new();
    for i in items { if !out.contains(&i.category) { out.push(i.category.clone()); } }
    out
}

/// Markdown checkboxes grouped by category; [`parse_markdown`] reads it back.
pub fn to_markdown(items: &[Item], title: &str) -> String {
    let mut out = vec![format!("# {title}"), String::new()];
    for c in categories(items) {
        out.push(format!("## {c}"));
        for i in items.iter().filter(|i| i.category == c) { out.push(format!("- [{}] {}", if i.packed { "x" } else { " " }, i.label())); }
        out.push(String::new());
    }
    out.join("\n")
}

/// Parse a Markdown checklist into (title, items).
pub fn parse_markdown(md: &str) -> (String, Vec<Item>) {
    let (mut title, mut category, mut items) = ("Packing list".to_string(), "Items".to_string(), Vec::new());
    for raw in md.lines() {
        let line = raw.trim();
        if let Some(rest) = line.strip_prefix("## ") { category = rest.trim().into(); continue; }
        if let Some(rest) = line.strip_prefix("# ") { title = rest.trim().into(); continue; }
        let body = line.strip_prefix("- ").or_else(|| line.strip_prefix("* "));
        if let Some(b) = body {
            let packed = if b.starts_with("[ ]") { false } else if b.starts_with("[x]") || b.starts_with("[X]") { true } else { continue };
            let mut name = b[3..].trim().to_string();
            if name.is_empty() { continue; }
            let mut qty = 1;
            let digits: String = name.chars().take_while(|c| c.is_ascii_digit()).collect();
            if !digits.is_empty() {
                let rest = name[digits.len()..].trim_start();
                let after = rest.strip_prefix('×').or_else(|| rest.strip_prefix('x'));
                if let Some(a) = after { if a.starts_with(char::is_whitespace) { qty = digits.parse().unwrap_or(1); name = a.trim().to_string(); } }
            }
            items.push(Item { name, category: category.clone(), qty, packed });
        }
    }
    (title, items)
}
