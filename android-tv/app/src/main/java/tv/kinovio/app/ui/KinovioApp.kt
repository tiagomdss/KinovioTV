package tv.kinovio.app.ui

import android.net.Uri
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tv.kinovio.app.R
import tv.kinovio.app.data.SettingsStore
import tv.kinovio.app.stremio.InstalledAddon
import tv.kinovio.app.stremio.StremioCatalog
import tv.kinovio.app.stremio.StremioClient
import tv.kinovio.app.stremio.StremioMeta
import tv.kinovio.app.stremio.StremioMetaPreview
import tv.kinovio.app.stremio.StremioStream
import tv.kinovio.app.stremio.StremioSubtitle
import tv.kinovio.app.stremio.StremioVideo

private val Ink = Color(0xFF02040A)
private val InkDeep = Color(0xFF010206)
private val Panel = Color(0xFF07101D)
private val PanelSoft = Color(0xFF091624)
private val PanelGlass = Color(0xFF0C1A2A)
private val Line = Color(0xFF183049)
private val LineSoft = Color(0xFF0E1E2E)
private val TextMuted = Color(0xFF8FA0B5)
private val TextDim = Color(0xFF5C6B7E)
private val BrandEmerald = Color(0xFF00E6C3)
private val BrandCyan = Color(0xFF00B8FF)
private val BrandPurple = Color(0xFF8B5CFF)
private val BrandMagenta = Color(0xFFE94FD0)
private val Gold = Color(0xFFA7FF2A)
private const val KINOVIOSTREAM_TEST_ADDON_URL = "https://kinoviostream.martinsds.dev.br/manifest.json"
private const val CINEMETA_ADDON_URL = "https://v3-cinemeta.strem.io/manifest.json"

private fun InstalledAddon.hasResource(resource: String): Boolean =
    manifest.resources.any { it.name.equals(resource, ignoreCase = true) }

private data class ShowcaseItem(
    val title: String,
    val subtitle: String,
    val meta: String,
    val progress: Float,
    val colors: List<Color>,
    val source: String
)

private val continueWatchingShowcase = listOf(
    ShowcaseItem("Echoes of Dawn", "S1 E6 - 18 min left", "Personal library", 0.72f, listOf(Color(0xFF315F8D), Color(0xFF081826)), "Jellyfin"),
    ShowcaseItem("Neon Paradox", "Resume movie", "Watchlist", 0.41f, listOf(Color(0xFF2276FF), Color(0xFF141B3C)), "Stremio"),
    ShowcaseItem("Deep Current", "S1 E4 - next up", "Cloud drive", 0.58f, listOf(Color(0xFF1A9EC0), Color(0xFF06242C)), "WebDAV")
)

private val libraryShowcase = listOf(
    ShowcaseItem("Live TV", "M3U, Xtream, EPG", "Favorites and hidden groups", 0f, listOf(Color(0xFF00E6B8), Color(0xFF08392F)), "IPTV"),
    ShowcaseItem("Collections", "Movies, shows, cast", "TMDB-style metadata", 0f, listOf(Color(0xFF9D4DFF), Color(0xFF22113F)), "Library"),
    ShowcaseItem("Trakt", "Per-profile sync", "Optional account link", 0f, listOf(Color(0xFFFFC86A), Color(0xFF3E2C08)), "Sync")
)

private data class ProfilePersona(
    val name: String,
    val accent: List<Color>,
    val label: String
)

private val profilePersonas = listOf(
    ProfilePersona("Casa", listOf(BrandEmerald, BrandCyan), "K"),
    ProfilePersona("Tiago", listOf(BrandPurple, BrandMagenta), "T"),
    ProfilePersona("Anime", listOf(Color(0xFFFF5C87), Color(0xFFFFC857)), "A"),
    ProfilePersona("K-Drama", listOf(Color(0xFF38F2B2), Color(0xFF7B61FF)), "D"),
    ProfilePersona("Kids", listOf(Color(0xFFA7FF2A), Color(0xFF00B8FF)), "K")
)

private data class MobileStudioAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val onClick: () -> Unit
)

private data class PremiumFeature(
    val title: String,
    val subtitle: String,
    val status: String,
    val icon: ImageVector,
    val colors: List<Color>
)

private val premiumFeatureDeck = listOf(
    PremiumFeature("Cloud Sync", "Progress and profiles", "Local first", Icons.Rounded.Storage, listOf(Color(0xFF00E6C3), Color(0xFF0A2730))),
    PremiumFeature("Offline Mode", "Save allowed files", "Planned", Icons.Rounded.VideoLibrary, listOf(Color(0xFF8B5CFF), Color(0xFF201336))),
    PremiumFeature("Subtitles", "Multi-language tracks", "Ready", Icons.Rounded.Subtitles, listOf(Color(0xFFFFC857), Color(0xFF3A2A09))),
    PremiumFeature("New Episodes", "Calendar and reminders", "Planned", Icons.Rounded.CalendarMonth, listOf(Color(0xFF00B8FF), Color(0xFF082A46))),
    PremiumFeature("Screen Share", "Mobile to living room", "Next", Icons.Rounded.SwapHoriz, listOf(Color(0xFFFF5C87), Color(0xFF3B1424))),
    PremiumFeature("4K Ready", "HDR source selection", "Player", Icons.Rounded.Tv, listOf(Color(0xFFA7FF2A), Color(0xFF233708)))
)

private data class CinemaManagerMetric(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val accent: Color
)

private val cinemaManagerMetrics = listOf(
    CinemaManagerMetric("Sources", "Stremio + servers", Icons.Rounded.Storage, BrandEmerald),
    CinemaManagerMetric("Quality", "4K HDR ready", Icons.Rounded.Tv, Gold),
    CinemaManagerMetric("Subtitles", "SRT SSA ASS", Icons.Rounded.Subtitles, BrandCyan),
    CinemaManagerMetric("Binge", "Next preload", Icons.Rounded.Forward10, BrandPurple)
)

private data class EpisodeTimelineItem(
    val title: String,
    val meta: String,
    val state: String,
    val completed: Boolean
)

private val episodeTimelinePreview = listOf(
    EpisodeTimelineItem("Continue", "S1 E06 - 18 min left", "watching", true),
    EpisodeTimelineItem("Up Next", "S1 E07 - preloading sources", "ready", false),
    EpisodeTimelineItem("Calendar", "New episode reminder", "planned", false)
)

internal const val SOURCE_ORDER_SEASON = -1000
internal const val SOURCE_ORDER_EPISODE_COUNT = 500

sealed interface Screen {
    data object Profiles : Screen
    data object Home : Screen
    data object Settings : Screen
    data class Detail(val addon: InstalledAddon, val item: StremioMetaPreview) : Screen
    data class Player(val title: String, val stream: StremioStream, val alternates: List<StremioStream>) : Screen
}

@Composable
fun KinovioApp() {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }
    val client = remember { StremioClient() }
    val scope = rememberCoroutineScope()
    val isTvLike = LocalConfiguration.current.screenWidthDp >= 700

    var screen by remember { mutableStateOf<Screen>(Screen.Profiles) }
    var returnAfterPlayer by remember { mutableStateOf<Screen>(Screen.Home) }
    var addons by remember { mutableStateOf(store.loadAddons()) }
    var watchlist by remember { mutableStateOf(store.loadWatchlist()) }
    var continueWatching by remember { mutableStateOf(store.loadContinueWatching()) }
    var profileName by remember { mutableStateOf(store.loadProfileName()) }
    var iptvUrl by remember { mutableStateOf(store.loadIptvUrl()) }
    var serverUrl by remember { mutableStateOf(store.loadServerUrl()) }
    var traktName by remember { mutableStateOf(store.loadTraktName()) }
    var autoPlayBestSource by remember { mutableStateOf(store.loadAutoPlayBestSource()) }
    var minQuality by remember { mutableStateOf(store.loadMinQuality()) }
    var preferredSubtitle by remember { mutableStateOf(store.loadPreferredSubtitle()) }
    var selectedAddon by remember(addons) { mutableStateOf(addons.firstOrNull()) }
    var selectedCatalog by remember(selectedAddon) {
        mutableStateOf(selectedAddon?.manifest?.catalogs?.firstOrNull { it.type == "movie" || it.type == "series" })
    }
    var items by remember { mutableStateOf<List<StremioMetaPreview>>(emptyList()) }
    var catalogRows by remember { mutableStateOf<Map<String, List<StremioMetaPreview>>>(emptyMap()) }
    var homeSection by remember { mutableStateOf("Home") }
    var query by remember { mutableStateOf("") }
    var addUrl by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }
    var message by remember {
        val lastCrash = store.loadLastCrash()
        mutableStateOf(
            if (lastCrash.isNotBlank()) {
                "Last crash captured: ${lastCrash.lineSequence().take(4).joinToString(" | ")}"
            } else {
                "Connect a Stremio-compatible addon manifest to start browsing metadata and sources you provide or are allowed to access."
            }
        )
    }

    fun firstBrowsableCatalog(addon: InstalledAddon?): StremioCatalog? =
        addon?.manifest?.catalogs?.firstOrNull { catalog ->
            (catalog.type == "movie" || catalog.type == "series") && catalog.extra.none { it.isRequired }
        }

    fun browsableCatalogs(addon: InstalledAddon?): List<StremioCatalog> =
        addon?.manifest?.catalogs
            ?.filter { catalog -> (catalog.type == "movie" || catalog.type == "series") && catalog.extra.none { it.isRequired } }
            .orEmpty()

    fun InstalledAddon.catalogsAvailable(): Boolean =
        manifest.catalogs.any { it.type == "movie" || it.type == "series" }

    fun preferredContentAddon(list: List<InstalledAddon>): InstalledAddon? =
        list.firstOrNull { it.manifestUrl == KINOVIOSTREAM_TEST_ADDON_URL }
            ?: list.firstOrNull { it.hasResource("stream") }
            ?: list.firstOrNull { it.catalogsAvailable() }
            ?: list.firstOrNull()

    fun rowLabel(catalog: StremioCatalog): String {
        val name = catalog.name.lowercase()
        val id = catalog.id.lowercase()
        return when {
            "anime" in name || "anime" in id -> if ("top" in id || "melhores" in name) "Anime - Top" else "Anime - New"
            "k-drama" in name || "kdrama" in name || "k-drama" in id || "kdrama" in id || "korean" in name || "drama coreano" in name ->
                if (catalog.type.equals("movie", ignoreCase = true)) "K-Drama Movies" else "K-Drama Series"
            "bl" in id || "bls" in name -> "BL Series"
            catalog.name.isNotBlank() && catalog.name != catalog.id -> catalog.name
            catalog.type.equals("movie", ignoreCase = true) -> "Movies"
            catalog.type.equals("series", ignoreCase = true) -> "TV Shows"
            else -> catalog.name
        }
    }

    fun StremioMetaPreview.looksLikeAnime(): Boolean {
        val haystack = (listOf(name, type, releaseInfo.orEmpty()) + genres).joinToString(" ").lowercase()
        return "anime" in haystack || "animation" in haystack || "animação" in haystack
    }

    fun StremioMetaPreview.looksLikeKDrama(): Boolean {
        val haystack = (listOf(name, type, releaseInfo.orEmpty()) + genres).joinToString(" ").lowercase()
        return "k-drama" in haystack ||
            "kdrama" in haystack ||
            "korean" in haystack ||
            "coreano" in haystack ||
            "drama coreano" in haystack
    }

    fun StremioMetaPreview.looksLikeBL(): Boolean {
        val haystack = (listOf(name, type, releaseInfo.orEmpty()) + genres).joinToString(" ").lowercase()
        return " boys love " in " $haystack " ||
            " boy love " in " $haystack " ||
            " bl " in " $haystack " ||
            "yaoi" in haystack ||
            "bromance" in haystack
    }

    fun refreshCatalog(search: String? = null) {
        val addon = selectedAddon
        val catalog = selectedCatalog
        if (addon == null || catalog == null) {
            items = emptyList()
            catalogRows = emptyMap()
            message = "No addon catalog is selected yet."
            return
        }
        scope.launch {
            loading = true
            message = ""
            if (!search.isNullOrBlank()) {
                val supportsNativeSearch = catalog.extra.any { it.name.equals("search", ignoreCase = true) }
                if (supportsNativeSearch) {
                    runCatching { client.catalog(addon, catalog, search) }
                        .onSuccess {
                            items = it
                            catalogRows = if (it.isNotEmpty()) mapOf("Search Results" to it) else emptyMap()
                            if (it.isEmpty()) message = "No titles were returned by this catalog."
                        }
                        .onFailure { message = it.message ?: "Could not load this catalog." }
                } else {
                    val needle = normalizeTitleForMatch(search)
                    val found = mutableListOf<StremioMetaPreview>()
                    browsableCatalogs(addon).forEach { rowCatalog ->
                        runCatching { client.catalog(addon, rowCatalog, null) }
                            .onSuccess { rowItems ->
                                found += rowItems.filter { item ->
                                    normalizeTitleForMatch(item.name).contains(needle)
                                }
                            }
                    }
                    val results = found.distinctBy { it.id }.take(80)
                    items = results
                    catalogRows = if (results.isNotEmpty()) mapOf("Search Results" to results) else emptyMap()
                    if (results.isEmpty()) message = "No titles matched this search in the connected catalogs."
                }
            } else {
                val rows = linkedMapOf<String, List<StremioMetaPreview>>()
                val collected = mutableListOf<StremioMetaPreview>()
                browsableCatalogs(addon).forEach { rowCatalog ->
                    runCatching { client.catalog(addon, rowCatalog, null) }
                        .onSuccess { rowItems ->
                            if (rowItems.isNotEmpty()) {
                                collected += rowItems
                                val label = rowLabel(rowCatalog)
                                rows[label] = (rows[label].orEmpty() + rowItems).distinctBy { it.id }.take(60)
                            }
                        }
                }
                val curatedRows = linkedMapOf<String, List<StremioMetaPreview>>()
                fun addCuratedRow(label: String, predicate: (StremioMetaPreview) -> Boolean) {
                    val rowItems = collected.filter(predicate).distinctBy { it.id }.take(60)
                    if (rowItems.isNotEmpty()) curatedRows[label] = rowItems
                }
                addCuratedRow("Movies") { item ->
                    item.type.equals("movie", ignoreCase = true) && !item.looksLikeAnime() && !item.looksLikeKDrama() && !item.looksLikeBL()
                }
                addCuratedRow("TV Shows") { item ->
                    item.type.equals("series", ignoreCase = true) && !item.looksLikeAnime() && !item.looksLikeKDrama() && !item.looksLikeBL()
                }
                addCuratedRow("K-Drama") { item -> item.looksLikeKDrama() }
                addCuratedRow("Anime") { item -> item.looksLikeAnime() }
                addCuratedRow("BL Series") { item -> item.looksLikeBL() }
                catalogRows = (curatedRows + rows).toMap()
                items = catalogRows.values.firstOrNull().orEmpty()
                if (catalogRows.isEmpty()) message = "No titles were returned by the connected catalogs."
            }
            loading = false
        }
    }

    LaunchedEffect(selectedAddon?.manifestUrl, selectedCatalog?.id) {
        refreshCatalog()
    }

    fun installAddonFromUrl(url: String) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank()) return
        scope.launch {
            loading = true
            runCatching { client.install(trimmedUrl) }
                .onSuccess { addon ->
                    addons = (addons + addon).distinctBy { it.manifestUrl }
                    store.saveAddons(addons)
                    if (addon.hasResource("stream") || selectedAddon == null) {
                        selectedAddon = addon
                        selectedCatalog = firstBrowsableCatalog(addon)
                    }
                    addUrl = ""
                    message = "${addon.manifest.name} connected."
                }
                .onFailure { message = it.message ?: "Could not connect this addon." }
            loading = false
        }
    }

    fun toggleWatchlist(item: StremioMetaPreview) {
        val exists = watchlist.any { it.id == item.id }
        watchlist = if (exists) {
            watchlist.filterNot { it.id == item.id }
        } else {
            listOf(item) + watchlist
        }
        store.saveWatchlist(watchlist)
        message = if (exists) "Removed from Watchlist." else "Added to Watchlist."
    }

    fun markContinueWatching(item: StremioMetaPreview) {
        continueWatching = (listOf(item) + continueWatching).distinctBy { it.id }.take(30)
        store.saveContinueWatching(continueWatching)
    }

    LaunchedEffect(Unit) {
        delay(1180)
        showSplash = false
    }

    LaunchedEffect(Unit) {
        if (addons.isEmpty()) {
            scope.launch {
                loading = true
                val installed = mutableListOf<InstalledAddon>()
                listOf(KINOVIOSTREAM_TEST_ADDON_URL, CINEMETA_ADDON_URL).forEach { url ->
                    runCatching { client.install(url) }.onSuccess { installed += it }
                }
                addons = installed.distinctBy { it.manifestUrl }
                store.saveAddons(addons)
                selectedAddon = preferredContentAddon(addons)
                selectedCatalog = firstBrowsableCatalog(selectedAddon)
                loading = false
            }
        } else if (addons.none { it.manifestUrl == CINEMETA_ADDON_URL }) {
            scope.launch {
                runCatching { client.install(CINEMETA_ADDON_URL) }
                    .onSuccess { addon ->
                        addons = (addons + addon).distinctBy { it.manifestUrl }
                        store.saveAddons(addons)
                    }
            }
        }
    }

    BackHandler(enabled = !showSplash) {
        when (screen) {
            Screen.Profiles -> screen = Screen.Home
            Screen.Home -> message = "You are already on Home."
            Screen.Settings -> screen = Screen.Home
            is Screen.Detail -> screen = Screen.Home
            is Screen.Player -> screen = returnAfterPlayer
        }
    }

    Surface(Modifier.fillMaxSize(), color = Ink) {
        Box(Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    (slideInHorizontally(
                        animationSpec = tween(320, easing = FastOutSlowInEasing),
                        initialOffsetX = { it / 5 }
                    ) + fadeIn(tween(220))).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(260, easing = FastOutSlowInEasing),
                            targetOffsetX = { -it / 8 }
                        ) + fadeOut(tween(180))
                    ).using(SizeTransform(clip = false))
                },
                label = "screen"
            ) { active ->
                when (active) {
                    Screen.Profiles -> ProfileGate(
                        isTvLike = isTvLike,
                        selectedName = profileName,
                        onSelect = {
                            profileName = it
                            store.saveProfileName(it)
                            screen = Screen.Home
                        },
                        onManageProfiles = { screen = Screen.Settings }
                    )

                    Screen.Home -> HomeScreen(
                        isTvLike = isTvLike,
                        activeSection = homeSection,
                        profileName = profileName,
                        addons = addons,
                        selectedAddon = selectedAddon,
                        selectedCatalog = selectedCatalog,
                        items = items,
                        catalogRows = catalogRows,
                        watchlist = watchlist,
                        continueWatching = continueWatching,
                        query = query,
                        addUrl = addUrl,
                        loading = loading,
                        message = message,
                        onAddonSelected = {
                            selectedAddon = it
                            selectedCatalog = firstBrowsableCatalog(it)
                        },
                        onCatalogSelected = { selectedCatalog = it },
                        onQueryChanged = { query = it },
                        onSearch = { refreshCatalog(query) },
                        onAddUrlChanged = { addUrl = it },
                        onInstallAddon = { installAddonFromUrl(addUrl) },
                        onInstallTestAddon = { installAddonFromUrl(KINOVIOSTREAM_TEST_ADDON_URL) },
                        onRemoveAddon = { addon ->
                            addons = addons.filterNot { it.manifestUrl == addon.manifestUrl }
                            store.saveAddons(addons)
                            selectedAddon = addons.firstOrNull()
                        },
                        onHome = { homeSection = "Home"; screen = Screen.Home },
                        onMovies = { homeSection = "Movies"; screen = Screen.Home },
                        onShows = { homeSection = "TV Shows"; screen = Screen.Home },
                        onKDrama = { homeSection = "K-Drama"; screen = Screen.Home },
                        onAnime = { homeSection = "Anime"; screen = Screen.Home },
                        onBL = { homeSection = "BL"; screen = Screen.Home },
                        onLive = { homeSection = "Live"; screen = Screen.Home },
                        onSearchTab = { homeSection = "Search"; screen = Screen.Home },
                        onLibrary = { homeSection = "Library"; screen = Screen.Home },
                        onSettings = { screen = Screen.Settings },
                        onOpenItem = { item ->
                            selectedAddon?.let { screen = Screen.Detail(it, item) }
                        }
                    )

                    Screen.Settings -> SettingsScreen(
                        isTvLike = isTvLike,
                        profileName = profileName,
                        addons = addons,
                        selectedAddon = selectedAddon,
                        selectedCatalog = selectedCatalog,
                        query = query,
                        addUrl = addUrl,
                        iptvUrl = iptvUrl,
                        serverUrl = serverUrl,
                        traktName = traktName,
                        autoPlayBestSource = autoPlayBestSource,
                        minQuality = minQuality,
                        preferredSubtitle = preferredSubtitle,
                        onBack = { screen = Screen.Home },
                        onProfileNameChanged = {
                            profileName = it.ifBlank { "Casa" }
                            store.saveProfileName(profileName)
                        },
                        onClearLocalLibrary = {
                            watchlist = emptyList()
                            continueWatching = emptyList()
                            store.clearLocalLibrary()
                            message = "Local library cleared."
                        },
                        onAddonSelected = {
                            selectedAddon = it
                            selectedCatalog = firstBrowsableCatalog(it)
                        },
                        onCatalogSelected = { selectedCatalog = it },
                        onQueryChanged = { query = it },
                        onSearch = { refreshCatalog(query) },
                        onAddUrlChanged = { addUrl = it },
                        onInstallAddon = { installAddonFromUrl(addUrl) },
                        onInstallTestAddon = { installAddonFromUrl(KINOVIOSTREAM_TEST_ADDON_URL) },
                        onIptvUrlChanged = {
                            iptvUrl = it
                            store.saveIptvUrl(it)
                        },
                        onServerUrlChanged = {
                            serverUrl = it
                            store.saveServerUrl(it)
                        },
                        onTraktNameChanged = {
                            traktName = it
                            store.saveTraktName(it)
                        },
                        onAutoPlayBestSourceChanged = {
                            autoPlayBestSource = it
                            store.saveAutoPlayBestSource(it)
                        },
                        onMinQualityChanged = {
                            minQuality = it
                            store.saveMinQuality(it)
                        },
                        onPreferredSubtitleChanged = {
                            preferredSubtitle = it
                            store.savePreferredSubtitle(it)
                        },
                        onRemoveAddon = { addon ->
                            addons = addons.filterNot { it.manifestUrl == addon.manifestUrl }
                            store.saveAddons(addons)
                            selectedAddon = addons.firstOrNull()
                        }
                    )

                    is Screen.Detail -> DetailScreen(
                        isTvLike = isTvLike,
                        client = client,
                        addon = active.addon,
                        metadataAddon = addons.firstOrNull { it.hasResource("meta") } ?: active.addon,
                        preview = active.item,
                        autoPlayBestSource = autoPlayBestSource,
                        minQuality = minQuality,
                        inWatchlist = watchlist.any { it.id == active.item.id },
                        onToggleWatchlist = { toggleWatchlist(active.item) },
                        onBack = { screen = Screen.Home },
                        onPlay = { title, stream, streams ->
                            markContinueWatching(active.item)
                            returnAfterPlayer = active
                            screen = Screen.Player(title, stream, streams)
                        }
                    )

                    is Screen.Player -> PlayerScreen(
                        title = active.title,
                        stream = active.stream,
                        alternates = active.alternates,
                        onBack = { screen = returnAfterPlayer }
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(animationSpec = tween(220)),
                exit = fadeOut(animationSpec = tween(520))
            ) {
                SplashScreen()
            }
        }
    }
}

