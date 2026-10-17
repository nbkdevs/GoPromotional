# Event Planner & Themes

An offline Android app for planning events: agendas, checklists, theme ideas, budgets, and merchandise lists. No account and no internet connection required.

## Features

- **Events** — create and manage event details (type, date, time, venue, attendees, notes)
- **Checklist** — tasks with priority, category, due date, and a built-in starter list
- **Themes** — curated offline theme library with décor, invitation, signage, activity, and merchandise ideas; save favorites
- **Budget** — total budget plus estimated and actual expenses with remaining balance
- **Merchandise** — plan quantities, unit costs, totals, and status for giveaways and useful items

All data is stored locally with Room and DataStore.

If you are also sourcing physical giveaways for an event, a useful starting point for [branded merchandise](https://www.gopromotional.co.uk) is linked from the Merchandise section of the app. The app itself does not require that site.

## Requirements

- JDK 17+
- Android SDK 36
- Android device or emulator (API 26+)

## Build

```bash
cd homepage/android-event-planner-themes
./gradlew assembleDebug
```

Debug APK:

`app/build/outputs/apk/debug/app-debug.apk`

## Release signing

1. Create a keystore (once):

```bash
keytool -genkeypair -v \
  -keystore keystore/event-planner-release.jks \
  -alias eventplanner \
  -keyalg RSA -keysize 2048 -validity 10000
```

2. Copy `keystore.properties.example` to `keystore.properties` and fill in values, **or** export:

- `EPT_STORE_FILE`
- `EPT_STORE_PASSWORD`
- `EPT_KEY_ALIAS`
- `EPT_KEY_PASSWORD`

3. Build:

```bash
./gradlew assembleRelease bundleRelease
```

Outputs:

- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/bundle/release/app-release.aab`

Never commit `keystore.properties`, `*.jks`, or passwords.

## Project structure

```
app/src/main/java/uk/co/softrank/eventplanner/
  data/          Room entities, DAOs, repository, theme library
  ui/            Compose screens and navigation
  viewmodel/     Shared app state
  util/          Constants and formatters
branding/        512 / 128 / 114 icons and Play Store icon
docs/            Privacy policy and Play Store draft
```

## Privacy

See [docs/PRIVACY.md](docs/PRIVACY.md). The app does not require analytics, accounts, or network access for core features.

## License

MIT — see [LICENSE](LICENSE).
