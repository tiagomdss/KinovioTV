# KinovioTV Android

KinovioTV is a Kotlin Android TV-first personal media hub and player: one polished place for metadata, connected sources, subtitles, watch progress, live TV, watchlists, and playback. It does not host, bundle, store, or ship movies, shows, live channels, or stream URLs. Users connect their own services, playlists, servers, files, and Stremio-compatible addon manifests.

## Build

Open `android-tv` in Android Studio, or run:

```powershell
.\gradlew.bat :app:assembleDebug
```

If Gradle is not on PATH yet, Android Studio can import the project directly and create/use the wrapper.

## Scope in this build

- TV-first Compose UI with remote-friendly focus states.
- Responsive layout for Android TV, tablets, and phones.
- Local profile/settings storage using app-private preferences.
- Add Stremio-compatible addons by manifest URL.
- Browse addon catalogs, open details, query stream resources, and play direct HTTP/HLS URLs.
- Fullscreen Media3 player with TV-safe controls, subtitle/audio entry points, source switching, and clear unsupported-source handling.
- Forward-inspired motion language: focused cards lift smoothly, source selection opens in an animated drawer, and detail surfaces use soft fade/scale transitions.
- Privacy-aware defaults: no bundled addons, no analytics, no cloud sync, and local secrets only.

## Roadmap Hooks

The code is structured so IPTV, Jellyfin, Emby, Plex, Trakt, Telegram lookup, cloud sync, and AI/TMDB matching can be added behind explicit user configuration without changing the core privacy model.