@Composable
private fun ProfileGate(
    isTvLike: Boolean,
    selectedName: String,
    onSelect: (String) -> Unit,
    onManageProfiles: () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val gateGradient = if (systemDark) {
        listOf(Color(0xFF170A2C), Color(0xFF07101D), InkDeep)
    } else {
        listOf(Color(0xFFF1F5FF), Color(0xFFDCEBFF), Color(0xFF121A2A))
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    gateGradient
                )
            )
    ) {
        AmbientOrbs()
        LightSweep()
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = if (isTvLike) 84.dp else 28.dp, vertical = if (isTvLike) 56.dp else 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BrandMark(if (isTvLike) 72.dp else 58.dp)
                Text("Who is watching?", color = Color.White, fontSize = if (isTvLike) 34.sp else 26.sp, fontWeight = FontWeight.Black)
                Text("Choose a local profile for watchlist, progress and sync.", color = TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(if (isTvLike) 30.dp else 20.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(profilePersonas) { persona ->
                    ProfilePersonaCard(
                        persona = persona,
                        selected = selectedName.equals(persona.name, ignoreCase = true),
                        isTvLike = isTvLike,
                        onClick = { onSelect(persona.name) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                GlassPill("Manage", onManageProfiles)
                GlassPill("Continue", onClick = { onSelect(selectedName.ifBlank { "Casa" }) })
            }
        }
    }
}

@Composable
private fun ProfilePersonaCard(
    persona: ProfilePersona,
    selected: Boolean,
    isTvLike: Boolean,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused || selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 360f),
        label = "profileScale"
    )
    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier
                .size(if (isTvLike) 132.dp else 92.dp)
                .clip(RoundedCornerShape(if (isTvLike) 28.dp else 22.dp))
                .background(Brush.linearGradient(persona.accent))
                .border(
                    if (selected || focused) 3.dp else 1.dp,
                    if (selected || focused) Color.White else Color.White.copy(alpha = 0.22f),
                    RoundedCornerShape(if (isTvLike) 28.dp else 22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (persona.name == "Casa") {
                BrandMark(if (isTvLike) 74.dp else 52.dp)
            } else {
                Text(persona.label, color = Color.White, fontSize = if (isTvLike) 54.sp else 38.sp, fontWeight = FontWeight.Black)
            }
        }
        Text(persona.name, color = Color.White, fontSize = if (isTvLike) 18.sp else 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SplashScreen() {
    val scale by animateFloatAsState(
        targetValue = 1.08f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "splashScale"
    )
    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {
            BrandMark(118.dp)
            Text("KINOVIO", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Text("PERSONAL MEDIA HUB", color = BrandEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun HomeScreen(
    isTvLike: Boolean,
    activeSection: String,
    profileName: String,
    addons: List<InstalledAddon>,
    selectedAddon: InstalledAddon?,
    selectedCatalog: StremioCatalog?,
    items: List<StremioMetaPreview>,
    catalogRows: Map<String, List<StremioMetaPreview>>,
    watchlist: List<StremioMetaPreview>,
    continueWatching: List<StremioMetaPreview>,
    query: String,
    addUrl: String,
    loading: Boolean,
    message: String,
    onAddonSelected: (InstalledAddon) -> Unit,
    onCatalogSelected: (StremioCatalog) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onAddUrlChanged: (String) -> Unit,
    onInstallAddon: () -> Unit,
    onInstallTestAddon: () -> Unit,
    onRemoveAddon: (InstalledAddon) -> Unit,
    onHome: () -> Unit,
    onMovies: () -> Unit,
    onShows: () -> Unit,
    onKDrama: () -> Unit,
    onAnime: () -> Unit,
    onBL: () -> Unit,
    onLive: () -> Unit,
    onSearchTab: () -> Unit,
    onLibrary: () -> Unit,
    onSettings: () -> Unit,
    onOpenItem: (StremioMetaPreview) -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val homeGradient = if (systemDark) {
        listOf(Color(0xFF030814), Ink, Color(0xFF050812))
    } else {
        listOf(Color(0xFFEFF5FF), Color(0xFFDDEBFA), Color(0xFF07101D))
    }
    Row(Modifier.fillMaxSize()) {
        if (isTvLike) {
            Sidebar(
                label = selectedAddon?.manifest?.name ?: "KinovioTV",
                activeSection = activeSection,
                onHome = onHome,
                onMovies = onMovies,
                onShows = onShows,
                onLive = onLive,
                onSettings = onSettings
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(homeGradient))
        ) {
            LightSweep()
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = if (isTvLike) 36.dp else 14.dp,
                    top = if (isTvLike) 36.dp else 14.dp,
                    end = if (isTvLike) 36.dp else 14.dp,
                    bottom = if (isTvLike) 36.dp else 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                item { HeroHeader(isTvLike, profileName, items.firstOrNull(), onOpenItem, onSearchTab, onSettings) }
                item {
                    HomeQuickTabs(
                        activeSection = activeSection,
                        onHome = onHome,
                        onMovies = onMovies,
                        onShows = onShows,
                        onKDrama = onKDrama,
                        onAnime = onAnime,
                        onBL = onBL,
                        onLive = onLive,
                        onSearch = onSearchTab
                    )
                }
                if (activeSection == "Search" && isTvLike) {
                    item {
                        SearchPanel(
                            query = query,
                            onQueryChanged = onQueryChanged,
                            onSearch = onSearch
                        )
                    }
                }
                if (activeSection == "Search" && !isTvLike) {
                    item {
                        SearchPanel(
                            query = query,
                            onQueryChanged = onQueryChanged,
                            onSearch = onSearch,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if ((activeSection == "Home" || activeSection == "Library") && continueWatching.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SectionTitle("Continue Watching")
                            PosterRail(continueWatching, isTvLike, onOpenItem)
                        }
                    }
                }
                if ((activeSection == "Home" || activeSection == "Library") && watchlist.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SectionTitle("Watchlist")
                            PosterRail(watchlist, isTvLike, onOpenItem)
                        }
                    }
                }
                if (activeSection == "Library" && watchlist.isEmpty() && continueWatching.isEmpty()) {
                    item {
                        LibraryEmptyPanel()
                    }
                }
                item {
                    when {
                    loading -> HomeSkeleton(isTvLike)
                    items.isEmpty() -> EmptyState(message)
                    else -> MediaGrid(items, catalogRows, activeSection, isTvLike, onOpenItem)
                }
            }
            }
            if (!isTvLike) {
                MobileBottomNav(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    activeSection = activeSection,
                    onHome = onHome,
                    onLibrary = onLibrary,
                    onSearch = onSearchTab,
                    onSettings = onSettings
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    isTvLike: Boolean,
    profileName: String,
    addons: List<InstalledAddon>,
    selectedAddon: InstalledAddon?,
    selectedCatalog: StremioCatalog?,
    query: String,
    addUrl: String,
    iptvUrl: String,
    serverUrl: String,
    traktName: String,
    autoPlayBestSource: Boolean,
    minQuality: String,
    preferredSubtitle: String,
    onBack: () -> Unit,
    onProfileNameChanged: (String) -> Unit,
    onClearLocalLibrary: () -> Unit,
    onAddonSelected: (InstalledAddon) -> Unit,
    onCatalogSelected: (StremioCatalog) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onAddUrlChanged: (String) -> Unit,
    onInstallAddon: () -> Unit,
    onInstallTestAddon: () -> Unit,
    onIptvUrlChanged: (String) -> Unit,
    onServerUrlChanged: (String) -> Unit,
    onTraktNameChanged: (String) -> Unit,
    onAutoPlayBestSourceChanged: (Boolean) -> Unit,
    onMinQualityChanged: (String) -> Unit,
    onPreferredSubtitleChanged: (String) -> Unit,
    onRemoveAddon: (InstalledAddon) -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(InkDeep, Ink, Color(0xFF06101B))))
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = if (isTvLike) 52.dp else 18.dp,
                top = if (isTvLike) 42.dp else 22.dp,
                end = if (isTvLike) 52.dp else 18.dp,
                bottom = 34.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BrandLockup(isTvLike)
                        Text("Settings", color = Color.White, fontSize = if (isTvLike) 38.sp else 30.sp, fontWeight = FontWeight.Black)
                        Text("Profile: $profileName", color = BrandEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    FocusButton("Back", onBack)
                }
            }
            item {
                ProfileSettingsPanel(
                    profileName = profileName,
                    onProfileNameChanged = onProfileNameChanged,
                    onClearLocalLibrary = onClearLocalLibrary
                )
            }
            item { SourceOverview(isTvLike, addons.size, iptvUrl, serverUrl, traktName) }
            item {
                SetupPanel(
                    isTvLike = isTvLike,
                    addons = addons,
                    selectedAddon = selectedAddon,
                    selectedCatalog = selectedCatalog,
                    query = query,
                    addUrl = addUrl,
                    iptvUrl = iptvUrl,
                    serverUrl = serverUrl,
                    traktName = traktName,
                    autoPlayBestSource = autoPlayBestSource,
                    minQuality = minQuality,
                    preferredSubtitle = preferredSubtitle,
                    onAddonSelected = onAddonSelected,
                    onCatalogSelected = onCatalogSelected,
                    onQueryChanged = onQueryChanged,
                    onSearch = onSearch,
                    onAddUrlChanged = onAddUrlChanged,
                    onInstallAddon = onInstallAddon,
                    onInstallTestAddon = onInstallTestAddon,
                    onIptvUrlChanged = onIptvUrlChanged,
                    onServerUrlChanged = onServerUrlChanged,
                    onTraktNameChanged = onTraktNameChanged,
                    onAutoPlayBestSourceChanged = onAutoPlayBestSourceChanged,
                    onMinQualityChanged = onMinQualityChanged,
                    onPreferredSubtitleChanged = onPreferredSubtitleChanged,
                    onRemoveAddon = onRemoveAddon
                )
            }
        }
    }
}

@Composable
private fun SearchPanel(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val compact = LocalConfiguration.current.screenWidthDp < 520
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.34f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Search, contentDescription = "Search", tint = BrandEmerald, modifier = Modifier.size(22.dp))
                Column {
                    Text("Search your connected sources", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Results come from your addons and personal sources.", color = TextMuted, fontSize = 12.sp)
                }
            }
            if (compact) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Movie, show, anime...") }
                    )
                    FocusButton("Search", onSearch, modifier = Modifier.fillMaxWidth(), icon = Icons.Rounded.Search)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Movie, show, anime...") }
                    )
                    FocusButton("Search", onSearch, modifier = Modifier.width(128.dp), icon = Icons.Rounded.Search)
                }
            }
        }
    }
}

@Composable
private fun ProfileSettingsPanel(
    profileName: String,
    onProfileNameChanged: (String) -> Unit,
    onClearLocalLibrary: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.13f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(BrandEmerald, BrandPurple))), contentAlignment = Alignment.Center) {
                    Text(profileName.take(1).uppercase(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
                Column {
                    Text("Profile", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Local profile settings for this device.", color = TextMuted, fontSize = 12.sp)
                }
            }
            OutlinedTextField(
                value = profileName,
                onValueChange = onProfileNameChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Profile name") }
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Pill("PIN optional", selected = false, onClick = { }, icon = Icons.Rounded.Settings) }
                item { Pill("Trakt per profile", selected = false, onClick = { }, icon = Icons.Rounded.SwapHoriz) }
                item { Pill("Local only", selected = true, onClick = { }, icon = Icons.Rounded.Storage) }
            }
            FocusButton(
                "Clear local library",
                onClearLocalLibrary,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.VideoLibrary
            )
        }
    }
}

@Composable
private fun HomeQuickTabs(
    activeSection: String,
    onHome: () -> Unit,
    onMovies: () -> Unit,
    onShows: () -> Unit,
    onKDrama: () -> Unit,
    onAnime: () -> Unit,
    onBL: () -> Unit,
    onLive: () -> Unit,
    onSearch: () -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(horizontal = 1.dp)) {
        item { ModeChip("Home", Icons.Rounded.Home, activeSection == "Home", onHome) }
        item { ModeChip("Movies", Icons.Rounded.Movie, activeSection == "Movies", onMovies) }
        item { ModeChip("Series", Icons.Rounded.Tv, activeSection == "TV Shows", onShows) }
        item { ModeChip("K-Drama", Icons.Rounded.FavoriteBorder, activeSection == "K-Drama", onKDrama) }
        item { ModeChip("Anime", Icons.Rounded.VideoLibrary, activeSection == "Anime", onAnime) }
        item { ModeChip("BL", Icons.Rounded.FavoriteBorder, activeSection == "BL", onBL) }
        item { ModeChip("Live", Icons.Rounded.LiveTv, activeSection == "Live", onLive) }
        item { ModeChip("Search", Icons.Rounded.Search, activeSection == "Search", onSearch) }
    }
}

