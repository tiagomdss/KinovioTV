package tv.kinovio.app.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import tv.kinovio.app.stremio.StremioMeta
import tv.kinovio.app.stremio.StremioMetaPreview
import tv.kinovio.app.stremio.StremioVideo

class EpisodeLogicTest {
    @Test
    fun sortSeasonsForDisplay_keepsSpecialsLast() {
        assertEquals(listOf(1, 2, 3, 0), sortSeasonsForDisplay(listOf(0, 2, 1, 0, 3)))
    }

    @Test
    fun defaultEpisodeSeason_prefersNormalSeasonOverSpecialsAndDirect() {
        assertEquals(1, defaultEpisodeSeason(listOf(0, 1, 2), showSourceOrder = true))
    }

    @Test
    fun defaultEpisodeSeason_usesDirectWhenNoSeasonsExist() {
        assertEquals(SOURCE_ORDER_SEASON, defaultEpisodeSeason(emptyList(), showSourceOrder = true))
    }

    @Test
    fun defaultEpisodeSeason_returnsSpecialsOnlyWhenItIsTheOnlySeason() {
        assertEquals(0, defaultEpisodeSeason(listOf(0), showSourceOrder = true))
    }

    @Test
    fun tmdbNumericId_extractsIdFromTmdbPrefix() {
        assertEquals(196950, tmdbNumericId("tmdb:196950"))
        assertEquals(196950, tmdbNumericId("tmdb:196950:1:2"))
        assertNull(tmdbNumericId("tt1234567"))
    }

    @Test
    fun absoluteEpisodeNumber_calculatesAcrossSeasons() {
        val videos = listOf(
            StremioVideo(id = "show:1:1", season = 1, episode = 1),
            StremioVideo(id = "show:1:2", season = 1, episode = 2),
            StremioVideo(id = "show:2:1", season = 2, episode = 1)
        )

        assertEquals(3, absoluteEpisodeNumber(videos, videos[2]))
    }

    @Test
    fun knownAbsoluteEpisodeNumber_usesAnimeOffsets() {
        val preview = StremioMetaPreview(id = "tmdb:46260", type = "series", name = "Naruto")
        val meta = StremioMeta(id = "tmdb:46260", type = "series", name = "Naruto")
        val episode = StremioVideo(id = "tmdb:46260:2:1", season = 2, episode = 1)

        assertEquals(36, knownAbsoluteEpisodeNumber(preview, meta, episode))
    }
}
