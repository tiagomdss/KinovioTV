package tv.kinovio.app.stremio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StremioManifest(
    val id: String = "",
    val name: String = "Addon",
    val description: String = "",
    val version: String = "",
    val logo: String? = null,
    val icon: String? = null,
    val resources: List<ResourceRef> = emptyList(),
    val types: List<String> = emptyList(),
    val catalogs: List<StremioCatalog> = emptyList()
)

@Serializable(with = ResourceRefSerializer::class)
data class ResourceRef(val name: String)

@Serializable
data class StremioCatalog(
    val type: String,
    val id: String,
    val name: String = id,
    val extra: List<CatalogExtra> = emptyList()
)

@Serializable
data class CatalogExtra(
    val name: String,
    @SerialName("isRequired") val isRequired: Boolean = false,
    val options: List<String> = emptyList()
)

@Serializable
data class CatalogResponse(val metas: List<StremioMetaPreview> = emptyList())

@Serializable
data class MetaResponse(val meta: StremioMeta? = null)

@Serializable
data class StreamResponse(val streams: List<StremioStream> = emptyList())

@Serializable
data class StremioMetaPreview(
    val id: String,
    val type: String = "movie",
    val name: String,
    val poster: String? = null,
    val background: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val releaseInfo: String? = null,
    val genres: List<String> = emptyList(),
    val imdbRating: String? = null
)

@Serializable
data class StremioMeta(
    val id: String,
    val type: String = "movie",
    val name: String,
    val poster: String? = null,
    val background: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val releaseInfo: String? = null,
    val runtime: String? = null,
    val genres: List<String> = emptyList(),
    val videos: List<StremioVideo> = emptyList()
)

@Serializable
data class StremioVideo(
    val id: String,
    val title: String = "",
    val season: Int? = null,
    val episode: Int? = null,
    val released: String? = null,
    val thumbnail: String? = null,
    val overview: String? = null
)

@Serializable
data class StremioStream(
    val name: String? = null,
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val externalUrl: String? = null,
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val sources: List<String> = emptyList(),
    val subtitles: List<StremioSubtitle> = emptyList(),
    val behaviorHints: StreamBehaviorHints? = null
) {
    val playableUrl: String?
        get() = url
            ?.substringBefore("|")
            ?.takeIf { it.startsWith("http://") || it.startsWith("https://") }

    val displayUrl: String?
        get() = playableUrl ?: externalUrl
}

@Serializable
data class StremioSubtitle(
    val id: String? = null,
    val url: String? = null,
    val lang: String? = null
)

@Serializable
data class StreamBehaviorHints(
    val filename: String? = null,
    val videoSize: Long? = null,
    val cached: Boolean = false,
    val bingeGroup: String? = null
)

@Serializable
data class InstalledAddon(
    val manifestUrl: String,
    val manifest: StremioManifest
)
