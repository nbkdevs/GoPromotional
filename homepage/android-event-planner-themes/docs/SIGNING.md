# Release signing

Sensitive credentials must stay off Git.

## Option A — local properties file

```bash
cp keystore.properties.example keystore.properties
# edit keystore.properties
```

`keystore.properties` is gitignored.

## Option B — environment variables

```bash
export EPT_STORE_FILE=keystore/event-planner-release.jks
export EPT_STORE_PASSWORD='...'
export EPT_KEY_ALIAS=eventplanner
export EPT_KEY_PASSWORD='...'
```

## Create a keystore

```bash
mkdir -p keystore
keytool -genkeypair -v \
  -keystore keystore/event-planner-release.jks \
  -alias eventplanner \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass "$EPT_STORE_PASSWORD" \
  -keypass "$EPT_KEY_PASSWORD" \
  -dname "CN=Event Planner Themes, OU=Mobile, O=Softrank, L=London, C=GB"
```

## Verify release APK is signed

```bash
./gradlew assembleRelease
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```