@Composable
private fun ModeChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, tween(150), label = "modeChipScale")
    Surface(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 16f else 0f
            }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) BrandEmerald else Color.White.copy(alpha = if (focused) 0.14f else 0.07f),
        border = BorderStroke(1.dp, if (selected) BrandEmerald else Color.White.copy(alpha = if (focused) 0.28f else 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = if (selected) InkDeep else Color.White, modifier = Modifier.size(17.dp))
            Text(label, color = if (selected) InkDeep else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun Sidebar(
    label: String,
    activeSection: String,
    onHome: () -> Unit,
    onMovies: () -> Unit,
    onShows: () -> Unit,
    onLive: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        Modifier
            .width(126.dp)
            .fillMaxHeight()
            .background(Color(0xCC02040A))
            .border(1.dp, Color.White.copy(alpha = 0.06f))
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        BrandMark(54.dp)
        SidebarItem("Home", Icons.Rounded.Home, activeSection == "Home", onHome)
        SidebarItem("Movies", Icons.Rounded.Movie, activeSection == "Movies", onMovies)
        SidebarItem("TV Shows", Icons.Rounded.Tv, activeSection == "TV Shows", onShows)
        SidebarItem("Live", Icons.Rounded.LiveTv, activeSection == "Live", onLive)
        SidebarItem("Settings", Icons.Rounded.Settings, false, onSettings)
        Spacer(Modifier.weight(1f))
        Text(label, color = TextMuted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SidebarItem(text: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit = {}) {
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .width(104.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .background(
                when {
                    selected -> BrandEmerald
                    focused -> Color.White.copy(alpha = 0.10f)
                    else -> Color.Transparent
                }
            )
            .border(
                if (focused && !selected) 1.dp else 0.dp,
                BrandEmerald.copy(alpha = 0.46f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = if (selected) InkDeep else if (focused) BrandEmerald else TextMuted,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = text,
            color = if (selected) InkDeep else if (focused) Color.White else TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BrandMark(markSize: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier
            .size(markSize)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.kinovio_logo_premium),
            contentDescription = "KinovioTV",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun BrandLockup(isTvLike: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BrandMark(if (isTvLike) 42.dp else 34.dp)
        Column {
            Text("KINOVIO", color = Color.White, fontSize = if (isTvLike) 18.sp else 15.sp, fontWeight = FontWeight.Black)
            Text("TV", color = BrandEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MobileHeroTop(profileName: String, onSearch: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            BrandMark(38.dp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("KINOVIO", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text("Personal cinema", color = BrandEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                    .clickable(onClick = onSearch),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(19.dp))
            }
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BrandEmerald, BrandPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text(profileName.take(1).uppercase(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun MobileBottomNav(
    modifier: Modifier = Modifier,
    activeSection: String,
    onHome: () -> Unit,
    onLibrary: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 28.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(29.dp),
        color = Color(0xCC080D15),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Home", Icons.Rounded.Home, activeSection == "Home", onHome)
            BottomNavItem("Library", Icons.Rounded.VideoLibrary, activeSection == "Library", onLibrary)
            BottomNavItem("Search", Icons.Rounded.Search, activeSection == "Search", onSearch)
            BottomNavItem("Settings", Icons.Rounded.Settings, false, onSettings)
        }
    }
}

@Composable
private fun BottomNavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(if (focused) Color.White.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier
                .size(if (selected) 34.dp else 26.dp)
                .clip(CircleShape)
                .background(if (selected) Brush.linearGradient(listOf(BrandEmerald, BrandCyan)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) InkDeep else if (focused) Color.White else TextMuted,
                modifier = Modifier.size(if (selected) 19.dp else 17.dp)
            )
        }
        Text(label, color = if (selected || focused) Color.White else TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MobileStudioGrid(
    onMovies: () -> Unit,
    onShows: () -> Unit,
    onKDrama: () -> Unit,
    onAnime: () -> Unit,
    onLive: () -> Unit,
    onSources: () -> Unit
) {
    val actions = listOf(
        MobileStudioAction("Movies", "Posters, sources", Icons.Rounded.Movie, listOf(Color(0xFF00E6C3), Color(0xFF064E45)), onMovies),
        MobileStudioAction("Series", "Seasons, episodes", Icons.Rounded.VideoLibrary, listOf(Color(0xFF8B5CFF), Color(0xFF24114D)), onShows),
        MobileStudioAction("Anime", "Direct or seasons", Icons.Rounded.PlayArrow, listOf(Color(0xFFFF5C87), Color(0xFF432033)), onAnime),
        MobileStudioAction("K-Drama", "Shows and movies", Icons.Rounded.FavoriteBorder, listOf(Color(0xFFFFC857), Color(0xFF47350A)), onKDrama),
        MobileStudioAction("Live TV", "IPTV, EPG", Icons.Rounded.LiveTv, listOf(Color(0xFF00B8FF), Color(0xFF07304C)), onLive),
        MobileStudioAction("Sources", "Addons, servers", Icons.Rounded.Storage, listOf(Color(0xFFB8FF2C), Color(0xFF243C08)), onSources)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Your Cinema")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            actions.chunked(2).forEach { rowActions ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowActions.forEach { action ->
                        MobileStudioActionCard(action, Modifier.weight(1f))
                    }
                    if (rowActions.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MobileStudioActionCard(action: MobileStudioAction, modifier: Modifier = Modifier) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.03f else 1f, tween(160), label = "studioActionScale")
    Box(
        modifier
            .height(108.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(action.colors))
            .border(1.dp, Color.White.copy(alpha = if (focused) 0.48f else 0.16f), RoundedCornerShape(22.dp))
            .clickable(onClick = action.onClick)
            .padding(14.dp)
    ) {
        Icon(action.icon, contentDescription = action.title, tint = Color.White, modifier = Modifier.align(Alignment.TopStart).size(24.dp))
        Column(Modifier.align(Alignment.BottomStart), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(action.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(action.subtitle, color = Color.White.copy(alpha = 0.76f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MobileAdvancedDiscovery(onSearch: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.30f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Smart Discovery", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Search by genre, source, quality and subtitles.", color = TextMuted, fontSize = 12.sp)
                }
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(BrandEmerald, BrandCyan)))
                        .clickable(onClick = onSearch),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = "Search", tint = InkDeep, modifier = Modifier.size(22.dp))
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item { DiscoveryFilterChip("Genre", "Movies, anime, K-drama") }
                item { DiscoveryFilterChip("4K HDR", "Quality first") }
                item { DiscoveryFilterChip("Subtitles", "PT-BR, EN, multi") }
                item { DiscoveryFilterChip("Offline", "Allowed sources") }
                item { DiscoveryFilterChip("Remote", "TV focus ready") }
            }
        }
    }
}

@Composable
private fun DiscoveryFilterChip(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.13f))
    ) {
        Column(Modifier.width(138.dp).padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = TextMuted, fontSize = 11.sp, lineHeight = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MobileCinemaManagerPanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xB007101D),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Personal Cinema Manager", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Organize, compare and resume from every connected source.", color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp)
                }
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(BrandPurple, BrandEmerald))),
                    contentAlignment = Alignment.Center
                ) {
                    BrandMark(30.dp)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                cinemaManagerMetrics.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { metric -> MobileMetricCard(metric, Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileMetricCard(metric: CinemaManagerMetric, modifier: Modifier = Modifier) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.025f else 1f, tween(150), label = "metricScale")
    Row(
        modifier
            .height(78.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = if (focused) 0.12f else 0.07f))
            .border(1.dp, metric.accent.copy(alpha = if (focused) 0.72f else 0.28f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(metric.accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(metric.icon, contentDescription = metric.title, tint = metric.accent, modifier = Modifier.size(19.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(metric.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(metric.value, color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MobileEpisodeTimelinePanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Black.copy(alpha = 0.32f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.13f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Watching Flow", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Progress, next episode and reminders in one clean path.", color = TextMuted, fontSize = 12.sp)
                }
                CircularProgressIndicator(color = BrandEmerald, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                episodeTimelinePreview.forEachIndexed { index, item ->
                    TimelineRow(item = item, showLine = index < episodeTimelinePreview.lastIndex)
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(item: EpisodeTimelineItem, showLine: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (item.completed) BrandEmerald else Color.White.copy(alpha = 0.08f))
                    .border(1.dp, if (item.completed) BrandEmerald else Color.White.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (item.completed) Icons.Rounded.PlayArrow else Icons.Rounded.CalendarMonth,
                    contentDescription = item.title,
                    tint = if (item.completed) InkDeep else TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
            if (showLine) {
                Box(Modifier.width(2.dp).height(38.dp).background(Color.White.copy(alpha = 0.12f)))
            }
        }
        Column(Modifier.weight(1f).padding(bottom = if (showLine) 12.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(item.state.uppercase(), color = if (item.completed) BrandEmerald else TextDim, fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
            Text(item.meta, color = TextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MobileWidgetLabPanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xD0060B13),
        border = BorderStroke(1.dp, BrandPurple.copy(alpha = 0.32f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("AI Widget Lab", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Describe a list. Validate it locally. Add it to your home.", color = TextMuted, fontSize = 12.sp)
                }
                Text(
                    "4/4",
                    color = InkDeep,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.clip(CircleShape).background(Gold).padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.07f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("WidgetMetadata = {", color = BrandCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("  id: \"kinovio.trending.week\"", color = Color.White.copy(alpha = 0.86f), fontSize = 12.sp)
                    Text("  modules: [\"loadList\"]", color = Color.White.copy(alpha = 0.86f), fontSize = 12.sp)
                    Text("}", color = BrandCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(BrandEmerald))
                        Text("Backtest passed - VideoItem fields verified", color = BrandEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MobilePremiumFeatureDeck() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Premium System")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 2.dp)) {
            items(premiumFeatureDeck) { feature ->
                MobilePremiumFeatureCard(feature)
            }
        }
    }
}

@Composable
private fun MobilePremiumFeatureCard(feature: PremiumFeature) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.035f else 1f, tween(160), label = "premiumFeatureScale")
    Box(
        Modifier
            .width(202.dp)
            .height(136.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 22f else 4f
            }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(feature.colors))
            .border(1.dp, Color.White.copy(alpha = if (focused) 0.45f else 0.18f), RoundedCornerShape(24.dp))
            .padding(14.dp)
    ) {
        Box(
            Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(feature.icon, contentDescription = feature.title, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Text(
            feature.status,
            color = InkDeep,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.82f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Column(Modifier.align(Alignment.BottomStart), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(feature.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(feature.subtitle, color = Color.White.copy(alpha = 0.78f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MobileTopTrending(
    items: List<StremioMetaPreview>,
    onOpenItem: (StremioMetaPreview) -> Unit
) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Top 10 Trending")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEachIndexed { index, item ->
                MobileTrendingRow(index + 1, item, onOpenItem)
            }
        }
    }
}

@Composable
private fun MobileTrendingRow(
    rank: Int,
    item: StremioMetaPreview,
    onOpenItem: (StremioMetaPreview) -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .height(104.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(20.dp))
            .background(if (focused) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.045f))
            .border(1.dp, if (focused) BrandEmerald.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.09f), RoundedCornerShape(20.dp))
            .clickable { onOpenItem(item) }
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(rank.toString().padStart(2, '0'), color = BrandEmerald, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(34.dp))
        Box(
            Modifier
                .width(76.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .background(PanelSoft),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.poster ?: item.background,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (item.poster == null && item.background == null) {
                Text(item.name.take(1), color = BrandEmerald, fontSize = 26.sp, fontWeight = FontWeight.Black)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                listOfNotNull(item.releaseInfo, item.type.replaceFirstChar { it.uppercase() }).joinToString("  •  "),
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.genres.take(2).forEach { genre -> HeroChip(genre) }
            }
        }
    }
}

@Composable
private fun CinematicTopBar(
    profileName: String,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Black.copy(alpha = 0.28f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
    ) {
        Row(
            Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandLockup(true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                HeroChip("Home")
                HeroChip("Movies")
                HeroChip("Series")
                HeroChip("Anime")
            }
            Row(
                Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .clickable(onClick = onSearch)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.86f), modifier = Modifier.size(20.dp))
                Text("Search movies, shows, episodes", color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BrandPurple, BrandEmerald))),
                contentAlignment = Alignment.Center
            ) {
                Text(profileName.take(1).uppercase(), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }
            GlassIcon("Settings", Icons.Rounded.Settings, onClick = onSettings)
        }
    }
}

@Composable
private fun HeroHeader(
    isTvLike: Boolean,
    profileName: String,
    featured: StremioMetaPreview?,
    onOpenItem: (StremioMetaPreview) -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(if (isTvLike) 420.dp else 470.dp)
            .clip(RoundedCornerShape(if (isTvLike) 22.dp else 0.dp))
            .background(InkDeep)
            .border(if (isTvLike) 1.dp else 0.dp, LineSoft, RoundedCornerShape(if (isTvLike) 22.dp else 0.dp))
    ) {
        if (featured != null) {
            AsyncImage(
                model = featured.background ?: featured.poster,
                contentDescription = featured.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.55f
            )
        } else {
            AmbientOrbs()
        }
        LightSweep()
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(InkDeep, Ink.copy(alpha = 0.55f), Color.Transparent),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(Float.MAX_VALUE, 0f)
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Ink.copy(alpha = 0.35f), InkDeep.copy(alpha = 0.96f))
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(InkDeep.copy(alpha = 0.42f), Color.Transparent, Color.Transparent)))
        )
        Column(
            Modifier
                .align(Alignment.TopStart)
                .padding(if (isTvLike) 30.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isTvLike) {
                CinematicTopBar(profileName = profileName, onSearch = onSearch, onSettings = onSettings)
            } else {
                MobileHeroTop(profileName, onSearch)
            }
        }
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(if (isTvLike) 34.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(if (isTvLike) 14.dp else 12.dp)
        ) {
            if (featured != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    HeroBadge(featured.type.replaceFirstChar { it.uppercase() })
                    featured.releaseInfo?.let { HeroBadge(it) }
                    HeroBadge("HD")
                }
                Text(
                    featured.name,
                    color = Color.White,
                    fontSize = if (isTvLike) 44.sp else 36.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = if (isTvLike) 46.sp else 38.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (isTvLike) {
                    Text(
                        "Continue from where you left off.",
                        color = TextMuted,
                        fontSize = 15.sp
                    )
                }
                if (isTvLike) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        PrimaryActionButton("Resume", onClick = { onOpenItem(featured) })
                        GlassPill("Details", onClick = { onOpenItem(featured) })
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PrimaryActionButton("Resume", onClick = { onOpenItem(featured) }, modifier = Modifier.fillMaxWidth())
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            HeroDots()
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                    .clickable { onOpenItem(featured) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.VideoLibrary, contentDescription = "Details", tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            } else {
                Text(
                    "STREAM MORE.\nEXPERIENCE EVERYTHING.",
                    color = Color.White,
                    fontSize = if (isTvLike) 40.sp else 30.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = if (isTvLike) 42.sp else 32.sp
                )
                Text(
                    "A personal cinema hub for source aggregation, library polish, remote-first playback, subtitles, profiles, IPTV, and sync.",
                    color = TextMuted,
                    fontSize = if (isTvLike) 16.sp else 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (isTvLike) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HeroChip("Profiles")
                        HeroChip("Watchlist")
                        HeroChip("IPTV")
                        HeroChip("Servers")
                        HeroChip("Trakt")
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeroChip("Profiles")
                            HeroChip("Watchlist")
                            HeroChip("IPTV")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeroChip("Servers")
                            HeroChip("Trakt")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroDots() {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(7) { index ->
            Box(
                Modifier
                    .size(if (index == 0) 7.dp else 5.dp)
                    .clip(CircleShape)
                    .background(if (index == 0) Color.White else Color.White.copy(alpha = 0.34f))
            )
        }
    }
}

@Composable
private fun HeroBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
    ) {
        Text(
            text.uppercase(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PrimaryActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, tween(160), label = "paScale")
    val glow by animateFloatAsState(if (focused) 1f else 0f, tween(180), label = "paGlow")
    Box(
        modifier
            .widthIn(min = 170.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = (18 + 22 * glow)
            }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(BrandEmerald, BrandCyan)))
            .clickable { onClick() }
            .padding(horizontal = 26.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = label, tint = InkDeep, modifier = Modifier.size(19.dp))
            Text(label, color = InkDeep, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun GlassPill(label: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, tween(160), label = "gpScale")
    Box(
        Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = if (focused) 0.18f else 0.08f))
            .border(1.dp, Color.White.copy(alpha = if (focused) 0.4f else 0.18f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 14.dp)
    ) {
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LightSweep() {
    Canvas(Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(-size.width * 0.10f, size.height * 0.86f)
            cubicTo(size.width * 0.18f, size.height * 0.68f, size.width * 0.44f, size.height * 0.92f, size.width * 1.12f, size.height * 0.54f)
            lineTo(size.width * 1.12f, size.height * 0.60f)
            cubicTo(size.width * 0.44f, size.height * 0.98f, size.width * 0.18f, size.height * 0.76f, -size.width * 0.10f, size.height * 0.92f)
            close()
        }
        drawPath(path, Brush.horizontalGradient(listOf(Color.Transparent, BrandEmerald.copy(alpha = 0.30f), BrandPurple.copy(alpha = 0.22f), Color.Transparent)))
    }
}

@Composable
private fun AmbientOrbs() {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(BrandEmerald.copy(alpha = 0.16f), radius = size.minDimension * 0.42f, center = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.18f))
        drawCircle(BrandPurple.copy(alpha = 0.18f), radius = size.minDimension * 0.38f, center = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.12f))
        drawCircle(Gold.copy(alpha = 0.10f), radius = size.minDimension * 0.32f, center = androidx.compose.ui.geometry.Offset(size.width * 0.68f, size.height * 0.84f))
    }
}

@Composable
private fun HeroChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
    ) {
        Text(
            text,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ShowcaseRail(title: String, items: List<ShowcaseItem>, refreshing: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(title)
            if (refreshing) {
                CircularProgressIndicator(color = BrandEmerald, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(items) { item ->
                ShowcaseCard(item)
            }
        }
    }
}

@Composable
private fun HomeSkeleton(isTvLike: Boolean) {
    val rows = if (isTvLike) 5 else 2
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        SectionTitle("Loading")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            repeat(rows) {
                SkeletonPoster(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SkeletonPoster(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.13f), Color.White.copy(alpha = 0.05f)))),
            contentAlignment = Alignment.BottomEnd
        ) {
            CircularProgressIndicator(color = BrandEmerald, strokeWidth = 2.dp, modifier = Modifier.padding(14.dp).size(22.dp))
        }
        Box(Modifier.fillMaxWidth(0.82f).height(12.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.10f)))
        Box(Modifier.fillMaxWidth(0.52f).height(10.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.07f)))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Brush.verticalGradient(listOf(BrandEmerald, BrandPurple)))
        )
        Text(
            title.uppercase(),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
private fun ShowcaseCard(item: ShowcaseItem) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.045f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "showcaseScale"
    )
    Column(
        Modifier
            .width(260.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = if (focused) -8f else 0f
                shadowElevation = if (focused) 24f else 0f
            }
            .clip(RoundedCornerShape(8.dp))
            .border(if (focused) 2.dp else 1.dp, if (focused) BrandEmerald else Line, RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1625), Color(0xFF050912))))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(118.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.linearGradient(item.colors)),
            contentAlignment = Alignment.Center
        ) {
            Text(item.title.uppercase(), color = Color.White.copy(alpha = 0.88f), fontSize = 18.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(14.dp))
            Text(item.source, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(CircleShape).background(BrandEmerald).padding(horizontal = 8.dp, vertical = 3.dp))
        }
        Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(item.subtitle, color = TextMuted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(item.meta, color = BrandPurple, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (item.progress > 0f) {
            Box(Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Line)) {
                Box(Modifier.fillMaxWidth(item.progress).height(4.dp).clip(CircleShape).background(BrandEmerald))
            }
        }
    }
}

@Composable
private fun SourceOverview(isTvLike: Boolean, installedAddons: Int, iptvUrl: String, serverUrl: String, traktName: String) {
    val sources = listOf(
        SourceStatus("Stremio addons", "$installedAddons connected", true),
        SourceStatus("IPTV + EPG", if (iptvUrl.isBlank()) "local playlist URL" else "playlist saved locally", iptvUrl.isNotBlank()),
        SourceStatus("Jellyfin / Emby / Plex", if (serverUrl.isBlank()) "personal server URL" else "server saved locally", serverUrl.isNotBlank()),
        SourceStatus("Cloud / NAS / Local files", "planned library import", false),
        SourceStatus("Trakt sync", if (traktName.isBlank()) "optional per profile" else traktName, traktName.isNotBlank())
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(sources) { source ->
            SourceStatusCard(source, isTvLike)
        }
    }
}

private data class SourceStatus(
    val title: String,
    val subtitle: String,
    val active: Boolean
)

@Composable
private fun SourceStatusCard(source: SourceStatus, isTvLike: Boolean) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.04f else 1f,
        animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
        label = "sourceScale"
    )
    val lift by animateFloatAsState(
        targetValue = if (focused) -6f else 0f,
        animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
        label = "sourceLift"
    )
    Column(
        Modifier
            .width(if (isTvLike) 220.dp else 188.dp)
            .height(92.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = lift
                shadowElevation = if (focused) 18f else 0f
            }
            .clip(RoundedCornerShape(8.dp))
            .border(
                if (focused) 2.dp else 1.dp,
                if (source.active) BrandEmerald else if (focused) BrandPurple else Line,
                RoundedCornerShape(8.dp)
            )
            .background(if (source.active) Color(0xFF082A2C) else Panel)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(source.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(source.subtitle, color = if (source.active) BrandEmerald else TextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupPanel(
    isTvLike: Boolean,
    addons: List<InstalledAddon>,
    selectedAddon: InstalledAddon?,
    selectedCatalog: StremioCatalog?,
    query: String,
    addUrl: String,
    iptvUrl: String,
    serverUrl: String,
    traktName: String,
    autoPlayBestSource: Boolean,
    minQuality: String,
    preferredSubtitle: String,
    onAddonSelected: (InstalledAddon) -> Unit,
    onCatalogSelected: (StremioCatalog) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onAddUrlChanged: (String) -> Unit,
    onInstallAddon: () -> Unit,
    onInstallTestAddon: () -> Unit,
    onIptvUrlChanged: (String) -> Unit,
    onServerUrlChanged: (String) -> Unit,
    onTraktNameChanged: (String) -> Unit,
    onAutoPlayBestSourceChanged: (Boolean) -> Unit,
    onMinQualityChanged: (String) -> Unit,
    onPreferredSubtitleChanged: (String) -> Unit,
    onRemoveAddon: (InstalledAddon) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xB007101D)),
        border = BorderStroke(1.dp, Line.copy(alpha = 0.82f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionTitle("Settings & Sources")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item { SettingsTile("Profile", "PINs, avatar, Trakt", "Ready", Icons.Rounded.Settings) }
                item { SettingsTile("Addons", "${addons.size} connected", "Stremio", Icons.Rounded.Storage) }
                item { SettingsTile("Servers", "Jellyfin, Emby, Plex", "Next", Icons.Rounded.VideoLibrary) }
                item { SettingsTile("IPTV", "M3U, EPG, favorites", "Next", Icons.Rounded.LiveTv) }
                item { SettingsTile("Privacy", "Local secrets", "Local", Icons.Rounded.FavoriteBorder) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = addUrl,
                    onValueChange = onAddUrlChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Addon manifest URL") },
                    placeholder = { Text("https://example.com/manifest.json") }
                )
                if (isTvLike) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        FocusButton("Connect", onInstallAddon)
                        FocusButton("Use KinovioStream test addon", onInstallTestAddon)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FocusButton("Connect", onInstallAddon, modifier = Modifier.fillMaxWidth())
                        FocusButton("Use KinovioStream test addon", onInstallTestAddon, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            if (addons.isNotEmpty()) {
                val playableAddons = addons.filter { it.hasResource("stream") }.ifEmpty { addons }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(playableAddons) { addon ->
                        Pill(
                            text = addon.manifest.name,
                            selected = addon.manifestUrl == selectedAddon?.manifestUrl,
                            onClick = { onAddonSelected(addon) }
                        )
                    }
                }
                selectedAddon?.let { addon ->
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(addon.manifest.catalogs.filter { catalog -> catalog.extra.none { it.isRequired } }) { catalog ->
                            Pill(
                                text = "${catalog.name} (${catalog.type})",
                                selected = catalog.id == selectedCatalog?.id && catalog.type == selectedCatalog.type,
                                onClick = { onCatalogSelected(catalog) }
                            )
                        }
                        item {
                            TextButton(onClick = { onRemoveAddon(addon) }) {
                                Text("Remove addon", color = BrandPurple)
                            }
                        }
                    }
                }
            }
            if (isTvLike) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Search connected catalog") }
                    )
                    FocusButton("Search", onSearch)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Search connected catalog") }
                    )
                    FocusButton("Search", onSearch, modifier = Modifier.fillMaxWidth())
                }
            }
            LocalSourceConfigPanel(
                isTvLike = isTvLike,
                iptvUrl = iptvUrl,
                serverUrl = serverUrl,
                traktName = traktName,
                onIptvUrlChanged = onIptvUrlChanged,
                onServerUrlChanged = onServerUrlChanged,
                onTraktNameChanged = onTraktNameChanged
            )
            PlaybackSettingsPanel(
                autoPlayBestSource = autoPlayBestSource,
                minQuality = minQuality,
                preferredSubtitle = preferredSubtitle,
                onAutoPlayBestSourceChanged = onAutoPlayBestSourceChanged,
                onMinQualityChanged = onMinQualityChanged,
                onPreferredSubtitleChanged = onPreferredSubtitleChanged
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalSourceConfigPanel(
    isTvLike: Boolean,
    iptvUrl: String,
    serverUrl: String,
    traktName: String,
    onIptvUrlChanged: (String) -> Unit,
    onServerUrlChanged: (String) -> Unit,
    onTraktNameChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Local Source Settings")
        if (isTvLike) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                LocalSourceField(
                    label = "IPTV playlist / EPG URL",
                    value = iptvUrl,
                    placeholder = "https://.../playlist.m3u or xmltv.xml",
                    onChange = onIptvUrlChanged,
                    modifier = Modifier.weight(1f)
                )
                LocalSourceField(
                    label = "Media server URL",
                    value = serverUrl,
                    placeholder = "https://jellyfin.example.com",
                    onChange = onServerUrlChanged,
                    modifier = Modifier.weight(1f)
                )
                LocalSourceField(
                    label = "Trakt profile label",
                    value = traktName,
                    placeholder = "Casa / Tiago / Kids",
                    onChange = onTraktNameChanged,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            LocalSourceField("IPTV playlist / EPG URL", iptvUrl, "https://.../playlist.m3u or xmltv.xml", onIptvUrlChanged)
            LocalSourceField("Media server URL", serverUrl, "https://jellyfin.example.com", onServerUrlChanged)
            LocalSourceField("Trakt profile label", traktName, "Casa / Tiago / Kids", onTraktNameChanged)
        }
        Text(
            "Saved on this device only. No credentials are sent anywhere until a connector is implemented.",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalSourceField(
    label: String,
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaybackSettingsPanel(
    autoPlayBestSource: Boolean,
    minQuality: String,
    preferredSubtitle: String,
    onAutoPlayBestSourceChanged: (Boolean) -> Unit,
    onMinQualityChanged: (String) -> Unit,
    onPreferredSubtitleChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Playback & Subtitles")
        Surface(
            color = Color.White.copy(alpha = 0.06f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Best-source autoplay", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Text("When enabled later, KTV can start the highest ranked source automatically.", color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                    Pill(if (autoPlayBestSource) "On" else "Off", selected = autoPlayBestSource, onClick = {
                        onAutoPlayBestSourceChanged(!autoPlayBestSource)
                    }, icon = Icons.Rounded.PlayArrow)
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Minimum quality", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("Any", "720p", "1080p", "4K")) { quality ->
                            Pill(quality, selected = minQuality == quality, onClick = { onMinQualityChanged(quality) })
                        }
                    }
                }
                OutlinedTextField(
                    value = preferredSubtitle,
                    onValueChange = onPreferredSubtitleChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Preferred subtitle language") },
                    placeholder = { Text("pt-BR, por, eng...") }
                )
            }
        }
    }
}

@Composable
private fun SettingsTile(title: String, subtitle: String, badge: String, icon: ImageVector) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.035f else 1f, tween(160), label = "settingsTileScale")
    Column(
        Modifier
            .width(210.dp)
            .height(96.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 20f else 0f
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (focused) Color(0xFF10263A) else PanelGlass)
            .border(1.dp, if (focused) BrandEmerald.copy(alpha = 0.48f) else LineSoft, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = BrandEmerald, modifier = Modifier.size(18.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
            Text(
                badge,
                color = Ink,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.clip(CircleShape).background(BrandEmerald).padding(horizontal = 7.dp, vertical = 3.dp)
            )
        }
        Text(subtitle, color = TextDim, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun MediaGrid(
    items: List<StremioMetaPreview>,
    catalogRows: Map<String, List<StremioMetaPreview>>,
    activeSection: String,
    isTvLike: Boolean,
    onOpenItem: (StremioMetaPreview) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(26.dp), modifier = Modifier.padding(bottom = 24.dp)) {
        if (activeSection == "Live") {
            SectionTitle("Live TV")
            LiveTvPreview()
            return@Column
        }
        if (catalogRows.isNotEmpty()) {
            val orderedKeys = catalogRows.keys
                .filter { title -> rowVisibleForSection(title, activeSection) }
                .sortedWith(compareBy<String> { rowPriorityForSection(it, activeSection) }.thenBy { it })
                .toList()
            orderedKeys.forEach { title ->
                val rowItems = catalogRows[title].orEmpty()
                if (rowItems.isNotEmpty()) {
                    SectionTitle(title)
                    PosterRail(rowItems, isTvLike, onOpenItem)
                }
            }
            if (orderedKeys.isEmpty()) {
                EmptyState("No collections matched this menu yet.")
            }
            return@Column
        }
        if (items.isNotEmpty()) {
            SectionTitle("Featured")
            PosterRail(items, isTvLike, onOpenItem)
        }
    }
}

private fun rowPriorityForSection(title: String, activeSection: String): Int {
    val text = title.lowercase()
    return when (activeSection) {
        "Anime" -> when {
            text.startsWith("anime -") -> 0
            text == "anime" -> 2
            else -> 1
        }
        "K-Drama" -> when {
            "k-drama" in text || "kdrama" in text -> 0
            text == "k-drama" -> 2
            else -> 1
        }
        "BL" -> when {
            text.startsWith("bl") -> 0
            else -> 1
        }
        else -> 0
    }
}

private fun rowVisibleForSection(title: String, activeSection: String): Boolean {
    val text = title.lowercase()
    if (activeSection == "TV Shows") {
        return ("show" in text || "series" in text || "serie" in text || "série" in text) &&
            "anime" !in text && "k-drama" !in text && "kdrama" !in text && "bl" !in text
    }
    return when (activeSection) {
        "Movies" -> "movie" in text || "filme" in text || "cinema" in text
        "TV Shows" -> "show" in text || "série" in text || "serie" in text || "series" in text || "anime" in text || "drama" in text || "bl" in text
        "K-Drama" -> "k-drama" in text || "kdrama" in text || "korean" in text || "coreano" in text || "drama" in text
        "Anime" -> "anime" in text || "animation" in text || "anima" in text
        "BL" -> "bl" in text || "boys love" in text || "yaoi" in text
        "Library" -> true
        else -> true
    }
}

private fun normalizeTitleForMatch(value: String): String =
    value.lowercase()
        .replace(Regex("""\[[^]]*]"""), " ")
        .replace(Regex("""\([^)]*\)"""), " ")
        .replace(Regex("""[^a-z0-9]+"""), " ")
        .trim()

internal fun absoluteEpisodeNumber(videos: List<StremioVideo>, target: StremioVideo): Int? {
    if (target.season == SOURCE_ORDER_SEASON) return target.episode
    if (target.season == null || target.episode == null) return target.episode
    val ordered = videos
        .filter { it.season != null && it.episode != null }
        .sortedWith(compareBy<StremioVideo> { it.season ?: Int.MAX_VALUE }.thenBy { it.episode ?: Int.MAX_VALUE })
    val index = ordered.indexOfFirst { candidate ->
        candidate.id == target.id ||
            (candidate.season == target.season && candidate.episode == target.episode)
    }
    return if (index >= 0) index + 1 else null
}

internal fun tmdbNumericId(value: String?): Int? =
    value?.substringAfter("tmdb:", missingDelimiterValue = "")
        ?.substringBefore(":")
        ?.toIntOrNull()

private val KnownAbsoluteAnimeOffsets = mapOf(
    46298 to mapOf(1 to 0, 2 to 26, 3 to 57, 4 to 87, 5 to 112, 6 to 131), // Hunter x Hunter
    37854 to mapOf(1 to 0, 2 to 61, 3 to 77, 4 to 91, 5 to 130, 6 to 143, 7 to 195, 8 to 206, 9 to 325, 10 to 336, 11 to 381, 12 to 405, 13 to 407, 14 to 421, 15 to 458, 16 to 491, 17 to 516, 18 to 522, 19 to 574, 20 to 628, 21 to 746, 22 to 891),
    46260 to mapOf(1 to 0, 2 to 35, 3 to 100, 4 to 141, 5 to 183), // Naruto
    31910 to mapOf(1 to 0, 2 to 32, 3 to 53, 4 to 71, 5 to 89, 6 to 112, 7 to 126, 8 to 143, 9 to 175, 10 to 197, 11 to 222, 12 to 243, 13 to 261, 14 to 283, 15 to 300, 16 to 321, 17 to 349, 18 to 375, 19 to 394, 20 to 432, 21 to 459),
    12609 to mapOf(1 to 0, 2 to 39, 3 to 74, 4 to 108, 5 to 139, 6 to 165, 7 to 200, 8 to 220, 9 to 254), // Dragon Ball Z
    68727 to mapOf(1 to 0, 2 to 28, 3 to 46, 4 to 77, 5 to 91), // Dragon Ball Super
    73223 to mapOf(1 to 0, 2 to 51, 3 to 103, 4 to 154), // Black Clover
    30984 to mapOf(1 to 0, 2 to 20, 3 to 41, 4 to 63, 5 to 91, 6 to 109, 7 to 132, 8 to 152, 9 to 167, 10 to 190, 11 to 206, 12 to 230, 13 to 266, 14 to 310, 15 to 342, 16 to 366),
    13916 to mapOf(1 to 0), // Death Note
    31911 to mapOf(1 to 0) // Fullmetal Alchemist Brotherhood
)

internal fun knownAbsoluteEpisodeNumber(preview: StremioMetaPreview, meta: StremioMeta?, video: StremioVideo): Int? {
    val season = video.season ?: return null
    val episode = video.episode ?: return null
    val tmdbId = tmdbNumericId(preview.id) ?: tmdbNumericId(meta?.id) ?: return null
    val offset = KnownAbsoluteAnimeOffsets[tmdbId]?.get(season) ?: return null
    return offset + episode
}

private fun shouldShowSourceOrder(preview: StremioMetaPreview, videos: List<StremioVideo>, seasons: List<Int>): Boolean {
    if (!preview.type.equals("series", ignoreCase = true)) return false
    if (videos.isEmpty()) return true
    val hasSeasonStructure = videos.any { it.season != null }
    if (!hasSeasonStructure) return true
    return seasons.size > 1 || seasons.any { it == 0 }
}

internal fun defaultEpisodeSeason(seasons: List<Int>, showSourceOrder: Boolean): Int? =
    when {
        seasons.any { it > 0 } -> seasons.first { it > 0 }
        seasons.isNotEmpty() -> seasons.first()
        showSourceOrder -> SOURCE_ORDER_SEASON
        else -> null
    }

private fun seasonLabel(season: Int): String =
    if (season == 0) "Specials" else "Season $season"

internal fun sortSeasonsForDisplay(seasons: List<Int>): List<Int> =
    seasons.distinct().sortedWith(compareBy<Int> { if (it == 0) Int.MAX_VALUE else it })

private fun sourceOrderEpisodes(preview: StremioMetaPreview, title: String, count: Int = SOURCE_ORDER_EPISODE_COUNT): List<StremioVideo> =
    (1..count).map { episode ->
        StremioVideo(
            id = "${preview.id}:1:$episode",
            title = "$title - Source episode $episode",
            season = SOURCE_ORDER_SEASON,
            episode = episode,
            thumbnail = preview.background ?: preview.poster,
            overview = "Uses absolute source numbering for anime, dubbed edits, and long Turkish cuts."
        )
    }

@Composable
private fun LiveTvPreview() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        item { LiveFeatureCard("Playlists", "M3U and Xtream sources", "Ready for user URLs", Icons.Rounded.LiveTv, listOf(BrandEmerald, BrandCyan)) }
        item { LiveFeatureCard("EPG Guide", "Program schedule and now playing", "Coming from your XMLTV", Icons.Rounded.CalendarMonth, listOf(BrandPurple, BrandCyan)) }
        item { LiveFeatureCard("Favorites", "Pin channels and hide groups", "Remote friendly", Icons.Rounded.FavoriteBorder, listOf(Gold, BrandEmerald)) }
    }
}

@Composable
private fun LibraryEmptyPanel() {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(52.dp).clip(CircleShape).background(Brush.linearGradient(listOf(BrandEmerald, BrandCyan))), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.VideoLibrary, contentDescription = "Library", tint = InkDeep, modifier = Modifier.size(26.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Your library starts here", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("Liked titles and started videos will appear in Watchlist and Continue Watching.", color = TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun LiveFeatureCard(title: String, subtitle: String, badge: String, icon: ImageVector, colors: List<Color>) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.035f else 1f, tween(160), label = "liveCardScale")
    Column(
        Modifier
            .width(270.dp)
            .height(158.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 22f else 0f
            }
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(colors.map { it.copy(alpha = 0.34f) } + listOf(PanelGlass)))
            .border(1.dp, if (focused) BrandEmerald.copy(alpha = 0.52f) else Color.White.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.30f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Text(badge, color = InkDeep, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.clip(CircleShape).background(BrandEmerald).padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun PosterRail(
    items: List<StremioMetaPreview>,
    isTvLike: Boolean,
    onOpenItem: (StremioMetaPreview) -> Unit
) {
    val cardWidth = if (isTvLike) 184.dp else 142.dp
    val railItems = items.take(60)
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxWidth()
            .height(if (isTvLike) 372.dp else 298.dp)
    ) {
        LazyRow(
            state = state,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = if (isTvLike) 42.dp else 2.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(railItems) { item ->
                Box(Modifier.width(cardWidth)) {
                    PosterCard(item, onOpenItem)
                }
            }
        }
        if (isTvLike && railItems.size > 4) {
            RailArrow(
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = "Previous titles",
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = {
                    scope.launch {
                        state.animateScrollToItem((state.firstVisibleItemIndex - 4).coerceAtLeast(0))
                    }
                }
            )
            RailArrow(
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Next titles",
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    scope.launch {
                        state.animateScrollToItem((state.firstVisibleItemIndex + 4).coerceAtMost((railItems.size - 1).coerceAtLeast(0)))
                    }
                }
            )
        }
    }
}

@Composable
private fun RailArrow(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Box(
        modifier
            .width(38.dp)
            .fillMaxHeight()
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Black.copy(alpha = 0.78f), Color.Black.copy(alpha = 0.28f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(if (focused) 38.dp else 32.dp)
                .clip(CircleShape)
                .background(if (focused) BrandEmerald else Color.White.copy(alpha = 0.14f))
                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = if (focused) InkDeep else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PosterQuickActions(onOpen: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onOpen),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = "Open", tint = InkDeep, modifier = Modifier.size(19.dp))
        }
        listOf(Icons.Rounded.FavoriteBorder, Icons.Rounded.Storage).forEach { icon ->
            Box(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.52f))
                    .border(1.dp, Color.White.copy(alpha = 0.24f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun PosterCard(
    item: StremioMetaPreview,
    onOpenItem: (StremioMetaPreview) -> Unit,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.07f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 380f),
        label = "posterScale"
    )
    val lift by animateFloatAsState(
        targetValue = if (focused) -14f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 380f),
        label = "posterLift"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "posterGlow"
    )
    Column(
        modifier
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = lift
                shadowElevation = if (focused) 36f else 2f
            }
            .clip(RoundedCornerShape(14.dp))
            .border(if (focused) 2.dp else 0.5.dp, if (focused) BrandEmerald else LineSoft, RoundedCornerShape(14.dp))
            .clickable { onOpenItem(item) }
            .background(
                Brush.verticalGradient(
                    listOf(
                        PanelGlass,
                        if (focused) BrandPurple.copy(alpha = 0.14f * glowAlpha) else Ink
                    )
                )
            )
            .padding(9.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF14242F), Color(0xFF081218)))),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.poster ?: item.background,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (item.poster == null && item.background == null) {
                Text(item.name.take(1), color = BrandEmerald, fontSize = 42.sp, fontWeight = FontWeight.Black)
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Ink.copy(alpha = 0.86f))))
            )
            if (focused) {
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.62f))
                        .border(1.dp, BrandEmerald.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Open", tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                shape = RoundedCornerShape(6.dp),
                color = InkDeep.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.5f))
            ) {
                Text(
                    item.type.uppercase(),
                    color = BrandEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
        Text(
            item.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp
        )
        val cardMeta = listOfNotNull(item.releaseInfo, item.type.uppercase()).joinToString(" - ")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                cardMeta,
                color = TextDim,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (focused) {
                Text("HD", color = BrandEmerald, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
        AnimatedVisibility(
            visible = focused,
            enter = fadeIn(tween(140)) + slideInVertically(initialOffsetY = { -it / 4 }),
            exit = fadeOut(tween(100))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PosterQuickActions(onOpen = { onOpenItem(item) })
                Text(
                    item.description ?: "Open details to choose episodes and sources.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DetailScreen(
    isTvLike: Boolean,
    client: StremioClient,
    addon: InstalledAddon,
    metadataAddon: InstalledAddon,
    preview: StremioMetaPreview,
    autoPlayBestSource: Boolean,
    minQuality: String,
    inWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onBack: () -> Unit,
    onPlay: (String, StremioStream, List<StremioStream>) -> Unit
) {
    val scope = rememberCoroutineScope()
    var meta by remember { mutableStateOf<StremioMeta?>(null) }
    var streams by remember { mutableStateOf<List<StremioStream>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    var showSources by remember(preview.id) { mutableStateOf(false) }
    var sourceTitle by remember(preview.id) { mutableStateOf(preview.name) }
    var selectedStream by remember(preview.id) { mutableStateOf<StremioStream?>(null) }
    var seasonModeAvailable by remember(preview.id) { mutableStateOf<Boolean?>(null) }
    var directModeAvailable by remember(preview.id) { mutableStateOf<Boolean?>(null) }
    var preferredBingeGroup by remember(preview.id) { mutableStateOf<String?>(null) }
    val title = meta?.name ?: preview.name
    val playable = streams.filter { isPlayableStreamCandidate(it) }
    val rankedPlayable = playable.sortedWith(streamComparator(minQuality, preferredBingeGroup))
    val sourceEmptyMessage = when {
        streams.isNotEmpty() -> "This source returned entries, but none expose an HTTP/HLS playable URL."
        preview.type.equals("series", ignoreCase = true) || meta?.videos.orEmpty().isNotEmpty() -> "Choose an episode or part first to load playable sources."
        else -> message.ifBlank { "No playable source was returned for this title." }
    }

    fun playSelected() {
        val stream = selectedStream ?: rankedPlayable.firstOrNull()
        if (selectedStream == null && rankedPlayable.size > 1 && !autoPlayBestSource) {
            selectedStream = rankedPlayable.firstOrNull()
            showSources = true
        } else if (stream?.playableUrl != null) {
            preferredBingeGroup = stream.behaviorHints?.bingeGroup ?: preferredBingeGroup
            onPlay(sourceTitle, stream, rankedPlayable.filter { it.playableUrl != stream.playableUrl })
        } else {
            message = "Select a playable source first."
            showSources = rankedPlayable.isNotEmpty()
        }
    }

    suspend fun loadFirstStreams(ids: List<String>): List<StremioStream> {
        var lastError: Throwable? = null
        ids.distinct().filter { it.isNotBlank() }.forEach { id ->
            val result = runCatching { client.streams(addon, preview.type, id) }
            result.onSuccess { streamsForId ->
                if (streamsForId.isNotEmpty()) return streamsForId
            }.onFailure { lastError = it }
        }
        lastError?.let { throw it }
        return emptyList()
    }

    suspend fun findMetadataByTitle(): StremioMeta? {
        if (metadataAddon.manifestUrl == addon.manifestUrl) return null
        val searchableCatalog = metadataAddon.manifest.catalogs.firstOrNull { catalog ->
            catalog.type.equals(preview.type, ignoreCase = true) &&
                catalog.extra.any { extra -> extra.name.equals("search", ignoreCase = true) }
        } ?: metadataAddon.manifest.catalogs.firstOrNull { it.type.equals(preview.type, ignoreCase = true) }
        searchableCatalog ?: return null
        val candidates = runCatching { client.catalog(metadataAddon, searchableCatalog, preview.name) }.getOrDefault(emptyList())
        val normalizedTitle = normalizeTitleForMatch(preview.name)
        val match = candidates.firstOrNull { normalizeTitleForMatch(it.name) == normalizedTitle }
            ?: candidates.firstOrNull { normalizeTitleForMatch(it.name).contains(normalizedTitle) || normalizedTitle.contains(normalizeTitleForMatch(it.name)) }
            ?: candidates.firstOrNull()
        return match?.let { runCatching { client.meta(metadataAddon, it.type, it.id) }.getOrNull() }
    }

    fun streamIdsForVideo(video: StremioVideo): List<String> {
        val absoluteEpisode = absoluteEpisodeNumber(meta?.videos.orEmpty(), video)
        val knownAnimeAbsoluteEpisode = knownAbsoluteEpisodeNumber(preview, meta, video)
        val structuredEpisodeIds = listOfNotNull(preview.id, meta?.id)
            .distinct()
            .flatMap { base ->
                listOfNotNull(
                    if (video.season != null && video.season != SOURCE_ORDER_SEASON && video.episode != null) "$base:${video.season}:${video.episode}" else null,
                    if (knownAnimeAbsoluteEpisode != null) "$base:1:$knownAnimeAbsoluteEpisode" else null,
                    if (knownAnimeAbsoluteEpisode != null) "$base:$knownAnimeAbsoluteEpisode" else null,
                    if (absoluteEpisode != null) "$base:1:$absoluteEpisode" else null,
                    if (video.episode != null) "$base:${video.episode}" else null
                )
            }
        val parentFromVideo = video.id.substringBeforeLast(":").takeIf { ":" in video.id }.orEmpty()
        return (listOf(video.id) + structuredEpisodeIds + parentFromVideo + preview.id + meta?.id.orEmpty())
            .distinct()
            .filter { it.isNotBlank() }
    }

    fun seasonProbeIds(video: StremioVideo): List<String> {
        val season = video.season ?: return emptyList()
        val episode = video.episode ?: return emptyList()
        return (listOf(video.id) + listOfNotNull(preview.id, meta?.id).flatMap { base ->
            listOf("$base:$season:$episode")
        }).distinct().filter { it.isNotBlank() }
    }

    fun directProbeIds(episode: Int): List<String> =
        listOfNotNull(preview.id, meta?.id)
            .flatMap { base -> listOf("$base:1:$episode", "$base:$episode") }
            .distinct()
            .filter { it.isNotBlank() }

    suspend fun hasPlayableStream(ids: List<String>): Boolean =
        runCatching { loadFirstStreams(ids).any { isPlayableStreamCandidate(it) } }.getOrDefault(false)

    fun loadStreamsFor(id: String, label: String) {
        scope.launch {
            loading = true
            sourceTitle = label
            showSources = false
            runCatching { loadFirstStreams(listOf(id, preview.id)) }
                .onSuccess {
                    streams = it
                    if (it.isEmpty()) {
                        selectedStream = null
                        message = "This item did not return playable sources."
                    } else if (it.none { stream -> isPlayableStreamCandidate(stream) }) {
                        selectedStream = null
                        message = "Sources were found, but none are playable by the internal player."
                    } else {
                        selectedStream = it.firstOrNull { stream -> isPlayableStreamCandidate(stream) }
                        message = ""
                        showSources = true
                    }
                }
                .onFailure {
                    streams = emptyList()
                    selectedStream = null
                    message = it.message ?: "Could not load sources."
                }
            loading = false
        }
    }

    fun playEpisode(video: StremioVideo) {
        val label = video.title.ifBlank {
            if (video.episode != null) "$title - E${video.episode}" else title
        }
        scope.launch {
            loading = true
            sourceTitle = label
            showSources = false
            message = ""
            val idsToTry = streamIdsForVideo(video)
            runCatching { loadFirstStreams(idsToTry) }
                .onSuccess { episodeStreams ->
                    streams = episodeStreams
                    val playableEpisodeStreams = episodeStreams.filter { isPlayableStreamCandidate(it) }
                    val rankedEpisodeStreams = playableEpisodeStreams.sortedWith(streamComparator(minQuality, preferredBingeGroup))
                    selectedStream = rankedEpisodeStreams.firstOrNull()
                    if (rankedEpisodeStreams.size == 1 || (rankedEpisodeStreams.size > 1 && autoPlayBestSource)) {
                        preferredBingeGroup = rankedEpisodeStreams.first().behaviorHints?.bingeGroup ?: preferredBingeGroup
                        onPlay(label, rankedEpisodeStreams.first(), rankedEpisodeStreams.drop(1))
                    } else if (rankedEpisodeStreams.size > 1) {
                        showSources = true
                        message = ""
                    } else if (episodeStreams.isNotEmpty()) {
                        message = "Sources were found for this episode, but none are playable by the internal player."
                    } else {
                        message = "No source was returned. Tried: ${idsToTry.take(4).joinToString(", ")}"
                    }
                }
                .onFailure {
                    streams = emptyList()
                    selectedStream = null
                    message = it.message ?: "Could not load episode sources."
                }
            loading = false
        }
    }

    LaunchedEffect(preview.id) {
        loading = true
        message = ""
        streams = emptyList()
        selectedStream = null
        seasonModeAvailable = null
        directModeAvailable = null
        val sourceMeta = runCatching { client.meta(addon, preview.type, preview.id) }.getOrNull()
        val fallbackMeta = if (sourceMeta == null && metadataAddon.manifestUrl != addon.manifestUrl) {
            runCatching { client.meta(metadataAddon, preview.type, preview.id) }.getOrNull()
        } else {
            null
        }
        val titleMatchedMeta = if ((sourceMeta ?: fallbackMeta)?.videos.orEmpty().isEmpty()) {
            findMetadataByTitle()
        } else {
            null
        }
        meta = listOfNotNull(sourceMeta, fallbackMeta, titleMatchedMeta)
            .maxByOrNull { candidate -> candidate.videos.size }
            ?: sourceMeta
            ?: fallbackMeta
            ?: titleMatchedMeta
        if (meta == null) {
            message = "Metadata fallback needed. Showing catalog data while loading sources."
        } else if (meta?.videos.orEmpty().isNotEmpty()) {
            message = ""
        }
        if (preview.type.equals("series", ignoreCase = true)) {
            val videos = meta?.videos.orEmpty()
            val seasonProbeVideo = videos.firstOrNull { (it.season ?: -1) > 0 && it.episode != null }
                ?: videos.firstOrNull { it.season == 0 && it.episode != null }
            seasonModeAvailable = seasonProbeVideo?.let { hasPlayableStream(seasonProbeIds(it)) }
            directModeAvailable = listOf(1, 2, 3).any { episode -> hasPlayableStream(directProbeIds(episode)) }
        }
        if (preview.type.equals("series", ignoreCase = true) || meta?.videos.orEmpty().isNotEmpty()) {
            selectedStream = null
        } else {
            runCatching { loadFirstStreams(listOf(preview.id, meta?.id.orEmpty())) }
                .onSuccess {
                    streams = it
                    selectedStream = it.firstOrNull { stream -> isPlayableStreamCandidate(stream) }
                    if (it.isEmpty()) message = "This addon did not return sources for this title."
                    if (it.isNotEmpty() && selectedStream == null) {
                        message = "Sources were found, but none are playable by the internal player."
                    }
                }
                .onFailure {
                    streams = emptyList()
                    selectedStream = null
                    message = it.message ?: "Could not load sources."
                }
            }
        loading = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        AsyncImage(
            model = meta?.background ?: preview.background ?: preview.poster,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.22f
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Ink), startY = 120f)))
        val backdropScale by animateFloatAsState(
            targetValue = if (loading) 1.06f else 1f,
            animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
            label = "backdropScale"
        )
        if (isTvLike) {
            TvDetailLayout(
                title = title,
                meta = meta,
                preview = preview,
                backdropScale = backdropScale,
                loading = loading,
                playable = playable,
                message = message,
                selectedStream = selectedStream,
                seasonModeAvailable = seasonModeAvailable,
                directModeAvailable = directModeAvailable,
                onBack = onBack,
                onChooseSource = { showSources = true },
                inWatchlist = inWatchlist,
                onToggleWatchlist = onToggleWatchlist,
                onPlaySelected = { playSelected() },
                onSourceSelected = {
                    selectedStream = it
                    preferredBingeGroup = it.behaviorHints?.bingeGroup ?: preferredBingeGroup
                },
                onEpisodeSelected = { video -> playEpisode(video) }
            )
        } else {
            MobileDetailLayout(
                title = title,
                meta = meta,
                preview = preview,
                backdropScale = backdropScale,
                loading = loading,
                playable = playable,
                message = message,
                selectedStream = selectedStream,
                seasonModeAvailable = seasonModeAvailable,
                directModeAvailable = directModeAvailable,
                onBack = onBack,
                onChooseSource = { showSources = true },
                inWatchlist = inWatchlist,
                onToggleWatchlist = onToggleWatchlist,
                onPlaySelected = { playSelected() },
                onSourceSelected = {
                    selectedStream = it
                    preferredBingeGroup = it.behaviorHints?.bingeGroup ?: preferredBingeGroup
                },
                onEpisodeSelected = { video -> playEpisode(video) }
            )
        }
        AnimatedVisibility(
            visible = showSources,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 160))
        ) {
            SourceDrawer(
                title = sourceTitle,
                streams = rankedPlayable,
                emptyMessage = sourceEmptyMessage,
                selectedStream = selectedStream,
                onClose = { showSources = false },
                onSelect = {
                    selectedStream = it
                    preferredBingeGroup = it.behaviorHints?.bingeGroup ?: preferredBingeGroup
                },
                onPlay = { playSelected() }
            )
        }
        AnimatedVisibility(
            visible = loading,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 22.dp),
            enter = fadeIn(tween(140)) + slideInVertically(initialOffsetY = { -it / 3 }),
            exit = fadeOut(tween(120))
        ) {
            LoadingToast("Loading sources")
        }
    }
}

