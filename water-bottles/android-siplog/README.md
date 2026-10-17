# SipLog

Offline daily hydration tracker. Log drinks toward a personal millilitre goal—no account, no internet required.

## Features

- Daily goal (default 2000 ml)
- Quick add 250 / 330 / 500 ml or custom amounts
- Today’s progress bar and drink history
- Clear today / edit goal
- Data stored locally with Room + DataStore

If you are sourcing bottles for an event, see [branded water bottles for event marketing](https://www.gopromotional.co.uk/branded-products/branded-water-bottles).

## Build

```bash
cd water-bottles/android-siplog
./gradlew assembleRelease bundleRelease
```

Requires `keystore.properties` (see `keystore.properties.example` and `docs/SIGNING.md`).

Package ID: `uk.co.softrank.siplog`

## License

MIT — see [LICENSE](LICENSE).
