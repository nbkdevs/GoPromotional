#!/usr/bin/env bash
# Cross-language CLI parity check: runs the same commands through the npm, PyPI, RubyGems and crates CLIs
# and diffs the output against the npm one. Needs node, python3, ruby and a `cargo build` in each crates/ dir.
# Usage: packages/tools/parity.sh
cd "$(dirname "$0")/.."
T=$(mktemp -d); export GOPROMOTIONAL_SIPLOG_FILE=$T/s.json GOPROMOTIONAL_MUGDAY_FILE=$T/m.json
declare -A PY=([homepage]=gopromotional_event_planner [water-bottles]=gopromotional_siplog [tote-bags]=gopromotional_packlist [travel-mugs]=gopromotional_mugday)
declare -A BIN=([homepage]=gopromotional-event-planner [water-bottles]=gopromotional-siplog [tote-bags]=gopromotional-packlist [travel-mugs]=gopromotional-mugday)
cmds_homepage=("countdown 2026-12-03 --today 2026-10-19" "timeline 2026-12-03 --today 2026-10-19" "giveaways --attendees 250 --uptake 0.35" "order-by 2026-10-26 --today 2026-10-19" "countdown 2026-02-30")
cmds_water=("goal --weight 70 --activity 30 --hot" "add 500ml --date 2026-10-19" "add 16oz --date 2026-10-19" "set-goal 2.5l" "status --date 2026-10-19" "week --date 2026-10-19" "undo --date 2026-10-19 --json" "add lots")
cmds_tote=("templates" "new --template work-day,travel --nights 2 --extra Umbrella --title Trip --out $T/t.md" "check $T/t.md passport" "add $T/t.md Snacks" "status $T/t.md" "status $T/t.md --json" "check $T/t.md e")
cmds_mug=("drinks" "add flat white --time 2026-10-19T08:00 --reusable" "add tea --time 2026-10-19T11:00 --reusable" "add filter-coffee --time 2026-10-19T14:00" "today --date 2026-10-19 --limit 200" "in-system --at 2026-10-19T22:00" "sleep-ready --threshold 100" "cups --date 2026-10-19" "cups --date 2026-10-19 --json" "undo" "add unicorn")
fail=0
run_all() { p=$1; shift; for impl in js py rb rs; do rm -f $T/s.json $T/m.json $T/t.md; : > $T/out.$impl
  for c in "$@"; do case $impl in
   js) node $p/npm/bin/cli.js $c;; py) PYTHONPATH=$p/pypi/src python3 -m ${PY[$p]}.cli $c;; rb) ruby $p/rubygems/exe/${BIN[$p]} $c;; rs) $p/crates/target/debug/${BIN[$p]} $c;; esac >> $T/out.$impl 2>&1; echo "--exit $?" >> $T/out.$impl; done; done
  for impl in py rb rs; do if diff -q $T/out.js $T/out.$impl >/dev/null; then echo "$p js==$impl"; else echo "$p js!=$impl"; fail=1; diff $T/out.js $T/out.$impl | head -${DIFFN:-12}; fi; done; }
run_all homepage "${cmds_homepage[@]}"; run_all water-bottles "${cmds_water[@]}"; run_all tote-bags "${cmds_tote[@]}"; run_all travel-mugs "${cmds_mug[@]}"
rm -rf $T; exit $fail