@Composable
private fun LoadingToast(label: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.64f),
        border = BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.32f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(color = BrandEmerald, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TvDetailLayout(
    title: String,
    meta: StremioMeta?,
    preview: StremioMetaPreview,
    backdropScale: Float,
    loading: Boolean,
    playable: List<StremioStream>,
    message: String,
    selectedStream: StremioStream?,
    seasonModeAvailable: Boolean?,
    directModeAvailable: Boolean?,
    onBack: () -> Unit,
    onChooseSource: () -> Unit,
    inWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onPlaySelected: () -> Unit,
    onSourceSelected: (StremioStream) -> Unit,
    onEpisodeSelected: (StremioVideo) -> Unit
) {
    val firstEpisode = meta?.videos?.firstOrNull()
    val hasParts = preview.type.equals("series", ignoreCase = true) || meta?.videos.orEmpty().isNotEmpty()
    val allSeasons = sortSeasonsForDisplay(meta?.videos.orEmpty().mapNotNull { it.season })
    val showSeasons = seasonModeAvailable != false || directModeAvailable != true
    val seasons = if (showSeasons) allSeasons else emptyList()
    val showSourceOrder = directModeAvailable == true ||
        (shouldShowSourceOrder(preview, meta?.videos.orEmpty(), allSeasons) && (directModeAvailable != false || seasons.isEmpty()))
    val sourceOrderVideos = if (showSourceOrder) sourceOrderEpisodes(preview, title) else emptyList()
    var selectedSeason by remember(preview.id, seasons) {
        mutableStateOf(defaultEpisodeSeason(seasons, showSourceOrder))
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val visibleVideos = when (selectedSeason) {
        SOURCE_ORDER_SEASON -> sourceOrderVideos
        null -> meta?.videos.orEmpty()
        else -> meta?.videos.orEmpty().filter { it.season == selectedSeason }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(34.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            TvCinematicHero(
                title = title,
                meta = meta,
                preview = preview,
                loading = loading,
                playable = playable,
                message = message,
                selectedStream = selectedStream,
                hasParts = hasParts,
                inWatchlist = inWatchlist,
                onBack = onBack,
                onEpisodes = { scope.launch { listState.animateScrollToItem(2) } },
                onPlaySelected = onPlaySelected,
                onChooseSource = onChooseSource,
                onToggleWatchlist = onToggleWatchlist,
                onSourceSelected = onSourceSelected
            )
        }
        item {
            DetailSourceStatus(
                isSeries = hasParts,
                episodeCount = meta?.videos.orEmpty().size,
                playableCount = playable.size,
                loading = loading,
                message = message,
                onSources = onChooseSource
            )
        }
        item {
            EpisodeSection(
                videos = visibleVideos,
                preview = preview,
                scale = backdropScale,
                seasons = seasons,
                selectedSeason = selectedSeason,
                showSourceOrder = showSourceOrder,
                onSeasonSelected = { selectedSeason = it },
                onEpisodeSelected = onEpisodeSelected
            )
        }
    }
}

@Composable
private fun TvCinematicHero(
    title: String,
    meta: StremioMeta?,
    preview: StremioMetaPreview,
    loading: Boolean,
    playable: List<StremioStream>,
    message: String,
    selectedStream: StremioStream?,
    hasParts: Boolean,
    inWatchlist: Boolean,
    onBack: () -> Unit,
    onEpisodes: () -> Unit,
    onPlaySelected: () -> Unit,
    onChooseSource: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onSourceSelected: (StremioStream) -> Unit
) {
    val stills = meta?.videos.orEmpty().mapNotNull { it.thumbnail }.take(2)
    Box(
        Modifier
            .fillMaxWidth()
            .height(560.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(InkDeep)
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
    ) {
        AsyncImage(
            model = meta?.background ?: preview.background ?: preview.poster ?: meta?.poster,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.82f
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(InkDeep.copy(alpha = 0.96f), InkDeep.copy(alpha = 0.62f), Color.Transparent),
                        startX = 0f,
                        endX = Float.POSITIVE_INFINITY
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, InkDeep.copy(alpha = 0.12f), InkDeep.copy(alpha = 0.90f))))
        )
        Row(
            Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalArrangement = Arrangement.spacedBy(34.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    FocusButton("Back", onBack, icon = Icons.AutoMirrored.Rounded.ArrowBack)
                    BrandLockup(true)
                }
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                    HeroBadge(preview.type.replaceFirstChar { it.uppercase() })
                    (meta?.releaseInfo ?: preview.releaseInfo)?.let { HeroBadge(it.take(10)) }
                    meta?.runtime?.let { HeroBadge(it) }
                }
                Text(
                    title,
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 58.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    meta?.description ?: preview.description ?: "Choose from your connected sources and start watching from your own library.",
                    color = Color.White.copy(alpha = 0.86f),
                    fontSize = 17.sp,
                    lineHeight = 25.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 620.dp)
                )
                Text(
                    listOfNotNull(meta?.genres?.take(4)?.joinToString("  /  "), meta?.runtime).joinToString("  -  "),
                    color = TextMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                RatingStrip()
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    FocusButton(
                        if (hasParts) if (preview.type.equals("movie", ignoreCase = true)) "Choose Part" else "Episodes" else "Play Now",
                        if (hasParts) onEpisodes else onPlaySelected,
                        modifier = Modifier.width(190.dp),
                        icon = Icons.Rounded.PlayArrow
                    )
                    FocusButton("Sources", onChooseSource, modifier = Modifier.width(150.dp), icon = Icons.Rounded.Storage)
                    GlassIcon(if (inWatchlist) "Saved" else "Watchlist", Icons.Rounded.FavoriteBorder, onClick = onToggleWatchlist)
                }
            }
            Column(
                Modifier.width(300.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(178.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black.copy(alpha = 0.32f))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
                ) {
                    AsyncImage(
                        model = stills.getOrNull(0) ?: meta?.poster ?: preview.poster,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f)))))
                    Text("Preview", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.BottomStart).padding(14.dp))
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(178.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black.copy(alpha = 0.32f))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
                ) {
                    AsyncImage(
                        model = stills.getOrNull(1) ?: preview.background ?: meta?.background,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f)))))
                    Text("${meta?.videos.orEmpty().size.coerceAtLeast(playable.size)} items", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.BottomStart).padding(14.dp))
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.36f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.13f)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Available sources", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        when {
                            loading -> LoadingState()
                            hasParts && playable.isEmpty() -> Text("Choose an episode to load sources.", color = TextMuted, fontSize = 12.sp)
                            playable.isEmpty() -> Text(message.ifBlank { "No playable source returned yet." }, color = TextMuted, fontSize = 12.sp)
                            else -> SourceList(playable.take(2), selectedStream, onSourceSelected)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileDetailLayout(
    title: String,
    meta: StremioMeta?,
    preview: StremioMetaPreview,
    backdropScale: Float,
    loading: Boolean,
    playable: List<StremioStream>,
    message: String,
    selectedStream: StremioStream?,
    seasonModeAvailable: Boolean?,
    directModeAvailable: Boolean?,
    onBack: () -> Unit,
    onChooseSource: () -> Unit,
    inWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onPlaySelected: () -> Unit,
    onSourceSelected: (StremioStream) -> Unit,
    onEpisodeSelected: (StremioVideo) -> Unit
) {
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val firstEpisode = meta?.videos?.firstOrNull()
    val hasParts = preview.type.equals("series", ignoreCase = true) || meta?.videos.orEmpty().isNotEmpty()
    val allSeasons = sortSeasonsForDisplay(meta?.videos.orEmpty().mapNotNull { it.season })
    val showSeasons = seasonModeAvailable != false || directModeAvailable != true
    val seasons = if (showSeasons) allSeasons else emptyList()
    val showSourceOrder = directModeAvailable == true ||
        (shouldShowSourceOrder(preview, meta?.videos.orEmpty(), allSeasons) && (directModeAvailable != false || seasons.isEmpty()))
    val sourceOrderVideos = if (showSourceOrder) sourceOrderEpisodes(preview, title) else emptyList()
    var selectedSeason by remember(preview.id, seasons) {
        mutableStateOf(defaultEpisodeSeason(seasons, showSourceOrder))
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val visibleVideos = when (selectedSeason) {
        SOURCE_ORDER_SEASON -> sourceOrderVideos
        null -> meta?.videos.orEmpty()
        else -> meta?.videos.orEmpty().filter { it.season == selectedSeason }
    }
    val heroHeight = when {
        screenHeightDp < 720 -> 430.dp
        screenHeightDp < 820 -> 480.dp
        else -> 530.dp
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                AsyncImage(
                    model = meta?.poster ?: preview.poster ?: preview.background,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = backdropScale
                            scaleY = backdropScale
                        },
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.10f), Color.Transparent, Ink), startY = 70f)))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FocusButton("Back", onBack, modifier = Modifier.width(92.dp), icon = Icons.AutoMirrored.Rounded.ArrowBack)
                    GlassIcon("Sources", Icons.Rounded.Storage, onClick = onChooseSource)
                }
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(title.uppercase(), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 36.sp)
                    Text(
                        listOfNotNull(meta?.releaseInfo ?: preview.releaseInfo, meta?.genres?.joinToString(", "), meta?.runtime).joinToString(" - "),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    FocusButton(
                        if (hasParts) if (preview.type.equals("movie", ignoreCase = true)) "Choose a part" else "Choose an episode" else "Play",
                        if (hasParts) ({ scope.launch { listState.animateScrollToItem(1) }; Unit }) else onPlaySelected,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.PlayArrow
                    )
                    FocusButton("Sources", onChooseSource, modifier = Modifier.fillMaxWidth(), icon = Icons.Rounded.Storage)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassIcon("Library", Icons.Rounded.VideoLibrary)
                        GlassIcon(if (inWatchlist) "Saved" else "Like", Icons.Rounded.FavoriteBorder, onClick = onToggleWatchlist)
                        GlassIcon("Calendar", Icons.Rounded.CalendarMonth)
                    }
                    Text(
                        "$title - ${meta?.description ?: preview.description ?: "Open a source from your connected library."}",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    RatingStrip()
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailSourceStatus(
                    isSeries = hasParts,
                    episodeCount = meta?.videos.orEmpty().size,
                    playableCount = playable.size,
                    loading = loading,
                    message = message,
                    onSources = onChooseSource
                )
                firstEpisode?.let { episode ->
                    NextEpisodePanel(episode, onClick = { onEpisodeSelected(episode) })
                }
                EpisodeSection(
                    videos = visibleVideos,
                    preview = preview,
                    scale = backdropScale,
                    seasons = seasons,
                    selectedSeason = selectedSeason,
                    showSourceOrder = showSourceOrder,
                    onSeasonSelected = { selectedSeason = it },
                    onEpisodeSelected = onEpisodeSelected
                )
            }
        }
        if (!hasParts || playable.isNotEmpty() || message.isNotBlank() || loading) {
            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    when {
                        loading -> LoadingState()
                        hasParts && playable.isEmpty() && message.isBlank() -> Unit
                        playable.isEmpty() -> EmptyState(message.ifBlank { "No playable source returned yet." })
                        else -> SourceList(playable.take(3), selectedStream, onSourceSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSourceStatus(
    isSeries: Boolean,
    episodeCount: Int,
    playableCount: Int,
    loading: Boolean,
    message: String,
    onSources: () -> Unit
) {
    val status = when {
        loading -> "Loading metadata and sources"
        isSeries && episodeCount > 0 -> "$episodeCount episodes from metadata"
        isSeries -> "Episode metadata will appear when the addon returns it"
        playableCount > 0 -> "$playableCount playable source${if (playableCount == 1) "" else "s"}"
        message.isNotBlank() -> message
        else -> "Sources come from your connected addons"
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.34f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BrandEmerald, BrandCyan))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Storage, contentDescription = "Sources", tint = InkDeep, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(if (isSeries) "Episode sources" else "Movie sources", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(status, color = TextMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            GlassIcon("Sources", Icons.Rounded.SwapHoriz, onClick = onSources)
        }
    }
}

@Composable
private fun RatingStrip() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item { RatingPill("TVDB", "8.3") }
        item { RatingPill("TMDB", "8.8") }
        item { RatingPill("KTV", "92%") }
        item { RatingPill("Pop", "93%") }
    }
}

