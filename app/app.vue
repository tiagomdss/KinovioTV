<script setup>
import { ref, onMounted, nextTick, computed } from 'vue';
import { initSpatialNavigation, setActiveElementId } from '~/utils/spatial-focus';
import { getInstalledAddons, installAddon, uninstallAddon, getStreamsFromAddons, getCatalogFromAddon, searchCatalogFromAddon, getMetaDetails, getSubtitlesFromAddons } from '~/utils/stremio';
import { getJellyfinServers, connectJellyfinServer, disconnectJellyfinServer } from '~/utils/jellyfin';
import { getTMDBDetails, getTMDBSeasonEpisodes, getTMDBDiscover, getTMDBExternalIds } from '~/utils/tmdb';
import { isTraktConnected, disconnectTrakt, getDeviceCode, pollDeviceToken } from '~/utils/trakt';
import { isNsfwContent } from '~/utils/nsfw';

// ─── Cloud Sync & Authentication State ───
const user = ref(null);
const checkSessionLoading = ref(true);

const syncData = async (payload) => {
  if (!user.value) return;
  try {
    const res = await $fetch('/api/user/sync', {
      method: 'POST',
      body: payload
    });
    if (res.success && res.user) {
      user.value = res.user;
    }
  } catch (err) {
    console.error('Erro ao sincronizar dados com o servidor:', err);
  }
};

const checkSession = async () => {
  try {
    const data = await $fetch('/api/auth/session');
    if (data.loggedIn && data.user) {
      user.value = data.user;
      profiles.value = data.user.profiles || [];
      
      // Carregar configurações da nuvem
      if (data.user.settings) {
        const s = data.user.settings;
        if (s.debridToken !== undefined) debridToken.value = s.debridToken;
        if (s.debridProvider !== undefined) debridProvider.value = s.debridProvider;
        if (s.tmdbApiKey !== undefined) tmdbApiKey.value = s.tmdbApiKey;
        if (s.stremioAddons) {
          localStorage.setItem('ktv_stremio_addons', JSON.stringify(s.stremioAddons));
        }
        if (s.jellyfinServers) {
          localStorage.setItem('ktv_jellyfin_servers', JSON.stringify(s.jellyfinServers));
        }
        if (s.iptvPlaylists) {
          localStorage.setItem('ktv_iptv_playlists', JSON.stringify(s.iptvPlaylists));
          iptvPlaylists.value = s.iptvPlaylists;
        }
      }

      const saved = localStorage.getItem('ktv_current_profile');
      if (saved) {
        const found = profiles.value.find(p => p.name === saved);
        if (found) {
          handleSelectProfile(found);
        }
      }
    }
  } catch (err) {
    console.warn('Erro ao verificar sessão:', err);
  } finally {
    checkSessionLoading.value = false;
  }
};

// ─── Screen State ───
const activeScreen = ref('main'); // main | detail | player
const currentTab = ref('home');   // home | search | favorites | livetv | profile | settings

// ─── Profiles State ───
const profiles = ref([]);
const currentProfile = ref(null);

// ─── Catalog Data ───
const popularMovies = ref([]);
const popularSeries = ref([]);
const animeMovies = ref([]);
const animeSeries = ref([]);
const kdramaMovies = ref([]);
const kdramaSeries = ref([]);
const blMovies = ref([]);
const blSeries = ref([]);
const searchResults = ref([]);
const searchedOnce = ref(false);
const loadingMovies = ref(true);
const loadingSeries = ref(true);
const currentSubtitles = ref([]);

// ─── Profile-Specific State (Loaded dynamically per profile) ───
const continueWatching = ref([]);
const favorites = ref([]);

// ─── Featured Show (for detail screen fallback) ───
const featuredShow = ref({
  title: 'SPIDER-NOIR',
  imdbId: 'tt1190634',
  description: 'Um detetive particular idoso e sem sorte na Nova York dos anos 1930 é forçado a lidar com sua vida passada como o único super-herói da cidade.',
  year: '2026',
  genre: 'Ação, Crime, Noir',
  duration: '48m',
  backdrop: 'https://image.tmdb.org/t/p/w780/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg',
  poster: 'https://image.tmdb.org/t/p/w342/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg',
  ratings: [
    { label: 'IMDb 8.4', color: 'badge-imdb' },
    { label: 'HD', color: 'badge-hd' },
  ],
  episodes: [
    { id: 'ep-1', title: 'Uma Teia de Sombras', date: '2026-05-15', duration: '45m', watched: true, progress: 100, description: 'Ben Reilly investiga o desaparecimento de um jovem jornalista.', thumbnail: 'https://image.tmdb.org/t/p/w300/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg' },
    { id: 'ep-2', title: 'A Femme Fatale e o Monstro', date: '2026-05-22', duration: '48m', watched: false, progress: 35, description: 'Uma mulher misteriosa entra no escritório de Reilly.', thumbnail: 'https://image.tmdb.org/t/p/w300/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg' },
    { id: 'ep-3', title: 'O Retorno do Passado', date: '2026-05-29', duration: '50m', watched: false, progress: 0, description: 'Antigos inimigos resgatam fantasmas do passado.', thumbnail: 'https://image.tmdb.org/t/p/w300/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg' },
  ]
});

// ─── Detail + Player State ───
const selectedShow = ref(null);
const activeStreams = ref([]);
const currentPlayingStream = ref(null);

