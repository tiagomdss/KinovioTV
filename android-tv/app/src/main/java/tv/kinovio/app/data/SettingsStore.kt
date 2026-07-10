package tv.kinovio.app.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tv.kinovio.app.stremio.InstalledAddon
import tv.kinovio.app.stremio.StremioMetaPreview

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("kinovio_settings", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    fun loadAddons(): List<InstalledAddon> {
        val raw = prefs.getString(KEY_ADDONS, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<InstalledAddon>>(raw) }.getOrDefault(emptyList())
    }

    fun saveAddons(addons: List<InstalledAddon>) {
        prefs.edit().putString(KEY_ADDONS, json.encodeToString(addons)).apply()
    }

    fun loadProfileName(): String = prefs.getString(KEY_PROFILE, "Casa") ?: "Casa"

    fun saveProfileName(name: String) {
        prefs.edit().putString(KEY_PROFILE, name.ifBlank { "Casa" }).apply()
    }

    fun loadIptvUrl(): String = prefs.getString(KEY_IPTV_URL, "") ?: ""

    fun saveIptvUrl(url: String) {
        prefs.edit().putString(KEY_IPTV_URL, url.trim()).apply()
    }

    fun loadServerUrl(): String = prefs.getString(KEY_SERVER_URL, "") ?: ""

    fun saveServerUrl(url: String) {
        prefs.edit().putString(KEY_SERVER_URL, url.trim()).apply()
    }

    fun loadTraktName(): String = prefs.getString(KEY_TRAKT_NAME, "") ?: ""

    fun saveTraktName(name: String) {
        prefs.edit().putString(KEY_TRAKT_NAME, name.trim()).apply()
    }

    fun loadAutoPlayBestSource(): Boolean = prefs.getBoolean(KEY_AUTO_PLAY_BEST_SOURCE, false)

    fun saveAutoPlayBestSource(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_PLAY_BEST_SOURCE, enabled).apply()
    }

    fun loadMinQuality(): String = prefs.getString(KEY_MIN_QUALITY, "Any") ?: "Any"

    fun saveMinQuality(value: String) {
        prefs.edit().putString(KEY_MIN_QUALITY, value.ifBlank { "Any" }).apply()
    }

    fun loadPreferredSubtitle(): String = prefs.getString(KEY_PREFERRED_SUBTITLE, "") ?: ""

    fun savePreferredSubtitle(value: String) {
        prefs.edit().putString(KEY_PREFERRED_SUBTITLE, value.trim()).apply()
    }

    fun loadWatchlist(): List<StremioMetaPreview> {
        val raw = prefs.getString(KEY_WATCHLIST, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<StremioMetaPreview>>(raw) }.getOrDefault(emptyList())
    }

    fun saveWatchlist(items: List<StremioMetaPreview>) {
        prefs.edit().putString(KEY_WATCHLIST, json.encodeToString(items.distinctBy { it.id })).apply()
    }

    fun loadContinueWatching(): List<StremioMetaPreview> {
        val raw = prefs.getString(KEY_CONTINUE, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<StremioMetaPreview>>(raw) }.getOrDefault(emptyList())
    }

    fun saveContinueWatching(items: List<StremioMetaPreview>) {
        prefs.edit().putString(KEY_CONTINUE, json.encodeToString(items.distinctBy { it.id }.take(30))).apply()
    }

    fun clearLocalLibrary() {
        prefs.edit().remove(KEY_WATCHLIST).remove(KEY_CONTINUE).apply()
    }

    fun saveLastCrash(message: String) {
        prefs.edit().putString(KEY_LAST_CRASH, message.take(1600)).apply()
    }

    fun loadLastCrash(): String = prefs.getString(KEY_LAST_CRASH, "") ?: ""

    fun clearLastCrash() {
        prefs.edit().remove(KEY_LAST_CRASH).apply()
    }

    private companion object {
        const val KEY_ADDONS = "addons"
        const val KEY_PROFILE = "profile_name"
        const val KEY_IPTV_URL = "iptv_url"
        const val KEY_SERVER_URL = "server_url"
        const val KEY_TRAKT_NAME = "trakt_name"
        const val KEY_AUTO_PLAY_BEST_SOURCE = "auto_play_best_source"
        const val KEY_MIN_QUALITY = "min_quality"
        const val KEY_PREFERRED_SUBTITLE = "preferred_subtitle"
        const val KEY_WATCHLIST = "watchlist"
        const val KEY_CONTINUE = "continue_watching"
        const val KEY_LAST_CRASH = "last_crash"
    }
}
