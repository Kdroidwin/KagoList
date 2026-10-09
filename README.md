# KagoList

KagoList is a small, local-first Android shopping list app built with Kotlin and Jetpack Compose. It is released under the MIT License.

## Privacy

- Shopping data stays in the app's private storage on the device; list payloads are encrypted with AES-GCM using a key held by Android Keystore.
- The app requests no network permissions and includes no account, ads, analytics, or trackers.
- Android cloud backup and device transfer are disabled for app data.
- Use **設定 → この端末のデータを削除** to erase the saved lists and reset preferences. The app also removes its Keystore key.

Sharing and file export are explicit actions. Uninstalling the app removes its local data.

## Build

Open this directory in Android Studio or run `./gradlew assembleDebug` with JDK 17 and Android SDK 36.

## Features

- Current shopping list grouped by store, with a purchased section
- Reusable staples that can be added to the current list
- Quantity controls (1–999) on shopping list and staple entries
- Light, dark, and true black AMOLED themes
- Custom store labels that can be added, removed, and reordered
- An editable hex accent color and optional add-button labels
- Add-item sheets that follow the active theme and accent color
- Optional labels in the bottom navigation
- JSON backup/restore plus separate shopping-list and staples export/share
- Purchase-destination selection when exporting or sharing a shopping list

Use the Android document picker to save or import JSON files. Sharing opens the Android Sharesheet and sends only the selected list file. Exported JSON is plain text; the on-device copy remains encrypted. See [PRIVACY.md](PRIVACY.md) for details.

Release signing is configured with environment variables so signing credentials stay out of the source archive: `KAGO_RELEASE_STORE_FILE`, `KAGO_RELEASE_STORE_PASSWORD`, `KAGO_RELEASE_KEY_ALIAS`, and `KAGO_RELEASE_KEY_PASSWORD`.
