# iamnazzal/gopromotional-siplog

A small hydration tracker: estimate a daily water goal from weight, exercise and weather, log drinks in ml, cl, litres or US fl oz, and see today's progress, the last 7 days, your average and your streak. The log is a plain JSON file.

- **Daily goal** estimate: weight × 33 ml + 12 ml per active minute (+500 ml in hot weather), rounded to 50 ml. A general guide, not medical advice.
- **Flexible amounts**: `250`, `500ml`, `33cl`, `0.5l`, `16oz`.
- **Progress**, **7-day history**, **average** and **streak** of days on target.
- **Undo** the last entry, **set** your own goal.
- Plain **JSON log** (`{goal, entries:[{date, ml}]}`), the same format in every language version.

A small image (`node:20-alpine`, no dependencies, runs as the unprivileged `node` user) with the `gopromotional-siplog` CLI as its entrypoint.

## Usage

```sh
docker run --rm iamnazzal/gopromotional-siplog --help
docker run --rm -v "$PWD:/data" --user "$(id -u):$(id -g)" iamnazzal/gopromotional-siplog add 500ml
```

The working directory is `/data`; mount a host folder there to keep your log between runs.

```console
$ gopromotional-siplog goal --weight 70 --activity 30 --hot
Suggested daily water goal: 3150 ml (a general guide, not medical advice).

$ gopromotional-siplog set-goal 2.5l
Daily goal set to 2500 ml.

$ gopromotional-siplog add 500ml --date 2026-10-19
Added 500 ml. 500/2500 ml (20%).

$ gopromotional-siplog add 16oz --date 2026-10-19
Added 473 ml. 973/2500 ml (39%).

$ gopromotional-siplog status --date 2026-10-19
2026-10-19: 973/2500 ml [########............] 39% - 1527 ml to go
Streak: 0 day(s)
```

## Build

```sh
cd packages/water-bottles
docker build -f docker/Dockerfile -t iamnazzal/gopromotional-siplog:1.0.0 -t iamnazzal/gopromotional-siplog:latest .
```

Image labels follow the OCI spec: `org.opencontainers.image.url` points to the project page and `org.opencontainers.image.source` to the repository.

## Links

- Website: [branded water bottles](https://www.gopromotional.co.uk/branded-products/branded-water-bottles)
- Source code: [https://github.com/nbkdevs/GoPromotional](https://github.com/nbkdevs/GoPromotional) (`packages/water-bottles/docker`)
- Licence: MIT