// ─── IPTV ───
const iptvChannels = ref([
  { name: 'HBO HD', category: 'Filmes', logo: 'https://image.tmdb.org/t/p/w92/tuomPhY2UtuPTqqFnKMVHo0WBfo.jpg', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4' },
  { name: 'Telecine Premium', category: 'Filmes', logo: 'https://image.tmdb.org/t/p/w92/3E0RkIEQrrGYjanwMpegJxuOx8V.jpg', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4' },
  { name: 'Discovery Channel', category: 'Docs', logo: 'https://image.tmdb.org/t/p/w92/A3H7gm41ASRI1i6MdOEm4BHNHBK.jpg', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4' },
  { name: 'Globo HD', category: 'Aberto', logo: 'https://image.tmdb.org/t/p/w92/wwemzKWzjKYJFfCeiB57q3r4Bcm.jpg', url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4' },
]);
const iptvPlaylists = ref([
  { name: 'Canais de Teste', url: 'https://raw.githubusercontent.com/iptv-org/iptv/master/channels/br.m3u' }
]);

// ─── Settings State ───
const installedAddons = ref([]);
const connectedServers = ref([]);
const debridProvider = ref('realdebrid');
const debridToken = ref('');
const tmdbApiKey = ref('');
const enableNsfw = ref(false);
const seasonViewMode = ref('posters');

// ─── Parental Control PIN Lock ───
const showPinModal = ref(false);
const pinInput = ref('');
const pinError = ref(false);
const pinTargetAction = ref(null);

const triggerPinRequired = (actionCallback) => {
  pinTargetAction.value = actionCallback;
  pinInput.value = '';
  pinError.value = false;
  showPinModal.value = true;
};

const handlePinKey = (num) => {
  if (pinInput.value.length < 4) {
    pinInput.value += num;
  }
  if (pinInput.value.length === 4) {
    verifyPin();
  }
};

const handlePinBackspace = () => {
  pinInput.value = pinInput.value.slice(0, -1);
};

const verifyPin = () => {
  const correctPin = '1234'; // PIN Padrão
  if (pinInput.value === correctPin) {
    showPinModal.value = false;
    if (typeof pinTargetAction.value === 'function') {
      pinTargetAction.value();
    }
    pinTargetAction.value = null;
  } else {
    pinError.value = true;
    pinInput.value = '';
    setTimeout(() => { pinError.value = false; }, 2000);
  }
};

const handleToggleNsfw = () => {
  if (!enableNsfw.value) {
    // Se está desativado e querem desbloquear, pede PIN!
    triggerPinRequired(() => {
      enableNsfw.value = true;
      if (typeof window !== 'undefined') {
        localStorage.setItem('ktv_enable_nsfw', 'true');
        window.dispatchEvent(new Event('ktv-nsfw-updated'));
      }
      showToast('Controle Parental desativado. Conteúdo +18 desbloqueado.');
    });
  } else {
    // Se está habilitado e querem ativar o controle/bloquear, bloqueia direto sem PIN
    enableNsfw.value = false;
    if (typeof window !== 'undefined') {
      localStorage.setItem('ktv_enable_nsfw', 'false');
      window.dispatchEvent(new Event('ktv-nsfw-updated'));
    }
    showToast('Controle Parental ativado. Conteúdo +18 desfoque/filtro habilitado.');
  }
};

const handleUpdateSeasonViewMode = (mode) => {
  seasonViewMode.value = mode;
  if (typeof window !== 'undefined') {
    localStorage.setItem('ktv_season_view_mode', mode);
  }
  showToast(`Temporadas serão exibidas como: ${mode === 'posters' ? 'Pôsteres' : 'Botões'}`);
};

const traktConnected = ref(false);
const showTraktModal = ref(false);
const traktUserCode = ref('');
const traktVerificationUrl = ref('');
let traktPollInterval = null;

// ─── Toasts ───
const toasts = ref([]);
const showToast = (message, type = 'success') => {
  const id = Date.now();
  toasts.value.push({ id, message, type });
  setTimeout(() => { toasts.value = toasts.value.filter(t => t.id !== id); }, 3500);
};

// Geração de Recomendações Reais baseadas no perfil ativo (Misturando Filmes, Séries, Animes, K-Drama, BL)
const recommendedMedia = computed(() => {
  const history = [...continueWatching.value, ...favorites.value];
  const combinedCatalog = [
    ...popularMovies.value, 
    ...popularSeries.value, 
    ...animeMovies.value, 
    ...animeSeries.value, 
    ...kdramaMovies.value, 
    ...kdramaSeries.value, 
    ...blMovies.value, 
    ...blSeries.value
  ];

  if (history.length === 0) {
    // Intercalar categorias para gerar um mix equilibrado de sugestões iniciais
    const mix = [];
    const maxLength = Math.max(
      popularMovies.value.length,
      popularSeries.value.length,
      animeSeries.value.length,
      kdramaSeries.value.length,
      blSeries.value.length
    );
    for (let i = 0; i < maxLength; i++) {
      if (popularMovies.value[i]) mix.push(popularMovies.value[i]);
      if (popularSeries.value[i]) mix.push(popularSeries.value[i]);
      if (animeSeries.value[i]) mix.push(animeSeries.value[i]);
      if (kdramaSeries.value[i]) mix.push(kdramaSeries.value[i]);
      if (blSeries.value[i]) mix.push(blSeries.value[i]);
    }
    return mix.slice(0, 15);
  }

  const genreCounts = {};
  history.forEach(item => {
    if (item.genre) {
      item.genre.split(',').forEach(g => {
        const clean = g.trim();
        if (clean) genreCounts[clean] = (genreCounts[clean] || 0) + 1;
      });
    }
  });

  const preferredGenres = Object.keys(genreCounts).sort((a, b) => genreCounts[b] - genreCounts[a]);

  const recs = combinedCatalog.filter(item => {
    const isWatching = continueWatching.value.some(cw => cw.imdbId === item.imdbId || cw.id === item.id);
    if (isWatching) return false;
    return item.genre && item.genre.split(',').some(g => preferredGenres.includes(g.trim()));
  });

  // Se não encontrar correspondência direta por gênero, retorna o mix inicial intercalado
  if (recs.length === 0) {
    const mix = [];
    for (let i = 0; i < 5; i++) {
      if (popularMovies.value[i]) mix.push(popularMovies.value[i]);
      if (popularSeries.value[i]) mix.push(popularSeries.value[i]);
      if (animeSeries.value[i]) mix.push(animeSeries.value[i]);
      if (kdramaSeries.value[i]) mix.push(kdramaSeries.value[i]);
    }
    return mix.slice(0, 12);
  }

  return recs.slice(0, 15);
});

// ─── Data Loading ───
const refreshSettings = () => {
  installedAddons.value = getInstalledAddons();
  connectedServers.value = getJellyfinServers();
  if (typeof window !== 'undefined') {
    debridToken.value = localStorage.getItem('ktv_debrid_token') || '';
    debridProvider.value = localStorage.getItem('ktv_debrid_provider') || 'realdebrid';
    tmdbApiKey.value = localStorage.getItem('ktv_tmdb_api_key') || '';
    enableNsfw.value = localStorage.getItem('ktv_enable_nsfw') === 'true';
    seasonViewMode.value = localStorage.getItem('ktv_season_view_mode') || 'posters';
    const savedIptv = localStorage.getItem('ktv_iptv_playlists');
    if (savedIptv) iptvPlaylists.value = JSON.parse(savedIptv);
  }
};

const loadStremioCatalogs = async () => {
  loadingMovies.value = true;
  loadingSeries.value = true;
  try {
    const addons = getInstalledAddons();
    const cinemeta = addons.find(a => a.name.includes('Cinemeta')) || addons[0];
    if (cinemeta) {
      const movies = await getCatalogFromAddon(cinemeta.manifestUrl, 'movie', 'top');
      if (movies?.length > 0) {
        popularMovies.value = movies.slice(0, 20).map(m => ({
          id: m.imdb_id || m.id,
          title: m.name,
          imdbId: m.imdb_id || m.id,
          backdrop: m.background || m.poster,
          poster: m.poster,
          description: m.description || 'Nenhuma sinopse disponível.',
          year: m.releaseInfo || m.year,
          genre: m.genres ? m.genres.join(', ') : 'Geral',
          rating: m.imdbRating || '',
          ratings: [{ label: `IMDb ${m.imdbRating || '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
        }));
      }
      loadingMovies.value = false;

      const series = await getCatalogFromAddon(cinemeta.manifestUrl, 'series', 'top');
      if (series?.length > 0) {
        popularSeries.value = series.slice(0, 20).map(s => ({
          id: s.imdb_id || s.id,
          title: s.name,
          imdbId: s.imdb_id || s.id,
          backdrop: s.background || s.poster,
          poster: s.poster,
          description: s.description || 'Nenhuma sinopse disponível.',
          year: s.releaseInfo || s.year,
          genre: s.genres ? s.genres.join(', ') : 'Geral',
          rating: s.imdbRating || '',
          ratings: [{ label: `IMDb ${s.imdbRating || '7.8'}`, color: 'badge-imdb' }],
          episodes: null, // Populado dinamicamente via getMetaDetails
          type: 'series'
        }));
      }
      loadingSeries.value = false;

      // 1. Anime Movies
      const animeMoviesList = await getTMDBDiscover('with_genres=16&with_original_language=ja&with_watch_providers=283&watch_region=BR&sort_by=popularity.desc', 'movie');
      if (animeMoviesList?.length > 0) {
        animeMovies.value = animeMoviesList.slice(0, 20).map(m => ({
          id: m.id,
          title: m.title || m.original_title,
          imdbId: '',
          backdrop: m.backdrop_path ? `https://image.tmdb.org/t/p/w780${m.backdrop_path}` : '',
          poster: m.poster_path ? `https://image.tmdb.org/t/p/w342${m.poster_path}` : '',
          description: m.overview || 'Nenhuma sinopse disponível.',
          year: m.release_date ? m.release_date.split('-')[0] : '',
          genre: 'Filme de Anime',
          rating: m.vote_average ? m.vote_average.toFixed(1) : '8.0',
          ratings: [{ label: `TMDB ${m.vote_average ? m.vote_average.toFixed(1) : '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
          type: 'movie'
        }));
      }

      // 2. Anime Series
      const animeSeriesList = await getTMDBDiscover('with_genres=16&with_original_language=ja&with_watch_providers=283&watch_region=BR&sort_by=popularity.desc', 'tv');
      if (animeSeriesList?.length > 0) {
        animeSeries.value = animeSeriesList.slice(0, 20).map(m => ({
          id: m.id,
          title: m.name || m.original_name,
          imdbId: '',
          backdrop: m.backdrop_path ? `https://image.tmdb.org/t/p/w780${m.backdrop_path}` : '',
          poster: m.poster_path ? `https://image.tmdb.org/t/p/w342${m.poster_path}` : '',
          description: m.overview || 'Nenhuma sinopse disponível.',
          year: m.first_air_date ? m.first_air_date.split('-')[0] : '',
          genre: 'Série de Anime',
          rating: m.vote_average ? m.vote_average.toFixed(1) : '8.0',
          ratings: [{ label: `TMDB ${m.vote_average ? m.vote_average.toFixed(1) : '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
          type: 'series'
        }));
      }

      // 3. K-Drama Movies
      const kdramaMoviesList = await getTMDBDiscover('with_original_language=ko&with_watch_providers=8|337|119|318&watch_region=BR&sort_by=popularity.desc', 'movie');
      if (kdramaMoviesList?.length > 0) {
        kdramaMovies.value = kdramaMoviesList.slice(0, 20).map(m => ({
          id: m.id,
          title: m.title || m.original_title,
          imdbId: '',
          backdrop: m.backdrop_path ? `https://image.tmdb.org/t/p/w780${m.backdrop_path}` : '',
          poster: m.poster_path ? `https://image.tmdb.org/t/p/w342${m.poster_path}` : '',
          description: m.overview || 'Nenhuma sinopse disponível.',
          year: m.release_date ? m.release_date.split('-')[0] : '',
          genre: 'Filme K-Drama',
          rating: m.vote_average ? m.vote_average.toFixed(1) : '8.0',
          ratings: [{ label: `TMDB ${m.vote_average ? m.vote_average.toFixed(1) : '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
          type: 'movie'
        }));
      }

      // 4. K-Drama Series
      const kdramaSeriesList = await getTMDBDiscover('with_original_language=ko&with_watch_providers=8|337|119|318&watch_region=BR&sort_by=popularity.desc', 'tv');
      if (kdramaSeriesList?.length > 0) {
        kdramaSeries.value = kdramaSeriesList.slice(0, 20).map(m => ({
          id: m.id,
          title: m.name || m.original_name,
          imdbId: '',
          backdrop: m.backdrop_path ? `https://image.tmdb.org/t/p/w780${m.backdrop_path}` : '',
          poster: m.poster_path ? `https://image.tmdb.org/t/p/w342${m.poster_path}` : '',
          description: m.overview || 'Nenhuma sinopse disponível.',
          year: m.first_air_date ? m.first_air_date.split('-')[0] : '',
          genre: 'Série K-Drama',
          rating: m.vote_average ? m.vote_average.toFixed(1) : '8.0',
          ratings: [{ label: `TMDB ${m.vote_average ? m.vote_average.toFixed(1) : '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
          type: 'series'
        }));
      }

      // 5. BL Movies (Romance genre 10749 + Asian languages)
      const blMoviesList = await getTMDBDiscover('with_genres=10749&with_original_language=th|ko|ja|tw|zh|id|vi&with_watch_providers=8|337|119|318|283&watch_region=BR&sort_by=popularity.desc', 'movie');
      if (blMoviesList?.length > 0) {
        blMovies.value = blMoviesList.slice(0, 20).map(m => ({
          id: m.id,
          title: m.title || m.original_title,
          imdbId: '',
          backdrop: m.backdrop_path ? `https://image.tmdb.org/t/p/w780${m.backdrop_path}` : '',
          poster: m.poster_path ? `https://image.tmdb.org/t/p/w342${m.poster_path}` : '',
          description: m.overview || 'Nenhuma sinopse disponível.',
          year: m.release_date ? m.release_date.split('-')[0] : '',
          genre: 'Filme BL',
          rating: m.vote_average ? m.vote_average.toFixed(1) : '8.0',
          ratings: [{ label: `TMDB ${m.vote_average ? m.vote_average.toFixed(1) : '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
          type: 'movie'
        }));
      }

      // 6. BL Series (Romance genre 10749 + Asian languages)
      const blSeriesList = await getTMDBDiscover('with_genres=10749&with_original_language=th|ko|ja|tw|zh|id|vi&with_watch_providers=8|337|119|318|283&watch_region=BR&sort_by=popularity.desc', 'tv');
      if (blSeriesList?.length > 0) {
        blSeries.value = blSeriesList.slice(0, 20).map(m => ({
          id: m.id,
          title: m.name || m.original_name,
          imdbId: '',
          backdrop: m.backdrop_path ? `https://image.tmdb.org/t/p/w780${m.backdrop_path}` : '',
          poster: m.poster_path ? `https://image.tmdb.org/t/p/w342${m.poster_path}` : '',
          description: m.overview || 'Nenhuma sinopse disponível.',
          year: m.first_air_date ? m.first_air_date.split('-')[0] : '',
          genre: 'Série BL',
          rating: m.vote_average ? m.vote_average.toFixed(1) : '8.0',
          ratings: [{ label: `TMDB ${m.vote_average ? m.vote_average.toFixed(1) : '8.0'}`, color: 'badge-imdb' }],
          episodes: null,
          type: 'series'
        }));
      }
    } else {
      loadingMovies.value = false;
      loadingSeries.value = false;
    }
  } catch (err) {
    console.warn('Erro ao carregar catálogos do Stremio', err);
    loadingMovies.value = false;
    loadingSeries.value = false;
  }
};

// ─── Navigation ───
const handleNavigate = (tab) => {
  currentTab.value = tab;
  activeScreen.value = 'main';
  nextTick(() => setActiveElementId(`nav-${tab}`));
};

// ─── Profile & Progress Handlers ───
const loadProfileData = (profileName) => {
  const profile = profiles.value.find(p => p.name === profileName);
  if (!profile) return;
  continueWatching.value = profile.continueWatching || [];
  favorites.value = profile.favorites || [];
};

const handleSelectProfile = (profile) => {
  currentProfile.value = profile;
  if (typeof window !== 'undefined') {
    localStorage.setItem('ktv_current_profile', profile.name);
  }
  loadProfileData(profile.name);
  showToast(`Perfil ${profile.name} ativo!`);
};

const handleCreateProfile = async (profile) => {
  const newProfile = { name: profile.name, avatar: profile.avatar, favorites: [], continueWatching: [] };
  profiles.value.push(newProfile);
  await syncData({ profiles: profiles.value });
  handleSelectProfile(newProfile);
};

const handleEditProfile = async ({ oldName, name, avatar }) => {
  const idx = profiles.value.findIndex(p => p.name === oldName);
  if (idx !== -1) {
    profiles.value[idx].name = name;
    profiles.value[idx].avatar = avatar;
    
    // Migrar histórico/favoritos locais do perfil antigo para o novo nome na nuvem se necessário
    if (profiles.value[idx].continueWatching) {
      profiles.value[idx].continueWatching = profiles.value[idx].continueWatching;
    }
    
    await syncData({ profiles: profiles.value });
    
    if (currentProfile.value && currentProfile.value.name === oldName) {
      currentProfile.value = profiles.value[idx];
      localStorage.setItem('ktv_current_profile', name);
    }
    showToast('Perfil atualizado!');
  }
};

const handleDeleteProfile = async (name) => {
  if (profiles.value.length <= 1) {
    showToast('Você deve manter pelo menos um perfil!', 'error');
    return;
  }
  profiles.value = profiles.value.filter(p => p.name !== name);
  await syncData({ profiles: profiles.value });
  if (currentProfile.value && currentProfile.value.name === name) {
    currentProfile.value = null;
    localStorage.removeItem('ktv_current_profile');
  }
  showToast('Perfil excluído');
};

const handlePlayerProgress = async (data) => {
  const currentShow = selectedShow.value;
  const profile = currentProfile.value;
  if (!currentShow || !profile) return;

  const isSeries = currentShow.episodes && currentShow.episodes.length > 0;
  const mediaId = isSeries ? `${currentShow.imdbId}_ep_${currentShow.selectedEpisode || 1}` : currentShow.imdbId;

  const progressItem = {
    id: mediaId,
    imdbId: currentShow.imdbId,
    title: currentShow.title,
    backdrop: currentShow.backdrop,
    poster: currentShow.poster,
    year: currentShow.year,
    genre: currentShow.genre,
    progress: Math.round(data.progress),
    currentTime: data.currentTime,
    duration: data.duration,
    episodeLabel: isSeries ? `T1:E${(currentShow.selectedEpisode || 0) + 1}` : ''
  };

  if (data.progress > 95) {
    continueWatching.value = continueWatching.value.filter(item => item.id !== mediaId);
  } else {
    const filtered = continueWatching.value.filter(item => item.id !== mediaId);
    continueWatching.value = [progressItem, ...filtered];
  }

  const pIdx = profiles.value.findIndex(p => p.name === profile.name);
  if (pIdx !== -1) {
    profiles.value[pIdx].continueWatching = continueWatching.value;
    await syncData({ profiles: profiles.value });
  }
};

const toggleFavorite = async (show) => {
  const profile = currentProfile.value;
  if (!show || !profile) return;

  const isFav = favorites.value.some(item => item.imdbId === show.imdbId);
  if (isFav) {
    favorites.value = favorites.value.filter(item => item.imdbId !== show.imdbId);
    showToast('Removido dos Favoritos');
  } else {
    favorites.value.push({ ...show });
    showToast('Adicionado aos Favoritos');
  }

  const pIdx = profiles.value.findIndex(p => p.name === profile.name);
  if (pIdx !== -1) {
    profiles.value[pIdx].favorites = favorites.value;
    await syncData({ profiles: profiles.value });
  }
};

// ─── Detail + Player ───
// Traduz os episódios da temporada ativa usando dados em português do TMDB
const translateEpisodesForSeason = async (tmdbId, seasonNum) => {
  if (!tmdbId) return;
  try {
    const tmdbEpisodes = await getTMDBSeasonEpisodes(tmdbId, seasonNum);
    if (tmdbEpisodes.length > 0 && selectedShow.value && selectedShow.value.episodes) {
      selectedShow.value.episodes = selectedShow.value.episodes.map(ep => {
        if (ep.season === seasonNum) {
          const tmdbEp = tmdbEpisodes.find(te => te.episodeNumber === ep.episode);
          if (tmdbEp) {
            // Preservar distinções de episódios divididos (ex: Parte 1 / Parte 2 no original do Cinemeta)
            let newTitle = tmdbEp.name || ep.title;
            const originalTitle = ep.title || '';
            const suffixMatch = originalTitle.match(/(part[e]?\s*\d+|\bpt\b\.?\s*\d+|-\s*parte\s*\d+|-\s*part\s*[a-z0-9])/i);
            if (suffixMatch && !newTitle.toLowerCase().includes(suffixMatch[0].toLowerCase())) {
              const cleanSuffix = suffixMatch[0].replace(/^-\s*/, '').trim();
              newTitle = `${newTitle} (${cleanSuffix})`;
            }

            return {
              ...ep,
              title: newTitle,
              description: tmdbEp.overview || ep.description,
              thumbnail: tmdbEp.stillPath || ep.thumbnail,
              airDate: tmdbEp.airDate || '',
              released: !tmdbEp.airDate || new Date(tmdbEp.airDate) <= new Date()
            };
          }
        }
        return ep;
      });
    }
  } catch (e) {
    console.warn('Erro ao traduzir episódios da temporada:', e);
  }
};

// ─── Detail + Player ───
const openDetail = (show) => {
  if (!enableNsfw.value && isNsfwContent(show)) {
    triggerPinRequired(() => {
      proceedToOpenDetail(show);
    });
  } else {
    proceedToOpenDetail(show);
  }
};

const proceedToOpenDetail = async (show) => {
  const currentShow = show || featuredShow.value;
  selectedShow.value = { ...currentShow, selectedEpisode: 0, cast: [] };
  activeScreen.value = 'detail';
  activeStreams.value = []; // Inicialmente vazia (busca sob demanda!)

  const isSeries = (currentShow.episodes && currentShow.episodes.length > 0) || currentShow.type === 'series' || currentShow.type === 'anime';
  const mediaType = isSeries ? 'series' : 'movie';

  let imdbId = currentShow.imdbId;

  // Se não temos o IMDb ID mas temos o TMDB ID (que é o id nos catálogos do TMDB)
  if (!imdbId && currentShow.id && typeof currentShow.id === 'number') {
    selectedShow.value.tmdbId = currentShow.id;
    try {
      const resolvedImdbId = await getTMDBExternalIds(currentShow.id, mediaType);
      if (resolvedImdbId) {
        imdbId = resolvedImdbId;
        selectedShow.value.imdbId = resolvedImdbId;
      }
    } catch (e) {
      console.warn('Erro ao resolver IMDb ID pelo TMDB:', e);
    }
  }

  // 1. Carregar metadados completos do Cinemeta (para pegar os episódios e elenco reais!)
  try {
    const addons = getInstalledAddons();
    const cinemeta = addons.find(a => a.name.includes('Cinemeta')) || addons[0];
    if (cinemeta && imdbId) {
      getMetaDetails(cinemeta.manifestUrl, mediaType, imdbId).then(fullMeta => {
        if (fullMeta && selectedShow.value && selectedShow.value.imdbId === imdbId) {
          // Atualizar episódios e higienizar metadados para evitar thumbnails/descrições fantasmas
          const rawVideos = fullMeta.videos || fullMeta.episodes || [];
          if (rawVideos && rawVideos.length > 0) {
            // Ordenar por Temporada e depois por Episódio (com Temporada 0 no final!)
            const sortedVideos = [...rawVideos].sort((a, b) => {
              const sA = a.season || 1;
              const sB = b.season || 1;
              if (sA === 0 && sB !== 0) return 1;
              if (sB === 0 && sA !== 0) return -1;
              if (sA !== sB) return sA - sB;
              const eA = a.episode || a.number || 0;
              const eB = b.episode || b.number || 0;
              return eA - eB;
            });
            
            // Deduplicar episódios (mesma temporada e número)
            const seen = new Set();
            const uniqueVideos = [];
            for (const ep of sortedVideos) {
              const s = ep.season || 1;
              const e = ep.episode || ep.number || 0;
              const key = `${s}_${e}`;
              if (!seen.has(key)) {
                seen.add(key);
                uniqueVideos.push(ep);
              }
            }
            
            selectedShow.value.episodes = uniqueVideos.map((ep, idx) => ({
              id: ep.id || `${imdbId}_${ep.season || 1}_${ep.episode || ep.number || (idx + 1)}`,
              title: ep.name || ep.title || `Episódio ${ep.episode || ep.number || (idx + 1)}`,
              season: ep.season || 1,
              episode: ep.episode || ep.number || (idx + 1),
              description: ep.overview || ep.description || 'Nenhuma sinopse disponível para este episódio.',
              thumbnail: ep.thumbnail || ep.still_path || selectedShow.value.backdrop || selectedShow.value.poster || '',
              duration: ep.runtime ? `${ep.runtime}m` : '45m',
              released: !ep.released || new Date(ep.released) <= new Date(),
              airDate: ep.released ? (typeof ep.released === 'string' ? ep.released.split('T')[0] : '') : ''
            }));
          }
          // Mapear elenco do Cinemeta se disponível (caso o TMDB falhe ou não tenha)
          if (fullMeta.cast && (!selectedShow.value.cast || selectedShow.value.cast.length === 0)) {
            selectedShow.value.cast = fullMeta.cast.map(actorName => ({
              name: actorName,
              character: 'Ator',
              profilePath: ''
            }));
          }
          // Atualizar outros detalhes do Cinemeta
          if (fullMeta.description) selectedShow.value.description = fullMeta.description;
          if (fullMeta.releaseInfo || fullMeta.year) selectedShow.value.year = fullMeta.releaseInfo || fullMeta.year;
        }
      }).catch(err => console.warn('Falha ao obter metadados do Cinemeta:', err));
    }
  } catch (err) {
    console.warn('Erro ao disparar busca Cinemeta:', err);
  }

  // 2. Buscar metadados ricos do TMDB concorrentemente
  if (imdbId) {
    getTMDBDetails(imdbId, mediaType).then(async meta => {
      if (meta && selectedShow.value && selectedShow.value.imdbId === imdbId) {
        selectedShow.value.tmdbId = meta.tmdbId;
        selectedShow.value.title = meta.title || selectedShow.value.title;
        selectedShow.value.description = meta.overview || selectedShow.value.description;
        selectedShow.value.poster = meta.poster || selectedShow.value.poster;
        selectedShow.value.backdrop = meta.backdrop || selectedShow.value.backdrop;
        selectedShow.value.country = meta.country || '';
        selectedShow.value.languages = meta.languages || '';
        selectedShow.value.watchProviders = meta.watchProviders || [];
        selectedShow.value.tmdbSeasons = meta.seasons || [];
        // Priorizar elenco do TMDB (que tem fotos e personagens), se disponível
        if (meta.cast && meta.cast.length > 0) {
          selectedShow.value.cast = meta.cast;
        }

        // Buscar episódios traduzidos para a temporada inicial
        const initialSeason = selectedShow.value.episodes && selectedShow.value.episodes.length > 0
          ? Math.min(...selectedShow.value.episodes.map(e => e.season))
          : 1;
        await translateEpisodesForSeason(meta.tmdbId, initialSeason);
      }
    }).catch(err => console.warn('Falha ao obter metadados TMDB:', err));
  }

  // 3. Buscar legendas do Cinemeta/addons concorrentemente
  if (imdbId) {
    currentSubtitles.value = [];
    getSubtitlesFromAddons(mediaType, imdbId).then(subs => {
      if (subs && subs.length > 0 && selectedShow.value && selectedShow.value.imdbId === imdbId) {
        currentSubtitles.value = subs;
      }
    }).catch(err => console.warn('Falha ao obter legendas do Stremio:', err));
  }
};

// Busca e reprodução automática inteligente de fontes reais
const handlePlayDefault = async (episodeIndex = 0) => {
  const currentShow = selectedShow.value;
  if (!currentShow) return;

  const isSeries = currentShow.episodes && currentShow.episodes.length > 0;
  const streamType = isSeries ? 'series' : 'movie';
  let streamId = currentShow.imdbId;
  let season = 1;
  let episode = 1;

  if (isSeries) {
    let targetEp = null;
    if (typeof episodeIndex === 'object' && episodeIndex !== null) {
      season = episodeIndex.season;
      episode = episodeIndex.episode;
      targetEp = currentShow.episodes.find(e => e.season === season && e.episode === episode);
    } else {
      if (selectedShow.value) {
        selectedShow.value.selectedEpisode = episodeIndex;
      }
      targetEp = currentShow.episodes[episodeIndex];
      season = targetEp?.season || 1;
      episode = targetEp?.number || targetEp?.episode || (episodeIndex + 1);
    }

    if (targetEp && targetEp.released === false) {
      showToast(`Este episódio ainda não foi lançado! Estreia em ${targetEp.airDate || 'breve'}.`, 'error');
      return;
    }
    streamId = `${currentShow.imdbId}:${season}:${episode}`;
  }

  showToast('Buscando fontes de vídeo nos addons...', 'info');
  try {
    const addonStreams = await getStreamsFromAddons(streamType, streamId);
    let streamsList = [];

    if (addonStreams && addonStreams.length > 0) {
      streamsList = addonStreams.map(s => {
        const title = s.title || s.name || 'Fonte automática';
        const tu = title.toUpperCase();
        let quality = '1080P';
        if (tu.includes('4K') || tu.includes('2160P')) quality = '4K';
        else if (tu.includes('720P')) quality = '720P';
        return {
          name: s.name, title, quality,
          type: tu.includes('DV') || tu.includes('DOLBY') ? 'Dolby Vision' : tu.includes('HDR') ? 'HDR10' : 'SDR',
          size: title.match(/(\d+(\.\d+)?\s*(GB|MB))/i)?.[0] || '',
          url: s.url || s.externalUrl || '',
          isKinovio: (s.name || '').toLowerCase().includes('kinoviostream')
        };
      }).filter(stream => /^https?:\/\//i.test(stream.url));

      // Ordenar: KinovioStream primeiro no topo
      streamsList.sort((a, b) => {
        if (a.isKinovio && !b.isKinovio) return -1;
        if (!a.isKinovio && b.isKinovio) return 1;
        return 0;
      });
    }

    // Atualiza a lista lateral reativa na tela de detalhes
    activeStreams.value = streamsList;

    if (streamsList.length > 0) {
      const kinovioSources = streamsList.filter(s => s.isKinovio);
      const bestStream = kinovioSources.find(s => s.quality === '4K') || kinovioSources.find(s => s.quality === '1080P') || kinovioSources[0] || streamsList[0];
      
      showToast(`Iniciando reprodução...`);
      playMedia(bestStream);
    } else {
      showToast('Nenhuma fonte encontrada nos addons instalados.', 'error');
    }
  } catch (err) {
    showToast('Erro ao buscar streams.', 'error');
    console.error(err);
  }
};

const playMedia = (stream) => {
  currentPlayingStream.value = stream;
  activeScreen.value = 'player';
};

const exitPlayer = () => {
  activeScreen.value = selectedShow.value ? 'detail' : 'main';
};

// ─── Search ───
const handleSearch = async (query) => {
  if (!query.trim()) return;
  searchedOnce.value = true;
  loadingMovies.value = true;
  try {
    const addons = getInstalledAddons();
    const cinemeta = addons.find(a => a.name.includes('Cinemeta')) || addons[0];
    if (cinemeta) {
      const [movieResults, seriesResults] = await Promise.all([
        searchCatalogFromAddon(cinemeta.manifestUrl, 'movie', 'top', query).catch(() => []),
        searchCatalogFromAddon(cinemeta.manifestUrl, 'series', 'top', query).catch(() => [])
      ]);

      const mappedMovies = (movieResults || []).map(m => ({
        id: m.imdb_id || m.id, title: m.name, imdbId: m.imdb_id || m.id,
        backdrop: m.background || m.poster, poster: m.poster,
        description: m.description || '', year: m.releaseInfo || m.year,
        genre: m.genres ? m.genres.join(', ') : 'Geral',
        rating: m.imdbRating || '',
        ratings: [{ label: `IMDb ${m.imdbRating || '7.5'}`, color: 'badge-imdb' }],
        episodes: null,
        type: 'movie'
      }));

      const mappedSeries = (seriesResults || []).map(s => ({
        id: s.imdb_id || s.id, title: s.name, imdbId: s.imdb_id || s.id,
        backdrop: s.background || s.poster, poster: s.poster,
        description: s.description || '', year: s.releaseInfo || s.year,
        genre: s.genres ? s.genres.join(', ') : 'Geral',
        rating: s.imdbRating || '',
        ratings: [{ label: `IMDb ${s.imdbRating || '7.5'}`, color: 'badge-imdb' }],
        episodes: null,
        type: 'series'
      }));

      searchResults.value = [...mappedMovies, ...mappedSeries];
      if (searchResults.value.length === 0) showToast('Nenhum resultado encontrado', 'info');
    }
  } catch (err) {
    console.error(err);
  } finally {
    loadingMovies.value = false;
  }
};

const handleFilterCategory = (categoryName) => {
  currentTab.value = 'search';
  searchResults.value = [];
  searchedOnce.value = false;
  handleSearch(categoryName);
};

const handleConnectTrakt = async () => {
  showTraktModal.value = true;
  traktUserCode.value = '';
  try {
    const res = await getDeviceCode();
    traktUserCode.value = res.user_code;
    traktVerificationUrl.value = res.verification_url;
    
    // Poll access token every 5 seconds
    traktPollInterval = setInterval(async () => {
      const token = await pollDeviceToken(res.device_code);
      if (token) {
        traktConnected.value = true;
        showToast('Conta do Trakt conectada com sucesso!', 'success');
        clearInterval(traktPollInterval);
        showTraktModal.value = false;
      }
    }, 5000);
  } catch (err) {
    showToast('Erro ao obter pareamento do Trakt.', 'error');
    showTraktModal.value = false;
  }
};

const cancelTraktPairing = () => {
  if (traktPollInterval) {
    clearInterval(traktPollInterval);
  }
  showTraktModal.value = false;
};

const handleDisconnectTrakt = () => {
  disconnectTrakt();
  traktConnected.value = false;
  showToast('Conta do Trakt desconectada.');
};

// ─── Settings Actions ───
const handleInstallAddon = async (url) => {
  if (!url.trim()) return;
  try {
    const addon = await installAddon(url.trim());
    showToast(`Addon ${addon.name} instalado!`);
    refreshSettings();
    const addons = getInstalledAddons();
    await syncData({ settings: { stremioAddons: addons } });
    loadStremioCatalogs();
  } catch (err) {
    showToast(err.message, 'error');
  }
};

const handleUninstallAddon = async (url) => {
  uninstallAddon(url);
  showToast('Addon desinstalado');
  refreshSettings();
  const addons = getInstalledAddons();
  await syncData({ settings: { stremioAddons: addons } });
};

const handleConnectServer = async ({ url, apiKey }) => {
  if (!url.trim() || !apiKey.trim()) return;
  try {
    const server = await connectJellyfinServer(url.trim(), apiKey.trim());
    showToast(`Jellyfin ${server.name} conectado!`);
    refreshSettings();
    const servers = getJellyfinServers();
    await syncData({ settings: { jellyfinServers: servers } });
  } catch (err) {
    showToast(err.message, 'error');
  }
};

const handleDisconnectServer = async (url) => {
  disconnectJellyfinServer(url);
  showToast('Servidor desconectado');
  refreshSettings();
  const servers = getJellyfinServers();
  await syncData({ settings: { jellyfinServers: servers } });
};

const handleAddIptv = async ({ name, url }) => {
  if (!name.trim() || !url.trim()) return;
  iptvPlaylists.value.push({ name, url });
  localStorage.setItem('ktv_iptv_playlists', JSON.stringify(iptvPlaylists.value));
  showToast('Playlist IPTV adicionada!');
  await syncData({ settings: { iptvPlaylists: iptvPlaylists.value } });
};

const handleRemoveIptv = async (index) => {
  iptvPlaylists.value.splice(index, 1);
  localStorage.setItem('ktv_iptv_playlists', JSON.stringify(iptvPlaylists.value));
  showToast('Playlist removida');
  await syncData({ settings: { iptvPlaylists: iptvPlaylists.value } });
};

const handleSaveSettings = async () => {
  if (typeof window === 'undefined') return;
  localStorage.setItem('ktv_debrid_token', debridToken.value);
  localStorage.setItem('ktv_debrid_provider', debridProvider.value);
  localStorage.setItem('ktv_tmdb_api_key', tmdbApiKey.value);
  showToast('Configurações salvas!');
  await syncData({
    settings: {
      debridToken: debridToken.value,
      debridProvider: debridProvider.value,
      tmdbApiKey: tmdbApiKey.value
    }
  });
};

const handleResetTmdbKey = async () => {
  if (typeof window === 'undefined') return;
  localStorage.removeItem('ktv_tmdb_api_key');
  tmdbApiKey.value = '';
  showToast('TMDB redefinido para a chave padrão!');
  await syncData({ settings: { tmdbApiKey: '' } });
};

// ─── Authentication Handlers ───
const handleAuthSuccess = (userData) => {
  user.value = userData;
  profiles.value = userData.profiles || [];
  
  if (userData.settings) {
    const s = userData.settings;
    if (s.debridToken !== undefined) debridToken.value = s.debridToken;
    if (s.debridProvider !== undefined) debridProvider.value = s.debridProvider;
    if (s.tmdbApiKey !== undefined) tmdbApiKey.value = s.tmdbApiKey;
    if (s.stremioAddons) {
      localStorage.setItem('ktv_stremio_addons', JSON.stringify(s.stremioAddons));
    }
    if (s.jellyfinServers) {
      localStorage.setItem('ktv_jellyfin_servers', JSON.stringify(s.jellyfinServers));
    }
    if (s.iptvPlaylists) {
      localStorage.setItem('ktv_iptv_playlists', JSON.stringify(s.iptvPlaylists));
      iptvPlaylists.value = s.iptvPlaylists;
    }
  }
  
  refreshSettings();
  loadStremioCatalogs();
  showToast('Conectado com sucesso!');
};

const handleLogout = async () => {
  try {
    await $fetch('/api/auth/logout', { method: 'POST' });
    user.value = null;
    currentProfile.value = null;
    profiles.value = [];
    localStorage.removeItem('ktv_current_profile');
    showToast('Você saiu da sua conta.');
  } catch (err) {
    showToast('Erro ao sair da conta.', 'error');
  }
};

// ─── Init ───
onMounted(async () => {
  initSpatialNavigation();
  await checkSession();
  refreshSettings();
  loadStremioCatalogs();
  traktConnected.value = isTraktConnected();
});
</script>

<template>
  <div class="min-h-screen bg-cinema-deep text-text-primary relative overflow-hidden font-sans">
    
    <!-- Loading spinner on start -->
    <div v-if="checkSessionLoading" class="fixed inset-0 z-[300] bg-cinema-deep flex flex-col items-center justify-center gap-4">
      <div class="w-10 h-10 border-4 border-accent border-t-transparent rounded-full animate-spin"></div>
      <span class="text-xs font-semibold text-text-secondary">Carregando KinovioTV...</span>
    </div>

    <!-- Auth Landing Page if not authenticated -->
    <LandingPage 
      v-else-if="!user" 
      @auth-success="handleAuthSuccess"
    />

    <!-- Main Application Flow -->
    <template v-else>
      <!-- Ambient iOS background colors -->
      <div class="bg-ambient-blob w-[300px] h-[300px] bg-accent left-[-5%] top-[-5%]"></div>
      <div class="bg-ambient-blob w-[400px] h-[400px] bg-brand right-[10%] top-[20%]"></div>
      
      <!-- Toast Notifications -->
      <div class="fixed top-4 right-4 z-[200] flex flex-col gap-2">
        <div 
          v-for="toast in toasts" 
          :key="toast.id" 
          class="flex items-center gap-2.5 px-4 py-2.5 rounded-xl text-xs font-semibold glass-heavy animate-slide-down"
          :class="toast.type === 'success' ? 'text-emerald-400 border-emerald-500/20' : toast.type === 'error' ? 'text-red-400 border-red-500/20' : 'text-brand border-brand/20'"
        >
          <svg v-if="toast.type === 'success'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          <svg v-else-if="toast.type === 'error'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9.75 9.75l4.5 4.5m0-4.5l-4.5 4.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          <svg v-else class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z"/></svg>
          <span>{{ toast.message }}</span>
        </div>
      </div>

      <!-- Profile Select Screen (Displays if no active profile selected) -->
      <ProfileSelectScreen 
        v-if="!currentProfile" 
        :profiles="profiles" 
        @select="handleSelectProfile"
        @create="handleCreateProfile"
        @edit="handleEditProfile"
        @delete="handleDeleteProfile"
      />

      <!-- Navigation (hidden during player and profile selection) -->
      <AppNavbar 
        v-if="activeScreen !== 'player' && currentProfile"
        :current-tab="currentTab" 
        :active-profile="currentProfile"
        @navigate="handleNavigate" 
        @change-profile="currentProfile = null"
      />

      <!-- Header Mobile (Apenas em mobile, escondido durante o player e detalhes) -->
      <header 
        v-if="activeScreen === 'main' && currentProfile"
        class="md:hidden fixed top-0 left-0 right-0 z-40 h-14 glass-heavy border-b border-glass-border px-4 flex items-center justify-between"
      >
        <div class="flex items-center gap-2" @click="handleNavigate('home')">
          <img src="~/assets/img/logo.png" alt="K" class="w-6 h-6 object-contain" />
          <span class="text-xs font-black tracking-wider text-white uppercase">KinovioTV</span>
        </div>
        
        <button 
          id="m-nav-settings"
          class="focusable w-9 h-9 rounded-lg flex items-center justify-center bg-glass border border-glass-border text-text-secondary hover:text-white"
          :class="currentTab === 'settings' ? 'text-accent border-accent/20 bg-accent/5' : ''"
          @click="handleNavigate('settings')"
          title="Configurações"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
        </button>
      </header>

      <!-- Main Content Area -->
      <main 
        v-if="currentProfile"
        class="flex-1 min-h-screen overflow-y-auto no-scrollbar"
        :class="activeScreen !== 'player' ? [
          'md:pl-[var(--nav-width-desktop)] pb-[var(--nav-height-mobile)] md:pb-0',
          activeScreen === 'main' ? 'pt-14 md:pt-0' : 'pt-0'
        ] : ''"
      >
        
        <!-- ═══ PLAYER SCREEN ═══ -->
        <PlayerScreen 
          v-if="activeScreen === 'player'"
          :stream="currentPlayingStream"
          :title="selectedShow?.title || ''"
          :streams="activeStreams"
          :subtitles="currentSubtitles"
          @exit="exitPlayer"
          @progress="handlePlayerProgress"
          @change-stream="currentPlayingStream = $event"
        />

        <!-- ═══ DETAIL SCREEN ═══ -->
        <DetailScreen 
          v-else-if="activeScreen === 'detail' && selectedShow"
          :show="selectedShow"
          :streams="activeStreams"
          :is-favorite="favorites.some(item => item.imdbId === selectedShow.imdbId)"
          :season-view-mode="seasonViewMode"
          @back="activeScreen = 'main'"
          @play="playMedia"
          @play-default="handlePlayDefault"
          @favorite="toggleFavorite"
          @change-season="translateEpisodesForSeason(selectedShow.tmdbId, $event)"
          @toast="showToast($event.message, $event.type)"
        />

        <!-- ═══ MAIN SCREENS ═══ -->
        <template v-else>

          <!-- HOME -->
          <HomeScreen 
            v-if="currentTab === 'home'"
            :movies="popularMovies"
            :series="popularSeries"
            :anime-movies="animeMovies"
            :anime-series="animeSeries"
            :kdrama-movies="kdramaMovies"
            :kdrama-series="kdramaSeries"
            :bl-movies="blMovies"
            :bl-series="blSeries"
            :recommended="recommendedMedia"
            :continue-watching="continueWatching"
            :loading-movies="loadingMovies"
            :loading-series="loadingSeries"
            @open-detail="openDetail"
            @filter-category="handleFilterCategory"
          />

          <!-- SEARCH -->
          <SearchScreen 
            v-else-if="currentTab === 'search'"
            :results="searchResults"
            :loading="loadingMovies"
            :searched="searchedOnce"
            @search="handleSearch"
            @select="openDetail"
          />

          <!-- CALENDAR -->
          <CalendarScreen
            v-else-if="currentTab === 'calendar'"
            :items="[...animeSeries, ...popularSeries, ...kdramaSeries, ...blSeries]"
            @select="openDetail"
            @explore="handleNavigate('search')"
          />

          <!-- FAVORITES (Real user favorites list) -->
          <div v-else-if="currentTab === 'favorites'" class="p-5 sm:p-8 lg:p-14 animate-fade-in">
            <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white mb-2">Minha Lista</h2>
            <p class="text-xs text-text-muted mb-8 font-medium">Seus filmes e séries salvos neste perfil</p>
            <div v-if="favorites.length > 0" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
              <MediaCard 
                v-for="item in favorites" 
                :key="item.imdbId || item.id" 
                :item="item" 
                layout="poster"
                @click="openDetail(item)"
              />
            </div>
            <div v-else class="flex flex-col items-center justify-center py-20 gap-3">
              <svg class="w-16 h-16 text-text-muted/40" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1"><path stroke-linecap="round" stroke-linejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z"/></svg>
              <p class="text-sm text-text-muted">Sua lista está vazia</p>
              <p class="text-[10px] text-text-muted">Clique no coração ao lado de "Assistir" em qualquer conteúdo para salvá-lo aqui.</p>
            </div>
          </div>

          <!-- LIVE TV -->
          <LiveTVScreen 
            v-else-if="currentTab === 'livetv'"
            :channels="iptvChannels"
            @fullscreen="playMedia({ name: $event.name, url: $event.url })"
          />

          <!-- PROFILE -->
          <ProfileScreen
            v-else-if="currentTab === 'profile'"
            :profile="currentProfile"
            :favorites-count="favorites.length"
            :watching-count="continueWatching.length"
            @settings="handleNavigate('settings')"
            @list="handleNavigate('favorites')"
            @change-profile="currentProfile = null"
          />

          <!-- SETTINGS -->
          <SettingsScreen 
            v-else-if="currentTab === 'settings'"
            :user-email="user?.email"
            :addons="installedAddons"
            :servers="connectedServers"
            :iptv-playlists="iptvPlaylists"
            :debrid-provider="debridProvider"
            :debrid-token="debridToken"
            :tmdb-api-key="tmdbApiKey"
            :trakt-connected="traktConnected"
            :enable-nsfw="enableNsfw"
            :season-view-mode="seasonViewMode"
            @toggle-nsfw="handleToggleNsfw"
            @update:season-view-mode="handleUpdateSeasonViewMode"
            @logout="handleLogout"
            @install-addon="handleInstallAddon"
            @uninstall-addon="handleUninstallAddon"
            @connect-server="handleConnectServer"
            @disconnect-server="handleDisconnectServer"
            @set-debrid-provider="debridProvider = $event"
            @update:debrid-token="debridToken = $event"
            @update:tmdb-api-key="tmdbApiKey = $event"
            @save-settings="handleSaveSettings"
            @add-iptv="handleAddIptv"
            @remove-iptv="handleRemoveIptv"
            @reset-tmdb-key="handleResetTmdbKey"
            @connect-trakt="handleConnectTrakt"
            @disconnect-trakt="handleDisconnectTrakt"
          />
        </template>
      </main>
    </template>

    <!-- Trakt Pairing Modal -->
    <div 
      v-if="showTraktModal" 
      class="fixed inset-0 z-[250] bg-black/80 backdrop-blur-md flex items-center justify-center p-4 animate-ios-fade-in"
      @click.self="cancelTraktPairing"
    >
      <div class="glass-heavy border border-glass-border p-8 rounded-3xl max-w-sm w-full flex flex-col items-center text-center gap-6 animate-ios-scale-up relative">
        <div class="absolute -top-10 -left-10 w-36 h-36 bg-rose-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="w-12 h-12 rounded-2xl bg-rose-500/15 flex items-center justify-center text-rose-500 text-2xl shadow-md">
          ❤️
        </div>
        <div>
          <h3 class="text-base font-black text-white">Conectar ao Trakt.tv</h3>
          <p class="text-xs text-text-secondary mt-1 max-w-[280px]">
            Acesse o link abaixo ou escaneie o código com a câmera do celular:
          </p>
        </div>
        <div class="relative w-44 h-44 flex items-center justify-center bg-white rounded-2xl p-2 border border-glass-border shadow-lg">
          <img 
            v-if="traktUserCode"
            :src="`https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent('https://trakt.tv/activate?user_code=' + traktUserCode)}`" 
            alt="Código QR" 
            class="w-full h-full object-contain"
          />
          <div v-else class="w-6 h-6 border-2 border-rose-500 border-t-transparent rounded-full animate-spin"></div>
        </div>
        <div v-if="traktUserCode">
          <span class="text-[10px] font-bold text-text-muted uppercase tracking-wider">Código de Ativação</span>
          <div class="text-2xl font-black tracking-widest text-white mt-1 uppercase select-none bg-glass/40 border border-glass-border px-4 py-2 rounded-xl">
            {{ traktUserCode }}
          </div>
        </div>
        <div class="flex flex-col items-center gap-2">
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-rose-500 animate-ping"></span>
            <span class="text-[10px] text-rose-400 font-semibold uppercase tracking-wider">Aguardando autorização...</span>
          </div>
          <p class="text-[9px] text-text-muted max-w-[240px]">
            O código irá expirar em alguns minutos. Após autorizar no Trakt, esta tela fechará automaticamente.
          </p>
        </div>
        <button 
          id="btn-trakt-cancel"
          class="focusable w-full py-2.5 bg-glass border border-glass-border/60 hover:bg-glass-hover text-text-secondary hover:text-white text-xs font-bold rounded-xl transition-smooth"
          @click="cancelTraktPairing"
        >
          Cancelar
        </button>
      </div>
    </div>

    <!-- Parental PIN Modal -->
    <div 
      v-if="showPinModal" 
      class="fixed inset-0 z-[300] bg-black/90 backdrop-blur-lg flex items-center justify-center p-4 animate-ios-fade-in"
      @click.self="showPinModal = false"
    >
      <div class="glass-heavy border border-glass-border p-8 rounded-3xl max-w-sm w-full flex flex-col items-center text-center gap-6 animate-ios-scale-up relative">
        <button 
          @click="showPinModal = false"
          class="absolute top-4 right-4 text-text-muted hover:text-white transition-smooth"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
        </button>

        <div class="w-12 h-12 rounded-2xl bg-red-500/15 flex items-center justify-center text-red-500 text-2xl shadow-md">
          🔞
        </div>
        
        <div>
          <h3 class="text-base font-black text-white">Controle Parental</h3>
          <p class="text-xs text-text-secondary mt-1 max-w-[280px]">
            Digite o PIN de 4 dígitos para autorizar o acesso (PIN Padrão: <strong class="text-accent">1234</strong>).
          </p>
        </div>

        <!-- Dots representation -->
        <div class="flex gap-4 justify-center my-2">
          <div 
            v-for="i in 4" 
            :key="i"
            class="w-3.5 h-3.5 rounded-full border border-glass-border transition-smooth"
            :class="pinInput.length >= i ? 'bg-accent border-accent scale-110 shadow-glow' : 'bg-glass/10'"
          ></div>
        </div>

        <!-- Virtual Keyboard -->
        <div class="grid grid-cols-3 gap-2 w-full max-w-[220px]">
          <button 
            v-for="num in ['1','2','3','4','5','6','7','8','9']" 
            :key="num"
            @click="handlePinKey(num)"
            class="focusable w-14 h-14 rounded-full bg-glass hover:bg-glass-hover text-white text-lg font-black flex items-center justify-center transition-smooth"
          >
            {{ num }}
          </button>
          <button 
            @click="handlePinBackspace"
            class="focusable w-14 h-14 rounded-full bg-red-950/20 text-red-400 hover:bg-red-900/30 flex items-center justify-center transition-smooth text-xs font-bold"
          >
            Apagar
          </button>
          <button 
            @click="handlePinKey('0')"
            class="focusable w-14 h-14 rounded-full bg-glass hover:bg-glass-hover text-white text-lg font-black flex items-center justify-center transition-smooth"
          >
            0
          </button>
          <button 
            @click="verifyPin"
            class="focusable w-14 h-14 rounded-full bg-emerald-950/30 text-emerald-400 hover:bg-emerald-800/40 flex items-center justify-center transition-smooth text-xs font-black border border-emerald-500/20"
          >
            OK
          </button>
        </div>

        <p v-if="pinError" class="text-[10px] font-bold text-red-500 uppercase tracking-wider animate-shake">
          PIN Incorreto! Tente novamente.
        </p>
      </div>
    </div>
  </div>
</template>
