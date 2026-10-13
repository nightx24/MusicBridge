# MusicBridge

[![Android Debug APK](https://github.com/nightx24/MusicBridge/actions/workflows/android.yml/badge.svg?branch=main)](https://github.com/nightx24/MusicBridge/actions/workflows/android.yml)

**MusicBridge is planned as a free, open-source Android music-library transfer and management app.** Its goal is to bring together the kinds of playlist transfer, synchronization, backup, import/export, and playlist-management tools offered by FreeYourMusic, Soundiiz, and TuneMyMusic.

The long-term goal is broad feature coverage—not a promise that every service or operation will be available immediately. Each feature depends on the destination service's supported APIs, authorization rules, rate limits, and terms.

## Current status

The current first version is a starter Android app for local track-list management and JSON/CSV backup. **Real streaming-service connections, transfers, synchronization, and the advanced tools below are not implemented yet.** The project will be developed in stages and tested as features are added.

## Product goals

### 1. Transfer between music services

- Choose a source and destination service.
- Connect accounts using authorized sign-in/OAuth flows where supported.
- Transfer playlists and, where both services allow it, liked/favorite tracks, saved albums, and followed artists.
- Select individual playlists or items, or transfer a larger part of a library.
- Preserve playlist title, order, and available metadata.
- Create a new destination playlist or add tracks to an existing playlist.
- Preview the transfer before writing changes.
- Show progress, successes, failures, duplicates, and unmatched tracks.
- Retry failed items and manually rematch uncertain tracks.
- Never delete or modify the source library during a normal transfer.

### 2. Track matching

- Prefer stable recording identifiers such as ISRC when available.
- Fall back to title, artist, album, duration, and version/remix/live-edition checks.
- Distinguish clean matches from uncertain matches.
- Let the user review or replace a match before transfer.
- Export unmatched tracks and transfer reports to CSV.

### 3. Playlist management

- Create, rename, clone, and edit playlists where the service permits it.
- Merge playlists, join tracklists, or replace a destination playlist's tracklist with clear confirmation.
- Split large playlists when destination limits require it.
- Sort tracks by title, artist, album, or other available metadata.
- Shuffle track order.
- Detect and review duplicate tracks.
- Add, remove, and reorder tracks where supported.
- Make destructive actions explicit and provide a preview/confirmation step.

### 4. Synchronization

- Configure a one-way sync from a source playlist to a destination playlist.
- Choose an add-only or replace-style sync mode when supported.
- Configure sync frequency, such as manual, daily, or weekly, subject to platform and hosting limits.
- Display last-run time, changes, errors, and sync history.
- Avoid accidental deletion; any mode that removes destination tracks must be clearly explained and confirmed.
- Do not claim bidirectional sync unless it is implemented and tested separately.

### 5. Import, export, and backup

- MusicBridge JSON backup and CSV export.
- Import/export common playlist formats where practical: M3U, M3U8, PLS, WPL, XSPF, XML, TXT, and CSV.
- Import from supported playlist URLs and pasted track lists.
- Export playlists and transfer reports for offline use.
- Validate imports, report skipped rows, and avoid silently losing data.
- Keep backups usable without a paid account.

### 6. Discovery and sharing

- Create shareable cross-service links for tracks, albums, artists, and playlists where matching URLs can be resolved.
- Optional playlist-generation tools based on a user's prompt or selected music preferences, if a suitable free/local or transparently costed implementation is available.
- Do not include copyrighted audio downloads or redistribute music files; transfers move playlist/library metadata, not audio.

### 7. Service coverage

The initial service priorities are **Spotify and YouTube Music**, followed by additional services based on API access and reliability. Candidates to evaluate include Apple Music, TIDAL, Deezer, Amazon Music, SoundCloud, Qobuz, YouTube, Audiomack, Audius, Boomplay, JioSaavn, Pandora, and other services with feasible authorized integration paths.

A compatibility matrix will be maintained per service and per operation (read playlists, write playlists, read liked songs, write liked songs, albums, artists, sync, and other features). A service must not be advertised as fully supported until the relevant operations are implemented and tested.

## Free and open-source principles

- No required paid subscription for core transfer, management, and backup features.
- Prefer local processing and user-controlled backups.
- Clearly disclose any third-party service limits, API restrictions, or optional operating costs.
- Never ask users to paste their music-service passwords into MusicBridge.
- Use provider-approved authorization, securely store tokens, and provide a way to disconnect accounts and clear stored data.
- Do not bypass paywalls, access controls, or provider restrictions.

## Development roadmap

1. **Foundation:** finish and verify the Android build; improve local playlist management and robust JSON/CSV import/export.
2. **First real transfer:** add a supported Spotify connection, playlist reading, matching, preview, and a tested destination write path.
3. **Second service:** implement the YouTube Music path only through a reliable, permitted integration approach; document any limitations.
4. **Transfer quality:** unmatched-track review, retries, progress reporting, duplicate detection, and CSV reports.
5. **Format support:** expand playlist file imports/exports and backup/restore.
6. **Management tools:** merge, join, clone, split, sort, shuffle, and safe replace operations.
7. **Synchronization:** add scheduled or manual one-way playlist sync where the app's architecture and provider rules allow it.
8. **More services and sharing:** add integrations incrementally; evaluate smartlinks and playlist-generation features after core transfers are stable.

This roadmap is aspirational. Each milestone needs implementation, build verification, and tests before it can be described as complete.

## Build and account integration setup

Open this project in Android Studio with Android SDK 35 installed, or use the GitHub Actions workflow to build a debug APK. For the account-connection plan and provider configuration requirements, see [INTEGRATION_SETUP.md](INTEGRATION_SETUP.md). The current app still does not connect accounts or transfer playlists.

## Privacy

The current starter version stores its local playlist on-device. Do not enter streaming-service passwords into this app. Future account integrations must use provider-approved authorization flows and securely handle tokens.

## Reference projects and products

Feature inspiration: [FreeYourMusic](https://freeyourmusic.com/), [Soundiiz](https://soundiiz.com/), and [TuneMyMusic](https://www.tunemymusic.com/).

Open-source technical references to evaluate:

- [mahdi-y/Spotify2YoutubeMusic](https://github.com/mahdi-y/Spotify2YoutubeMusic) — playlist and liked-song migration, track-match caching, progress reporting, and resume behavior.
- [loading-pk/Spotify-to-Youtube-Music](https://github.com/loading-pk/Spotify-to-Youtube-Music) — additional Spotify-to-YouTube Music implementation to inspect before adapting.
- [NoxbaneSudo/Migratify](https://github.com/NoxbaneSudo/Migratify) — CSV imports, duration-aware matching, failed-track reports, and resumable migration.
- [linsomniac/spotify_to_ytmusic](https://github.com/linsomniac/spotify_to_ytmusic) and [sigma67/spotify_to_ytmusic](https://github.com/sigma67/spotify_to_ytmusic) — focused Spotify playlist transfer references.
- [ahnafnafee/songmirror](https://github.com/ahnafnafee/songmirror) — broader multi-service transfer architecture and track matching.

These are reference projects, not integrated code. Before reusing any implementation, inspect the exact repository files, license, dependencies, authentication method, and current maintenance status. Prefer provider-approved authorization; unofficial YouTube Music interfaces may break and should not be treated as guaranteed stable.

## License

MIT. See [LICENSE](LICENSE).
