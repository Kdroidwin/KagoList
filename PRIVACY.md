# Privacy

KagoList does not collect or send personal data. The app has no account, network access, advertising, analytics, or third-party tracking.

Shopping list and staple item payloads are stored in the app's private storage and encrypted with AES-GCM. The encryption key is held by Android Keystore. Theme and navigation-label preferences are stored locally. Android backup and device-to-device transfer are disabled.

When you explicitly export or share a list, KagoList creates a plain JSON file. You can select a purchase destination before exporting or sharing a shopping list. The Android document picker lets you choose where to save it; the Android Sharesheet lets you choose an app or recipient. KagoList grants temporary read access to only that shared file. The recipient may retain or forward the file.

Use **マイページ → この端末のデータを削除** to erase saved list data and remove the encryption key. Uninstalling KagoList also removes its app data and key.
