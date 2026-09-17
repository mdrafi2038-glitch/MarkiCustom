# Sales Tracker App

Android app for sales planning and product-wise sales calculations.

Features:
- Sales Planning Maker
- Product Wise Sales
- Product Setup

## Automatic test-build pipeline

The `sales-tracker-app` branch builds the APK automatically with GitHub Actions on every push. The workflow also has an optional Firebase App Distribution publishing step.

For Firebase publishing, add these GitHub Actions repository secrets:

- `FIREBASE_APP_ID` — Firebase Android App ID for package `com.example.salestracker`
- `FIREBASE_TOKEN` — Firebase CI authentication token
- `FIREBASE_TESTERS` — comma-separated tester email address(es)

When those three secrets are present, every successful push builds the APK and publishes it to Firebase App Distribution automatically. Without the secrets, the normal GitHub APK artifact is still produced.

The Android build version code can be supplied by CI using `VERSION_CODE`, so successive test builds can be given increasing Android version codes.
