# MusicBridge account connection setup

This guide prepares the project for real account connections. **Account login and playlist transfer are not implemented yet.** Adding client IDs alone does not make transfers work; the OAuth flows, token storage, provider API clients, track review, and destination playlist writes still need to be implemented and tested.

## Integration approach

- **Spotify:** use Spotify's Authorization Code with PKCE flow for a public Android app. Do not embed a Spotify client secret in the APK. Request only the scopes needed for the user's chosen transfer actions.
- **YouTube:** use Google's supported OAuth authorization for Android and the YouTube Data API for playlist operations that the API supports. The app must not ask for a Google password. YouTube Music-specific library features may not all be available through the official YouTube API, so they must be described honestly rather than promised.
- **Transfer safety:** read the source playlist, match tracks, show the user a review/preview, and only then create or modify the destination playlist. Keep the source unchanged. Handle missing tracks and API errors without silently skipping them.
- **Privacy:** tokens must be stored securely, never logged, and removed when the user disconnects. Never commit client secrets, refresh tokens, access tokens, or credential files.

## 1. Create a Spotify app

1. Open the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard).
2. Create an app for MusicBridge and review Spotify's current Web API terms and development-mode restrictions.
3. Copy the **Client ID**. Do not copy or embed a client secret in this Android app.
4. Register the exact redirect URI used by the Android OAuth implementation. The implementation must use the same URI and PKCE verifier throughout the authorization-code exchange.

Add the client ID to your local project's root `local.properties` file:

```properties
SPOTIFY_CLIENT_ID=your_spotify_client_id
```

The Gradle build also reads an environment variable with the same name. Do not commit `local.properties`.

## 2. Configure Google / YouTube access

1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Create or select a project and enable the [YouTube Data API v3](https://console.cloud.google.com/apis/library/youtube.googleapis.com).
3. Configure the OAuth consent screen and the required test users if the app remains in testing.
4. Create the appropriate Android OAuth client using MusicBridge's package name and the SHA-1 signing certificate for the build you install. Use the matching Google-supported Android authorization flow; do not assume a generic web client ID can be used as an Android client ID.
5. Review the current YouTube API policies and quota limits before distributing the app.

For the current Gradle configuration, the optional local property is:

```properties
YOUTUBE_OAUTH_CLIENT_ID=your_google_oauth_client_id
```

Use the client ID appropriate to the eventual Google authorization implementation. **Do not put a client secret in this file or in the APK.** This property is configuration only; it does not mean Google sign-in is already wired up.

## 3. Build configuration status

The project now has the Android internet permission and Gradle fields for local client-ID configuration. No account sign-in UI, OAuth callback, secure token store, Spotify API client, YouTube API client, or real transfer operation has been completed by this setup change.

## 4. Recommended implementation order

1. Implement Spotify PKCE sign-in, callback validation, token expiry handling, and secure token storage.
2. Read the user's Spotify playlists and tracks using the granted scopes.
3. Implement Google authorization and read/write only the YouTube playlists supported by the official API.
4. Add matching review, a transfer preview, destination playlist creation, progress, retry, and unmatched-track reports.
5. Add disconnect-account controls and verify that tokens are removed.
6. Test the complete flow with test accounts and confirm that a real playlist transfers before advertising the feature as available.

## Reference repositories

These projects can inform design and UI, but they are not drop-in account-transfer modules:

- [BitChord](https://github.com/kushagrasinghx/BitChord): a YouTube Music-oriented player with account-related integrations. Its README identifies GPL-3.0; copying code may require MusicBridge to comply with that license.
- [Echo Music](https://github.com/EchoMusicApp/Echo-Music): a YouTube Music-focused player. Review its current license and each integration's implementation before reuse.
- [Spotube](https://github.com/team-spotube/spotube): a cross-platform, plugin-oriented music client. Its architecture may be useful for service adapters, but its playback/source model is not itself a playlist migration engine.

Before copying any code, check the current license, source file headers, dependencies, API terms, and authentication mechanism. Prefer adapting architecture and independently implementing provider-compliant transfer flows over copying large sections of code.
