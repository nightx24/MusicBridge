# MusicBridge

A free, community-focused Android app for backing up, importing, exporting, and eventually transferring music playlists between supported streaming services.

## Current status

The initial Android app is being built. The first milestone focuses on local playlist management and portable JSON/CSV backup files. Streaming-service transfers are not yet implemented and will require authorized API access, service-specific developer credentials, and compliance with each provider's policies.

## Planned features

- Manage a local playlist and track list
- Export playlist data to JSON or CSV
- Import MusicBridge JSON backups
- Clear feedback for invalid files and import/export errors
- Future service adapters for Spotify, YouTube Music, and other services where official APIs permit the required operations
- GitHub Actions debug APK builds

## Build

Open this project in Android Studio with Android SDK 35 installed, or use the GitHub Actions workflow to build a debug APK.

## Privacy

The initial version stores its local playlist on-device. Do not enter streaming-service passwords into this app. Future account integrations should use provider-approved authorization flows and store tokens securely.

## License

MIT. See [LICENSE](LICENSE).