@Composable
private fun RatingPill(label: String, value: String) {
    Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.42f), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))) {
        Text(listOf(label, value).filter { it.isNotBlank() }.joinToString(" "), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun GlassIcon(label: String, icon: ImageVector? = null, onClick: (() -> Unit)? = null) {
    var focused by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(22.dp),
        color = if (focused) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, if (focused) BrandEmerald.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.16f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NextEpisodePanel(video: StremioVideo, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.015f else 1f, tween(150), label = "nextEpisodeScale")
    Row(
        Modifier
            .fillMaxWidth()
            .height(88.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 16f else 0f
            }
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = if (focused) 0.12f else 0.07f))
            .border(1.dp, if (focused) BrandEmerald.copy(alpha = 0.52f) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(112.dp).fillMaxHeight().clip(RoundedCornerShape(10.dp)).background(PanelSoft)) {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.24f)))
            Box(Modifier.align(Alignment.Center).size(34.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.56f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play next", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Up next", color = BrandEmerald, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Text(video.title.ifBlank { "Episode ${video.episode ?: 1}" }, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                listOfNotNull(video.season?.let { "S$it" }, video.episode?.let { "E$it" }, video.released?.take(10)).joinToString(" - ").ifBlank { "Tap to load sources" },
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EpisodeSection(
    videos: List<StremioVideo>,
    preview: StremioMetaPreview,
    scale: Float,
    seasons: List<Int>,
    selectedSeason: Int?,
    showSourceOrder: Boolean,
    onSeasonSelected: (Int?) -> Unit,
    onEpisodeSelected: (StremioVideo) -> Unit
) {
    var seasonMenuExpanded by remember { mutableStateOf(false) }
    var episodeFilter by remember(preview.id, selectedSeason) { mutableStateOf("") }
    val seasonOptions = buildList {
        if (showSourceOrder) add(SOURCE_ORDER_SEASON)
        addAll(seasons)
    }
    val selectedLabel = when (selectedSeason) {
        SOURCE_ORDER_SEASON -> "Direct 1-500"
        null -> "Episodes"
        else -> seasonLabel(selectedSeason)
    }
    val filteredVideos = remember(videos, episodeFilter) {
        val query = episodeFilter.trim().lowercase()
        if (query.isBlank()) {
            videos
        } else {
            videos.filter { video ->
                val haystack = listOfNotNull(
                    video.title,
                    video.overview,
                    video.episode?.toString(),
                    video.episode?.let { "e$it" },
                    video.season?.takeIf { it != SOURCE_ORDER_SEASON }?.let { "s$it" },
                    video.season?.takeIf { it != SOURCE_ORDER_SEASON }?.let { season -> video.episode?.let { "s${season}e$it" } }
                ).joinToString(" ").lowercase()
                haystack.contains(query)
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(if (preview.type.equals("movie", ignoreCase = true)) "Parts" else "Episodes")
            if (filteredVideos.isNotEmpty()) {
                Text("${filteredVideos.size}/${videos.size}", color = BrandEmerald, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (seasonOptions.size > 1) {
                Box {
                    FocusButton(
                        selectedLabel,
                        onClick = { seasonMenuExpanded = true },
                        modifier = Modifier.widthIn(min = 150.dp)
                    )
                    DropdownMenu(
                        expanded = seasonMenuExpanded,
                        onDismissRequest = { seasonMenuExpanded = false }
                    ) {
                        seasonOptions.forEach { option ->
                            val label = if (option == SOURCE_ORDER_SEASON) "Direct 1-500" else seasonLabel(option)
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    seasonMenuExpanded = false
                                    onSeasonSelected(option)
                                }
                            )
                        }
                    }
                }
            } else if (seasonOptions.size == 1) {
                Text(selectedLabel, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedTextField(
                value = episodeFilter,
                onValueChange = { episodeFilter = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Filter") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Filter episodes") }
            )
        }
        EpisodeRail(filteredVideos, preview, scale, onEpisodeSelected)
    }
}

@Composable
private fun EpisodeRail(
    videos: List<StremioVideo>,
    preview: StremioMetaPreview,
    scale: Float,
    onEpisodeSelected: (StremioVideo) -> Unit
) {
    val displayVideos = videos.take(120)
    if (displayVideos.isEmpty()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No episode metadata returned yet. Try Direct 1-500 for addons that use absolute, dubbed, anime, Turkish, or novela episode numbering.",
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 22.dp),
                textAlign = TextAlign.Center
            )
        }
        return
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(displayVideos) { video ->
            EpisodeCard(video, scale, onClick = { onEpisodeSelected(video) })
        }
    }
}

@Composable
private fun EpisodeCard(video: StremioVideo, scale: Float, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val isCompact = LocalConfiguration.current.screenWidthDp < 520
    val cardWidth = if (isCompact) 246.dp else 290.dp
    val imageHeight = if (isCompact) 132.dp else 150.dp
    val episodeBadge = when {
        video.season == SOURCE_ORDER_SEASON && video.episode != null -> "#${video.episode}"
        video.season == 0 && video.episode != null -> "SP ${video.episode}"
        video.season != null && video.episode != null -> "S${video.season} E${video.episode}"
        video.episode != null -> "E${video.episode}"
        else -> "Play"
    }
    val focusScale by animateFloatAsState(
        targetValue = if (focused) 1.045f else 1f,
        animationSpec = spring(dampingRatio = 0.68f, stiffness = 420f),
        label = "episodeScale"
    )
    Column(
        Modifier
            .width(cardWidth)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = focusScale
                scaleY = focusScale
                shadowElevation = if (focused) 26f else 0f
            }
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(12.dp))
                .border(if (focused) 2.dp else 1.dp, if (focused) BrandEmerald else Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                .background(PanelSoft)
        ) {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.74f)))))
            Box(
                Modifier
                    .align(Alignment.Center)
                    .size(if (focused) 48.dp else 40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = if (focused) 0.62f else 0.36f))
                    .border(1.dp, Color.White.copy(alpha = if (focused) 0.40f else 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play episode", tint = Color.White, modifier = Modifier.size(if (focused) 28.dp else 24.dp))
            }
            Text(
                episodeBadge,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.Black.copy(alpha = 0.56f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            if (video.title.contains("Continue", ignoreCase = true)) {
                Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.20f))) {
                    Box(Modifier.fillMaxWidth(0.28f).height(4.dp).background(BrandCyan))
                }
            }
            if (focused) {
                Box(Modifier.align(Alignment.TopEnd).padding(10.dp).size(26.dp).clip(CircleShape).background(BrandEmerald), contentAlignment = Alignment.Center) {
                    Text("OK", color = InkDeep, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }
        Text(video.released?.take(10) ?: "2026-04-16", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(video.title.ifBlank { episodeBadge }, color = Color.White, fontSize = if (isCompact) 15.sp else 17.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(video.overview ?: "Source metadata from your connected addon.", color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SourceDrawer(
    title: String,
    streams: List<StremioStream>,
    emptyMessage: String,
    selectedStream: StremioStream?,
    onClose: () -> Unit,
    onSelect: (StremioStream) -> Unit,
    onPlay: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(330.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xEE0D1A36), Color(0xF608152D), Color(0xFF04070F))
                )
            )
            .border(1.dp, BrandPurple.copy(alpha = 0.45f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Sources", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(title, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                FocusButton("Play", onPlay)
                FocusButton("Close", onClose)
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Pill("4K", selected = false, onClick = { }) }
            item { Pill("1080P", selected = true, onClick = { }) }
            item { Pill("Dolby", selected = false, onClick = { }) }
            item { Pill("SDR", selected = false, onClick = { }) }
        }
        if (streams.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    emptyMessage,
                    color = TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                itemsIndexed(streams) { index, stream ->
                    SourceTile(
                        stream = stream,
                        active = stream.playableUrl == selectedStream?.playableUrl || (selectedStream == null && index == 0),
                        onClick = { onSelect(stream) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceTile(stream: StremioStream, active: Boolean = false, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val quality = extractQuality(stream).ifBlank { "AUTO" }
    val server = extractServer(stream).ifBlank { stream.name ?: "Direct stream" }
    val size = stream.behaviorHints?.videoSize?.let { formatBytes(it) }
    val chips = streamFeatureChips(stream).ifEmpty { listOf(quality) }.take(6)
    val description = cleanStreamDescription(stream.description, stream.title ?: stream.behaviorHints?.filename ?: "")
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.04f else 1f,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "sourceTileScale"
    )
    Column(
        Modifier
            .width(250.dp)
            .height(170.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 20f else 0f
            }
            .clip(RoundedCornerShape(8.dp))
            .border(if (focused) 3.dp else 1.dp, if (focused) BrandEmerald else Line, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (focused || active) Color(0xFF122E36) else Panel)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                items(chips) { chip -> RatingPill(chip, "") }
                size?.let { item { RatingPill(it, "") } }
            }
            Text(server, color = Color.White, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                description ?: stream.title ?: stream.behaviorHints?.filename ?: "User-provided URL",
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stream.playableUrl?.substringBefore("?")?.substringAfterLast("/")?.take(18).orEmpty().ifBlank { "HTTP/HLS" }, color = BrandEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (active) "Active" else "Select", color = if (active) Gold else Color.White, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SourceList(
    streams: List<StremioStream>,
    selectedStream: StremioStream?,
    onSelect: (StremioStream) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Sources", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            items(streams) { stream ->
                val active = stream.playableUrl == selectedStream?.playableUrl
                FocusCard(onClick = { onSelect(stream) }) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(stream.name ?: "Direct stream", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(stream.title ?: stream.behaviorHints?.filename ?: stream.playableUrl.orEmpty(), color = TextMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Text(if (active) "Selected" else "Select", color = if (active) Gold else BrandEmerald, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

private data class StreamRequest(
    val url: String,
    val headers: Map<String, String>
)

private fun streamRequestFor(stream: StremioStream): StreamRequest {
    val raw = stream.url.orEmpty()
    val cleanUrl = raw.substringBefore("|").ifBlank { stream.playableUrl.orEmpty() }
    val headers = raw.substringAfter("|", "")
        .split("&")
        .mapNotNull { part ->
            val key = part.substringBefore("=", "").trim()
            val value = part.substringAfter("=", "").trim()
            if (key.isBlank() || value.isBlank()) null else key to java.net.URLDecoder.decode(value, "UTF-8")
        }
        .toMap()
    return StreamRequest(cleanUrl, headers)
}

private fun isPotentiallyPlayableUrl(url: String?): Boolean {
    val clean = url?.substringBefore("|")?.substringBefore("?")?.lowercase().orEmpty()
    if (!clean.startsWith("http://") && !clean.startsWith("https://")) return false
    if (clean.endsWith(".srt") || clean.endsWith(".ass") || clean.endsWith(".ssa") || clean.endsWith(".vtt")) return false
    if (clean.endsWith(".zip") || clean.endsWith(".rar") || clean.endsWith(".7z")) return false
    if (clean.endsWith(".jpg") || clean.endsWith(".jpeg") || clean.endsWith(".png") || clean.endsWith(".webp")) return false
    return true
}

private fun isPlayableStreamCandidate(stream: StremioStream): Boolean =
    isPotentiallyPlayableUrl(stream.playableUrl) && stream.infoHash.isNullOrBlank()

private fun buildPlayableMediaItem(stream: StremioStream, includeExternalSubtitles: Boolean = true): MediaItem {
    val request = streamRequestFor(stream)
    val url = request.url
    if (url.isBlank()) return MediaItem.fromUri("")
    val isHls = url.contains(".m3u8", ignoreCase = true) ||
        stream.name?.contains("hls", ignoreCase = true) == true ||
        url.contains("/proxy/hls", ignoreCase = true)
    val isDash = url.contains(".mpd", ignoreCase = true) ||
        stream.name?.contains("dash", ignoreCase = true) == true ||
        url.contains("/dash/", ignoreCase = true)
    val builder = MediaItem.Builder().setUri(url)
    if (isHls) {
        builder.setMimeType(MimeTypes.APPLICATION_M3U8)
    } else if (isDash) {
        builder.setMimeType(MimeTypes.APPLICATION_MPD)
    }
    val subtitleConfigs = if (includeExternalSubtitles) sortedSubtitlesForStream(stream).take(8).mapNotNull { subtitle ->
        val subtitleUrl = subtitle.url?.takeIf { it.startsWith("http://") || it.startsWith("https://") } ?: return@mapNotNull null
        val mimeType = subtitleMimeType(subtitleUrl)
        runCatching {
            MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleUrl))
                .setMimeType(mimeType)
                .setLanguage(subtitle.lang)
                .setLabel(subtitle.id ?: subtitle.lang ?: subtitleUrl.substringAfterLast('/'))
                .build()
        }.getOrNull()
    } else {
        emptyList()
    }
    if (subtitleConfigs.isNotEmpty()) {
        builder.setSubtitleConfigurations(subtitleConfigs)
    }
    stream.name?.let { builder.setMediaMetadata(androidx.media3.common.MediaMetadata.Builder().setTitle(it).build()) }
    return builder.build()
}

private fun subtitleMimeType(url: String): String {
    val clean = url.substringBefore("?").lowercase()
    return when {
        clean.endsWith(".vtt") || clean.endsWith(".webvtt") -> MimeTypes.TEXT_VTT
        clean.endsWith(".ass") || clean.endsWith(".ssa") -> MimeTypes.TEXT_SSA
        else -> MimeTypes.APPLICATION_SUBRIP
    }
}

private fun playbackErrorMessageFor(error: PlaybackException): String {
    val reason = when (error.errorCode) {
        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
        PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED,
        PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
        PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES -> "Codec not supported by this device"

        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
        PlaybackException.ERROR_CODE_TIMEOUT -> "Network timeout while loading source"

        PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Source server rejected playback request"

        PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED,
        PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "Source format is invalid or unsupported"

        else -> "Source failed to play"
    }
    return "$reason. ${error.message.orEmpty()}".trim()
}

@Composable
private fun PlayerScreen(
    title: String,
    stream: StremioStream,
    alternates: List<StremioStream>,
    onBack: () -> Unit
) {
    if (!isPlayableStreamCandidate(stream)) {
        InvalidSourceScreen(title = title, onBack = onBack)
        return
    }
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val initialWidthDp = LocalConfiguration.current.screenWidthDp
    val shouldLockLandscape = remember { initialWidthDp < 700 }
    val playerScope = rememberCoroutineScope()
    var controlsVisible by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(true) }
    var showSubtitlePanel by remember { mutableStateOf(false) }
    var showSources by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var playerViewError by remember { mutableStateOf<String?>(null) }
    var buffering by remember { mutableStateOf(true) }
    var currentStream by remember(stream.playableUrl) { mutableStateOf(stream) }
    var externalSubtitlesEnabled by remember(currentStream.playableUrl) { mutableStateOf(true) }
    var positionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(0L) }
    val triedSources = remember { mutableStateListOf<String>() }
    val playerReleased = remember { java.util.concurrent.atomic.AtomicBoolean(false) }
    val allPlayerSources = remember(stream.playableUrl, alternates) {
        (listOf(stream) + alternates)
            .filter { isPlayableStreamCandidate(it) }
            .distinctBy { it.playableUrl }
    }

    val playerResult = remember {
        runCatching {
            val requestHeaders = streamRequestFor(currentStream).headers
            val defaultHeaders = mapOf(
                "Accept" to "*/*",
                "Connection" to "keep-alive"
            ) + requestHeaders
            val httpFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("KinovioTV/0.1 Android")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(20_000)
                .setReadTimeoutMs(30_000)
                .setDefaultRequestProperties(defaultHeaders)
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    12_000,
                    45_000,
                    250,
                    1_500
                )
                .setTargetBufferBytes(64 * 1024 * 1024)
                .setPrioritizeTimeOverSizeThresholds(false)
                .setBackBuffer(3_000, false)
                .build()
            val trackSelector = DefaultTrackSelector(context).apply {
                parameters = buildUponParameters()
                    .setAllowVideoMixedMimeTypeAdaptiveness(true)
                    .setAllowVideoNonSeamlessAdaptiveness(true)
                    .setAllowAudioMixedMimeTypeAdaptiveness(true)
                    .setExceedVideoConstraintsIfNecessary(true)
                    .setExceedAudioConstraintsIfNecessary(true)
                    .setExceedRendererCapabilitiesIfNecessary(true)
                    .build()
            }
            ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(context).setDataSourceFactory(httpFactory))
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build()
                .apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                    volume = 1f
                }
        }
    }
    val player = playerResult.getOrNull()
    if (player == null) {
        PlayerErrorScreen(
            title = title,
            message = playerResult.exceptionOrNull()?.message ?: "Could not start the player.",
            onBack = onBack
        )
        return
    }

    DisposableEffect(activity, shouldLockLandscape) {
        val previousOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (shouldLockLandscape) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        onDispose {
            if (shouldLockLandscape) {
                activity?.requestedOrientation = previousOrientation
            }
        }
    }

    val switchTo: (StremioStream) -> Unit = { next ->
        if (next.playableUrl != currentStream.playableUrl && isPlayableStreamCandidate(next)) {
            triedSources.add(currentStream.playableUrl.orEmpty())
            currentStream = next
            playerError = null
            buffering = true
        }
    }

    LaunchedEffect(currentStream.playableUrl, externalSubtitlesEnabled) {
        runCatching {
            if (playerReleased.get()) return@LaunchedEffect
            positionMs = 0L
            durationMs = 0L
            buffering = true
            player.stop()
            player.clearMediaItems()
            player.setMediaItem(buildPlayableMediaItem(currentStream, includeExternalSubtitles = externalSubtitlesEnabled))
            player.playWhenReady = true
            player.prepare()
        }.onFailure {
            playerError = it.message ?: "Could not load this source."
            buffering = false
            controlsVisible = true
        }
    }

    LaunchedEffect(playerError) {
        val err = playerError ?: return@LaunchedEffect
        if (err.contains("Retrying this source", ignoreCase = true)) return@LaunchedEffect
        val next = allPlayerSources.firstOrNull {
            isPlayableStreamCandidate(it) && it.playableUrl !in triedSources && it.playableUrl != currentStream.playableUrl
        }
        if (next != null && !err.contains("blocked", ignoreCase = true)) {
            kotlinx.coroutines.delay(900)
            switchTo(next)
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                if (playerReleased.get()) return
                if (externalSubtitlesEnabled && currentStream.subtitles.isNotEmpty()) {
                    playerError = "Subtitle track failed. Retrying this source without external subtitles..."
                    externalSubtitlesEnabled = false
                    buffering = true
                    controlsVisible = true
                    return
                }
                triedSources.add(currentStream.playableUrl.orEmpty())
                playerError = playbackErrorMessageFor(error)
                playing = false
                buffering = false
                controlsVisible = true
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playerReleased.get()) return
                when (playbackState) {
                    Player.STATE_READY -> {
                        playerError = null
                        buffering = false
                        val rawDuration = runCatching { player.duration }.getOrDefault(0L)
                        durationMs = if (rawDuration > 0L && rawDuration != androidx.media3.common.C.TIME_UNSET) rawDuration else 0L
                    }
                    Player.STATE_BUFFERING -> buffering = true
                    Player.STATE_ENDED, Player.STATE_IDLE -> buffering = false
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (playerReleased.get()) return
                playing = isPlaying
            }
        }
        player.addListener(listener)
        val job = playerScope.launch {
            while (!playerReleased.get()) {
                kotlinx.coroutines.delay(500)
                positionMs = runCatching { player.currentPosition.coerceAtLeast(0L) }.getOrDefault(positionMs)
                if (durationMs <= 0) {
                    val rawDuration = runCatching { player.duration }.getOrDefault(0L)
                    durationMs = if (rawDuration > 0L && rawDuration != androidx.media3.common.C.TIME_UNSET) rawDuration else 0L
                }
            }
        }
        onDispose {
            playerReleased.set(true)
            job.cancel()
            runCatching {
                player.removeListener(listener)
                player.stop()
                player.clearMediaItems()
                player.release()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { controlsVisible = !controlsVisible }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                runCatching {
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                        controllerAutoShow = false
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                }.getOrElse {
                    playerViewError = it.message ?: "Could not create the video surface."
                    android.widget.FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                }
            },
            update = { view ->
                runCatching {
                    if (view is PlayerView) view.player = player
                }.onFailure {
                    playerViewError = it.message ?: "Could not attach the video surface."
                }
            }
        )
        playerViewError?.let { err ->
            PlayerInlineError(
                title = "Video surface failed",
                message = err,
                onBack = onBack,
                onSources = { showSources = true }
            )
        }
        if (controlsVisible) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.82f), Color.Transparent, Color.Black.copy(alpha = 0.90f))
                        )
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        FocusButton("Back", onBack, icon = Icons.AutoMirrored.Rounded.ArrowBack)
                        BrandLockup(false)
                    }
                    Pill("Kinovio Player", selected = true, onClick = { })
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (buffering && playerError == null) {
                            CircularProgressIndicator(color = BrandEmerald, strokeWidth = 3.dp, modifier = Modifier.size(56.dp))
                        } else {
                            FocusButton(
                                if (playing) "Pause" else "Play",
                                onClick = {
                                    if (!playerReleased.get()) runCatching { if (playing) player.pause() else player.play() }
                                },
                                modifier = Modifier.width(148.dp),
                                icon = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
                            )
                        }
                        playerError?.let { err ->
                            val nextSource = allPlayerSources.firstOrNull {
                                isPlayableStreamCandidate(it) && it.playableUrl !in triedSources && it.playableUrl != currentStream.playableUrl
                            }
                            Surface(
                                color = Color.Black.copy(alpha = 0.68f),
                                border = BorderStroke(1.dp, BrandPurple.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        if (nextSource != null) "This source failed. Trying another..." else "This source failed.",
                                        color = if (nextSource != null) BrandEmerald else Gold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        err,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 4.dp),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (allPlayerSources.size > 1) {
                                            Pill("Sources", selected = showSources, onClick = { showSources = true }, icon = Icons.Rounded.SwapHoriz)
                                        }
                                        Pill("Back", selected = false, onClick = onBack, icon = Icons.AutoMirrored.Rounded.ArrowBack)
                                    }
                                }
                            }
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(currentStream.name ?: "Direct stream", color = BrandEmerald, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                    }
                    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(progress)
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(listOf(BrandEmerald, BrandPurple)))
                            )
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(formatTime(positionMs), color = TextMuted, fontSize = 11.sp)
                            Text(formatTime(durationMs), color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        item {
                            Pill(
                                if (playing) "Pause" else "Play",
                                selected = playing,
                                onClick = { if (!playerReleased.get()) runCatching { if (playing) player.pause() else player.play() } },
                                icon = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
                            )
                        }
                        item {
                            Pill("-10s", selected = false, onClick = {
                                if (!playerReleased.get()) runCatching { player.seekTo((player.currentPosition - 10000).coerceAtLeast(0)) }
                            }, icon = Icons.Rounded.Replay10)
                        }
                        item {
                            Pill("+10s", selected = false, onClick = {
                                if (!playerReleased.get()) runCatching { player.seekTo(player.currentPosition + 10000) }
                            }, icon = Icons.Rounded.Forward10)
                        }
                        item {
                            val subtitleLabel = if (currentStream.subtitles.isEmpty()) "Subtitles" else "${currentStream.subtitles.size} subtitles"
                            Pill(subtitleLabel, selected = showSubtitlePanel, onClick = { showSubtitlePanel = !showSubtitlePanel }, icon = Icons.Rounded.Subtitles)
                        }
                        item { Pill("${alternates.size + 1} sources", selected = showSources, onClick = { showSources = !showSources }, icon = Icons.Rounded.SwapHoriz) }
                        item {
                            Text(
                                "User-provided URLs only.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = controlsVisible && showSubtitlePanel,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 34.dp, top = 82.dp),
            enter = fadeIn(animationSpec = tween(160)) + slideInVertically(initialOffsetY = { -it / 5 }),
            exit = fadeOut(animationSpec = tween(140))
        ) {
            SubtitlePanel(currentStream)
        }
        AnimatedVisibility(
            visible = controlsVisible && showSources,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 34.dp, top = 82.dp),
            enter = fadeIn(animationSpec = tween(160)) + slideInVertically(initialOffsetY = { -it / 5 }),
            exit = fadeOut(animationSpec = tween(140))
        ) {
            SourceSwitchPanel(
                current = currentStream,
                all = allPlayerSources,
                onPick = { picked ->
                    showSources = false
                    switchTo(picked)
                }
            )
        }
    }
}

