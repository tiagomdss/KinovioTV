package tv.kinovio.app.stremio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class StremioClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    suspend fun install(manifestUrl: String): InstalledAddon = withContext(Dispatchers.IO) {
        val cleanUrl = normalizeManifestUrl(manifestUrl)
        val manifest = getJson<StremioManifest>(cleanUrl)
        InstalledAddon(cleanUrl, manifest)
    }

    suspend fun catalog(addon: InstalledAddon, catalog: StremioCatalog, search: String? = null): List<StremioMetaPreview> {
        val base = addon.manifestUrl.removeSuffix("/manifest.json")
        val suffix = if (search.isNullOrBlank()) {
            ""
        } else {
            "/search=${URLEncoder.encode(search.trim(), "UTF-8")}"
        }
        return withContext(Dispatchers.IO) {
            getJson<CatalogResponse>("$base/catalog/${catalog.type}/${catalog.id}$suffix.json").metas
        }
    }

    suspend fun meta(addon: InstalledAddon, type: String, id: String): StremioMeta? {
        val base = addon.manifestUrl.removeSuffix("/manifest.json")
        return withContext(Dispatchers.IO) {
            getJson<MetaResponse>("$base/meta/$type/${id.encodePath()}.json").meta
        }
    }

    suspend fun streams(addon: InstalledAddon, type: String, id: String): List<StremioStream> {
        val base = addon.manifestUrl.removeSuffix("/manifest.json")
        return withContext(Dispatchers.IO) {
            getJson<StreamResponse>("$base/stream/$type/${id.encodePath()}.json").streams
        }
    }

    private inline fun <reified T> getJson(url: String): T {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "KinovioTV/0.1 Android")
            .build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}: ${response.message}")
            val body = response.body.string()
            return json.decodeFromString<T>(body)
        }
    }

    private fun normalizeManifestUrl(input: String): String {
        val trimmed = input.trim()
        val https = when {
            trimmed.startsWith("stremio://") -> trimmed.replaceFirst("stremio://", "https://")
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            else -> "https://$trimmed"
        }
        return if (https.endsWith("/manifest.json")) https else "${https.trimEnd('/')}/manifest.json"
    }

    private fun String.encodePath(): String =
        split("/").joinToString("/") {
            URLEncoder.encode(it, "UTF-8")
                .replace("+", "%20")
                .replace("%3A", ":")
        }
}
