use gopromotional_packlist::*;
use std::process::ExitCode;

const HELP: &str = "gopromotional-packlist: packing checklists you can keep in a Markdown file

Usage:
  gopromotional-packlist templates
  gopromotional-packlist new [--template travel[,work-day,...]] [--nights N] [--extra \"Item\"]... [--title T] [--out FILE]
  gopromotional-packlist check <FILE> <item>      mark packed (unique partial names work)
  gopromotional-packlist uncheck <FILE> <item>
  gopromotional-packlist add <FILE> <item>
  gopromotional-packlist status <FILE>            progress and what is still left
Options: --json, -h/--help";

fn run(args: Vec<String>) -> Result<String, String> {
    let (mut pos, mut extras) = (Vec::new(), Vec::new());
    let (mut template_arg, mut nights, mut title, mut out, mut json) = ("travel".to_string(), 0u32, "Packing list".to_string(), None, false);
    let mut it = args.into_iter();
    while let Some(a) = it.next() {
        let mut val = || it.next().ok_or(format!("{a} needs a value"));
        match a.as_str() {
            "--json" => json = true,
            "-h" | "--help" => return Ok(HELP.into()),
            "-V" | "--version" => return Ok(env!("CARGO_PKG_VERSION").into()),
            "--template" => template_arg = val()?,
            "--nights" => nights = val()?.parse().map_err(|_| "--nights must be a whole number")?,
            "--extra" => extras.push(val()?),
            "--title" => title = val()?,
            "--out" => out = Some(val()?),
            _ => pos.push(a),
        }
    }
    let Some(cmd) = pos.first().cloned() else { return Ok(HELP.into()) };
    let read = |f: &str| std::fs::read_to_string(f).map(|s| parse_markdown(&s)).map_err(|e| format!("{f}: {e}"));
    match cmd.as_str() {
        "templates" => Ok(if json { serde_json::to_string_pretty(&TEMPLATES.iter().map(|(n, t)| (n.to_string(), t.to_vec())).collect::<std::collections::BTreeMap<_, _>>()).unwrap() }
            else { TEMPLATES.iter().map(|(n, t)| format!("{n} ({} items)", t.len())).collect::<Vec<_>>().join("\n") }),
        "new" => {
            let names: Vec<&str> = template_arg.split(',').map(str::trim).filter(|s| !s.is_empty()).collect();
            let ex: Vec<&str> = extras.iter().map(String::as_str).collect();
            let items = build_list(&names, nights, &ex)?;
            let md = to_markdown(&items, &title);
            match out {
                Some(f) => { std::fs::write(&f, &md).map_err(|e| e.to_string())?; Ok(format!("Wrote {} items to {f}", items.len())) }
                None => Ok(if json { serde_json::to_string_pretty(&items).unwrap() } else { md }),
            }
        }
        "check" | "uncheck" | "add" => {
            let file = pos.get(1).ok_or(format!("{cmd} needs a file and an item name"))?.clone();
            let name = pos[2..].join(" ");
            if name.is_empty() { return Err(format!("{cmd} needs a file and an item name")); }
            let (title, mut items) = read(&file)?;
            if cmd == "add" {
                if items.iter().any(|i| i.name.to_lowercase() == name.to_lowercase()) { return Err(format!("\"{name}\" is already on the list")); }
                items.push(Item::new(&name, "Extras"));
            } else { mark(&mut items, &name, Some(cmd == "check"))?; }
            std::fs::write(&file, to_markdown(&items, &title)).map_err(|e| e.to_string())?;
            let p = progress(&items);
            Ok(format!("{}/{} packed ({}%)", p.packed, p.total, p.percent))
        }
        "status" => {
            let (title, items) = read(pos.get(1).ok_or("status needs a file")?)?;
            let (p, left) = (progress(&items), remaining(&items));
            Ok(if json { serde_json::to_string_pretty(&serde_json::json!({"packed": p.packed, "total": p.total, "percent": p.percent, "remaining": left})).unwrap() }
               else if left.is_empty() { format!("{title}: {}/{} packed ({}%)\nAll packed.", p.packed, p.total, p.percent) }
               else { format!("{title}: {}/{} packed ({}%)\nStill to pack: {}", p.packed, p.total, p.percent, left.iter().map(|i| i.name.as_str()).collect::<Vec<_>>().join(", ")) })
        }
        other => Err(format!("Unknown command \"{other}\". Run with --help.")),
    }
}

fn main() -> ExitCode {
    match run(std::env::args().skip(1).collect()) {
        Ok(s) => { println!("{s}"); ExitCode::SUCCESS }
        Err(e) => { eprintln!("Error: {e}"); ExitCode::from(2) }
    }
}