@Composable
private fun InvalidSourceScreen(title: String, onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BrandLockup(false)
            Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("This source does not expose a playable HTTP/HLS URL yet.", color = TextMuted, fontSize = 14.sp)
            FocusButton("Back", onBack, modifier = Modifier.width(160.dp))
        }
    }
}

@Composable
private fun PlayerErrorScreen(title: String, message: String, onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BrandLockup(false)
            Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(message, color = TextMuted, fontSize = 14.sp, lineHeight = 19.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
            FocusButton("Back", onBack, modifier = Modifier.width(160.dp), icon = Icons.AutoMirrored.Rounded.ArrowBack)
        }
    }
}

@Composable
private fun PlayerInlineError(
    title: String,
    message: String,
    onBack: () -> Unit,
    onSources: () -> Unit
) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, BrandPurple.copy(alpha = 0.46f)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                Modifier.widthIn(max = 420.dp).padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, color = Gold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(message, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp, textAlign = TextAlign.Center, maxLines = 4, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    FocusButton("Sources", onSources, icon = Icons.Rounded.SwapHoriz)
                    FocusButton("Back", onBack, icon = Icons.AutoMirrored.Rounded.ArrowBack)
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s) else String.format("%d:%02d", m, s)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun SourceSwitchPanel(
    current: StremioStream,
    all: List<StremioStream>,
    onPick: (StremioStream) -> Unit
) {
    Column(
        Modifier
            .widthIn(max = 420.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(28.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Switch source", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
        all.forEachIndexed { index, s ->
            val active = s.playableUrl == current.playableUrl
            FocusCard(onClick = { if (!active) onPick(s) }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(s.name ?: "Direct stream", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            extractServer(s) + " " + extractQuality(s),
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (active) {
                        Text("ACTIVE", color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

private fun extractServer(stream: StremioStream): String {
    val title = stream.title ?: ""
    val lines = title.split("\n")
    val serverLine = lines.firstOrNull { it.contains("Server", ignoreCase = true) }
    return serverLine?.replace(Regex("[^\\p{L}\\p{N} \\-]+"), "")?.trim()?.take(28) ?: (stream.name ?: "")
}

private fun extractQuality(stream: StremioStream): String {
    val candidates = listOf("4K", "2160p", "1080p", "720p", "480p", "360p")
    val haystack = (stream.name ?: "") + " " + (stream.title ?: "")
    return candidates.firstOrNull { haystack.contains(it, ignoreCase = true) } ?: ""
}

private object StreamPresentationRegexes {
    val REMUX = Regex("""\bREMUX\b""", RegexOption.IGNORE_CASE)
    val BLURAY = Regex("""\b(BLURAY|BDRIP|BDREMUX)\b""", RegexOption.IGNORE_CASE)
    val WEBDL = Regex("""\b(WEB[- .]?DL|WEBDL)\b""", RegexOption.IGNORE_CASE)
    val WEBRIP = Regex("""\bWEB[- .]?RIP\b""", RegexOption.IGNORE_CASE)
    val HDTV = Regex("""\bHDTV\b""", RegexOption.IGNORE_CASE)
    val CAM = Regex("""\b(CAM|TS|TELESYNC|HDCAM)\b""", RegexOption.IGNORE_CASE)
    val HEVC = Regex("""\b(HEVC|X265|H265)\b""", RegexOption.IGNORE_CASE)
    val H264 = Regex("""\b(H264|X264|AVC)\b""", RegexOption.IGNORE_CASE)
    val AV1 = Regex("""\bAV1\b""", RegexOption.IGNORE_CASE)
    val HDR = Regex("""\bHDR(10\+?|10)?\b""", RegexOption.IGNORE_CASE)
    val DV = Regex("""\b(DV|DoVi|Dolby[\s._-]*Vision)\b""", RegexOption.IGNORE_CASE)
    val ATMOS = Regex("""\bATMOS\b""", RegexOption.IGNORE_CASE)
    val TRUEHD = Regex("""\bTRUEHD\b""", RegexOption.IGNORE_CASE)
    val DTS = Regex("""\bDTS(?:[- .]?(HD|X|MA))?\b""", RegexOption.IGNORE_CASE)
    val CH71 = Regex("""\b(7[._ ]?1)\b""", RegexOption.IGNORE_CASE)
    val CH51 = Regex("""\b(5[._ ]?1)\b""", RegexOption.IGNORE_CASE)
    val MULTI_AUDIO = Regex("""\b(MULTI|DUAL[ .-]?AUDIO|MULTI[ .-]?AUDIO)\b""", RegexOption.IGNORE_CASE)
    val LANGUAGE_HINT = Regex("""\b(PT[- ]?BR|PORTUGUESE|DUBLADO|DUAL|LEGENDADO|ENGLISH|SPANISH|JAPANESE|KOREAN|TURKISH)\b""", RegexOption.IGNORE_CASE)
}

private fun streamSearchBlob(stream: StremioStream): String =
    listOfNotNull(stream.name, stream.title, stream.description, stream.behaviorHints?.filename, stream.playableUrl)
        .joinToString(" ")

private fun streamResolutionScore(stream: StremioStream): Int {
    val text = streamSearchBlob(stream)
    return when {
        text.contains("2160p", true) || text.contains("4K", true) -> 4
        text.contains("1080p", true) -> 3
        text.contains("720p", true) -> 2
        StreamPresentationRegexes.CAM.containsMatchIn(text) -> 0
        else -> 1
    }
}

private fun streamReleaseScore(stream: StremioStream): Int {
    val text = streamSearchBlob(stream)
    return when {
        StreamPresentationRegexes.REMUX.containsMatchIn(text) -> 5
        StreamPresentationRegexes.BLURAY.containsMatchIn(text) -> 4
        StreamPresentationRegexes.WEBDL.containsMatchIn(text) -> 3
        StreamPresentationRegexes.WEBRIP.containsMatchIn(text) -> 2
        StreamPresentationRegexes.HDTV.containsMatchIn(text) -> 1
        else -> 0
    }
}

private fun streamFeatureChips(stream: StremioStream): List<String> {
    val text = streamSearchBlob(stream)
    return buildList {
        if (stream.behaviorHints?.cached == true) add("Cached")
        if (stream.infoHash != null || stream.sources.isNotEmpty()) add("Torrent")
        if (stream.playableUrl?.contains(".m3u8", ignoreCase = true) == true) add("HLS")
        extractQuality(stream).takeIf { it.isNotBlank() }?.let(::add)
        when {
            StreamPresentationRegexes.REMUX.containsMatchIn(text) -> add("REMUX")
            StreamPresentationRegexes.BLURAY.containsMatchIn(text) -> add("BluRay")
            StreamPresentationRegexes.WEBDL.containsMatchIn(text) -> add("WEB-DL")
            StreamPresentationRegexes.WEBRIP.containsMatchIn(text) -> add("WEBRip")
        }
        when {
            StreamPresentationRegexes.AV1.containsMatchIn(text) -> add("AV1")
            StreamPresentationRegexes.HEVC.containsMatchIn(text) -> add("HEVC")
            StreamPresentationRegexes.H264.containsMatchIn(text) -> add("H.264")
        }
        if (StreamPresentationRegexes.DV.containsMatchIn(text)) add("DV")
        if (StreamPresentationRegexes.HDR.containsMatchIn(text)) add("HDR")
        if (StreamPresentationRegexes.ATMOS.containsMatchIn(text)) add("Atmos")
        if (StreamPresentationRegexes.TRUEHD.containsMatchIn(text)) add("TrueHD")
        if (StreamPresentationRegexes.DTS.containsMatchIn(text)) add("DTS")
        if (StreamPresentationRegexes.CH71.containsMatchIn(text)) add("7.1")
        if (StreamPresentationRegexes.CH51.containsMatchIn(text)) add("5.1")
        if (StreamPresentationRegexes.MULTI_AUDIO.containsMatchIn(text)) add("Multi-audio")
        StreamPresentationRegexes.LANGUAGE_HINT.find(text)?.value?.uppercase()?.let(::add)
        if (stream.subtitles.isNotEmpty()) add("${stream.subtitles.size} subs")
        stream.behaviorHints?.bingeGroup?.takeIf { it.isNotBlank() }?.let { add("Binge") }
    }.distinct()
}

private fun minQualityScore(value: String): Int = when (value.lowercase()) {
    "4k", "2160p" -> 4
    "1080p" -> 3
    "720p" -> 2
    else -> 0
}

private fun streamComparator(minQuality: String = "Any", preferredBingeGroup: String? = null): Comparator<StremioStream> {
    val minimum = minQualityScore(minQuality)
    return compareByDescending<StremioStream> {
        minimum <= 0 || streamResolutionScore(it) >= minimum
    }
        .thenByDescending { !preferredBingeGroup.isNullOrBlank() && it.behaviorHints?.bingeGroup == preferredBingeGroup }
        .thenByDescending { it.behaviorHints?.cached == true }
        .thenByDescending { it.playableUrl?.startsWith("http", ignoreCase = true) == true }
        .thenByDescending { streamResolutionScore(it) }
        .thenByDescending { streamReleaseScore(it) }
        .thenByDescending { it.behaviorHints?.videoSize ?: 0L }
        .thenByDescending { it.sources.size }
        .thenBy { (it.name ?: it.title ?: "").lowercase() }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return ""
    val gb = bytes / 1_000_000_000.0
    return if (gb >= 1.0) String.format("%.2f GB", gb) else "${bytes / 1_000_000} MB"
}

private fun cleanStreamDescription(raw: String?, title: String): String? {
    if (raw.isNullOrBlank()) return null
    val sizeLinePattern = Regex("""^.*\d+(\.\d+)?\s*(GB|MB|KB|TB).*$""", RegexOption.IGNORE_CASE)
    val mdNoise = Regex("""[`*_]{1,4}""")
    val cleaned = raw.lines()
        .map { it.trim() }
        .filter { line ->
            line.isNotBlank() &&
                !line.equals("None", ignoreCase = true) &&
                !line.equals(title, ignoreCase = true) &&
                !Regex("""^\[.+]$""").matches(line) &&
                !sizeLinePattern.matches(line)
        }
        .joinToString("\n") { it.replace(mdNoise, "").trim() }
        .trim()
    return cleaned.takeIf { it.isNotBlank() }
}

private val SubtitleEpisodeRegex = Regex("""s\d{1,2}e\d{1,2}|\d{1,2}x\d{1,2}""", RegexOption.IGNORE_CASE)
private val SubtitleSplitSeasonRegex = Regex("""(s\d{1,2})\s+(e\d{1,2})""", RegexOption.IGNORE_CASE)
private val SubtitleSeparatorRegex = Regex("""[.\-_\s]+""")
private val SubtitleFileExtensionRegex = Regex("""\.(mkv|mp4|avi|mov|wmv|flv|m4v|ts|m2ts|srt|vtt|ass|ssa)$""", RegexOption.IGNORE_CASE)
private val SubtitleNoiseTokens = setOf(
    "ntsc", "pal", "proper", "repack", "extended", "theatrical", "imax", "hdr", "hdr10", "sdr", "dv",
    "dubbed", "subbed", "rip", "complete", "internal", "limited", "english", "french", "german",
    "spanish", "italian", "portuguese", "japanese", "korean", "turkish", "arabic", "hindi", "subs", "sub"
)

private fun subtitleTokenWeight(token: String): Int = when {
    token.all { it.isDigit() } -> 0
    SubtitleEpisodeRegex.matches(token) -> 8
    token in setOf("480p", "576p", "720p", "1080p", "1080i", "2160p", "4k", "uhd") -> 3
    token in setOf("bluray", "bdrip", "bdremux", "webrip", "webdl", "web", "hdtv", "remux") -> 4
    token in setOf("x264", "x265", "h264", "h265", "hevc", "avc", "av1", "aac", "ac3", "eac3", "dts", "atmos") -> 2
    token in SubtitleNoiseTokens -> 1
    else -> 10
}

private fun weightedSubtitleScore(streamSource: String, subtitleId: String): Int {
    if (streamSource.isBlank() || subtitleId.isBlank()) return 0
    fun normalize(value: String) = SubtitleSplitSeasonRegex.replace(value) { it.groupValues[1] + it.groupValues[2] }
    fun bodyAndGroup(value: String): Pair<String, String?> {
        val index = value.lastIndexOf('-')
        return if (index >= 0) {
            val group = value.substring(index + 1).lowercase().trim()
                .replace(SubtitleFileExtensionRegex, "")
                .ifBlank { null }
            value.substring(0, index) to group
        } else {
            value to null
        }
    }
    val (streamBody, streamGroup) = bodyAndGroup(normalize(streamSource))
    val (subBody, subGroup) = bodyAndGroup(normalize(subtitleId.replace(Regex("""^\[[^]]+]"""), "").trim()))
    val streamTokens = streamBody.lowercase().split(SubtitleSeparatorRegex).filter { it.length > 1 }
    val subTokens = subBody.lowercase().split(SubtitleSeparatorRegex).filter { it.length > 1 }.toSet()
    var total = 0
    var matched = 0
    streamTokens.forEach { token ->
        val weight = subtitleTokenWeight(token)
        if (weight > 0) {
            total += weight
            if (token in subTokens) matched += weight
        }
    }
    if (streamGroup != null) {
        total += 5
        if (streamGroup == subGroup) matched += 5
    }
    return if (total == 0) 0 else (matched * 100 / total).coerceIn(0, 100)
}

private fun sortedSubtitlesForStream(stream: StremioStream): List<StremioSubtitle> {
    val streamName = stream.behaviorHints?.filename ?: stream.title ?: stream.name.orEmpty()
    fun languagePriority(lang: String?): Int {
        val value = lang.orEmpty().lowercase()
        return when {
            value in setOf("pt", "por", "pt-br", "pt_br", "portuguese", "br") -> 4
            value in setOf("en", "eng", "english") -> 3
            value.isNotBlank() -> 2
            else -> 1
        }
    }
    return stream.subtitles.sortedWith(
        compareByDescending<StremioSubtitle> { languagePriority(it.lang) }
            .thenByDescending { weightedSubtitleScore(streamName, it.id ?: it.url?.substringAfterLast('/').orEmpty()) }
            .thenBy { it.id ?: it.url.orEmpty() }
    )
}

@Composable
private fun SubtitlePanel(stream: StremioStream) {
    val subtitles = sortedSubtitlesForStream(stream).take(8)
    Column(
        Modifier
            .width(340.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black.copy(alpha = 0.76f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(28.dp))
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Subtitles", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Icon(Icons.Rounded.Subtitles, contentDescription = "Subtitles", tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.10f)))
        if (subtitles.isEmpty()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("No subtitle tracks returned by this source.", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("External subtitle search and local subtitle loading will plug into this panel later.", color = TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
            }
        } else {
            subtitles.forEachIndexed { index, subtitle ->
                val label = subtitle.id ?: subtitle.url?.substringAfterLast('/') ?: "Subtitle ${index + 1}"
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label, color = Color.White, fontSize = 16.sp, lineHeight = 22.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(subtitle.lang ?: "auto", color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = BrandEmerald)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Panel)
            .border(1.dp, Line, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message.ifBlank { "Nothing to show yet." },
            color = TextMuted,
            modifier = Modifier.padding(24.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun Pill(text: String, selected: Boolean, onClick: () -> Unit, icon: ImageVector? = null) {
    var focused by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (selected) BrandEmerald else PanelSoft,
        border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) Color.White else Line)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = text, tint = if (selected) Ink else Color.White, modifier = Modifier.size(17.dp))
            }
            Text(
                text,
                color = if (selected) Ink else Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FocusButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.025f else 1f,
        animationSpec = tween(160, easing = FastOutSlowInEasing),
        label = "focusButtonScale"
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (focused) 18f else 0f
            }
            .border(if (focused) 2.dp else 0.dp, Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald, contentColor = Ink)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun FocusCard(onClick: () -> Unit, content: @Composable () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick)
            .border(if (focused) 3.dp else 1.dp, if (focused) BrandEmerald else Line, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(8.dp)
    ) {
        content()
    }
}
