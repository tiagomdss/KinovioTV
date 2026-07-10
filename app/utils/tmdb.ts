// Cliente de API do TMDB (The Movie Database) para metadados ricos do KinovioTV

const DEFAULT_TMDB_API_KEY = '291e5a73a2dac4670e1aec07ffdefccc';

export interface WatchProvider {
  name: string;
  logo: string;
  id: number;
}

export interface CastMember {
  name: string;
  character: string;
  profilePath: string;
}

export interface TMDBMetadata {
  title?: string;
  overview?: string;
  poster?: string;
  backdrop?: string;
  country?: string;
  languages?: string;
  tmdbId?: number;
  watchProviders?: WatchProvider[];
  cast?: CastMember[];
  seasons?: any[];
}

export interface TMDBEpisode {
  id: number;
  name: string;
  overview: string;
  seasonNumber: number;
  episodeNumber: number;
  stillPath: string;
  runtime: number;
}

function getApiKey(): string {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('ktv_tmdb_api_key') || DEFAULT_TMDB_API_KEY;
  }
  return DEFAULT_TMDB_API_KEY;
}

function getSystemLanguage(): string {
  if (typeof window !== 'undefined') {
    const savedLang = localStorage.getItem('ktv_subtitle_language');
    if (savedLang && savedLang !== 'off') return savedLang;
    
    const sysLang = navigator.language || 'pt-BR';
    return sysLang.toLowerCase().startsWith('pt') ? 'pt-BR' : 'en-US';
  }
  return 'pt-BR';
}

// Busca metadados e provedores de streaming pelo ID IMDb do Stremio
export async function getTMDBDetails(imdbId: string, type: 'movie' | 'series'): Promise<TMDBMetadata | null> {
  if (!imdbId) return null;
  const apiKey = getApiKey();
  const tmdbType = type === 'series' ? 'tv' : 'movie';
  const lang = getSystemLanguage();
  
  try {
    const findUrl = `https://api.themoviedb.org/3/find/${imdbId}?api_key=${apiKey}&external_source=imdb_id&language=${lang}`;
    const findRes = await fetch(findUrl);
    if (!findRes.ok) return null;
    
    const findData = await findRes.json();
    const results = type === 'series' ? findData.tv_results : findData.movie_results;
    if (!results || results.length === 0) return null;
    
    const tmdbItem = results[0];
    const tmdbId = tmdbItem.id;

    const detailsUrl = `https://api.themoviedb.org/3/${tmdbType}/${tmdbId}?api_key=${apiKey}&language=${lang}`;
    const providersUrl = `https://api.themoviedb.org/3/${tmdbType}/${tmdbId}/watch/providers?api_key=${apiKey}`;
    const creditsUrl = `https://api.themoviedb.org/3/${tmdbType}/${tmdbId}/credits?api_key=${apiKey}&language=${lang}`;
 
    const [detailsRes, providersRes, creditsRes] = await Promise.all([
      fetch(detailsUrl).catch(() => null),
      fetch(providersUrl).catch(() => null),
      fetch(creditsUrl).catch(() => null)
    ]);

    let title = type === 'series' ? tmdbItem.name : tmdbItem.title;
    let overview = tmdbItem.overview;
    let poster = tmdbItem.poster_path ? `https://image.tmdb.org/t/p/w500${tmdbItem.poster_path}` : '';
    let backdrop = tmdbItem.backdrop_path ? `https://image.tmdb.org/t/p/w1280${tmdbItem.backdrop_path}` : '';
    let country = '';
    let languages = '';
    let watchProviders: WatchProvider[] = [];
    let cast: CastMember[] = [];
    let tmdbSeasons: any[] = [];

    if (detailsRes && detailsRes.ok) {
      const details = await detailsRes.json();
      title = (type === 'series' ? details.name : details.title) || title;
      overview = details.overview || overview;
      poster = details.poster_path ? `https://image.tmdb.org/t/p/w500${details.poster_path}` : poster;
      backdrop = details.backdrop_path ? `https://image.tmdb.org/t/p/w1280${details.backdrop_path}` : backdrop;
      
      const countries = details.production_countries || [];
      if (countries.length > 0) {
        country = countries.map((c: any) => c.name).join(', ');
      }
      
      const langs = details.spoken_languages || [];
      if (langs.length > 0) {
        languages = langs.map((l: any) => l.name).join(', ');
      }

      if (type === 'series') {
        tmdbSeasons = (details.seasons || []).map((s: any) => ({
          seasonNumber: s.season_number,
          name: s.name || `Temporada ${s.season_number}`,
          poster: s.poster_path ? `https://image.tmdb.org/t/p/w185${s.poster_path}` : '',
          episodeCount: s.episode_count
        }));
      }
    }

    if (providersRes && providersRes.ok) {
      const providersData = await providersRes.json();
      const countryCode = lang.split('-')[1]?.toUpperCase() || 'BR';
      const brProviders = providersData.results?.[countryCode] || providersData.results?.BR || {};
      const flatrate = brProviders.flatrate || [];
      const rent = brProviders.rent || [];
      const buy = brProviders.buy || [];

      const allProviders = [...flatrate, ...rent, ...buy];
      const seen = new Set();
      
      for (const p of allProviders) {
        if (!seen.has(p.provider_id)) {
          seen.add(p.provider_id);
          watchProviders.push({
            name: p.provider_name,
            logo: p.logo_path ? `https://image.tmdb.org/t/p/w92${p.logo_path}` : '',
            id: p.provider_id
          });
        }
      }
    }

    if (creditsRes && creditsRes.ok) {
      try {
        const creditsData = await creditsRes.json();
        cast = (creditsData.cast || []).slice(0, 10).map((c: any) => ({
          name: c.name,
          character: c.character,
          profilePath: c.profile_path ? `https://image.tmdb.org/t/p/w185${c.profile_path}` : ''
        }));
      } catch (err) {
        console.warn('Erro ao processar elenco:', err);
      }
    }

    return {
      title,
      overview,
      poster,
      backdrop,
      country,
      languages,
      tmdbId,
      watchProviders,
      cast,
      seasons: tmdbSeasons
    };
  } catch (error) {
    console.error(`Erro ao buscar dados do TMDB para ${imdbId}:`, error);
    return null;
  }
}

// Busca episódios de uma temporada específica em Português
export async function getTMDBSeasonEpisodes(tmdbId: number, seasonNumber: number): Promise<TMDBEpisode[]> {
  const apiKey = getApiKey();
  const lang = getSystemLanguage();
  const url = `https://api.themoviedb.org/3/tv/${tmdbId}/season/${seasonNumber}?api_key=${apiKey}&language=${lang}`;

  try {
    const res = await fetch(url);
    if (!res.ok) return [];
    const data = await res.json();
    return (data.episodes || []).map((ep: any) => ({
      id: ep.id,
      name: ep.name,
      overview: ep.overview,
      seasonNumber: ep.season_number,
      episodeNumber: ep.episode_number,
      stillPath: ep.still_path ? `https://image.tmdb.org/t/p/w300${ep.still_path}` : '',
      runtime: ep.runtime || 45,
      airDate: ep.air_date || ''
    }));
  } catch (error) {
    console.error(`Erro ao buscar episódios da temp ${seasonNumber} no TMDB:`, error);
    return [];
  }
}

// Método genérico de descoberta do TMDB (K-Dramas, BL, etc.)
export async function getTMDBDiscover(params: string, type: 'movie' | 'tv' = 'tv'): Promise<any[]> {
  const apiKey = getApiKey();
  const lang = getSystemLanguage();
  const url = `https://api.themoviedb.org/3/discover/${type}?api_key=${apiKey}&language=${lang}&${params}`;
  try {
    const res = await fetch(url);
    if (!res.ok) return [];
    const data = await res.json();
    return data.results || [];
  } catch (e) {
    console.error('Erro no TMDB Discover:', e);
    return [];
  }
}

// Recupera IDs externos (IMDb ID) pelo ID do TMDB
export async function getTMDBExternalIds(tmdbId: number, type: 'movie' | 'series'): Promise<string | null> {
  const apiKey = getApiKey();
  const tmdbType = type === 'series' ? 'tv' : 'movie';
  const url = `https://api.themoviedb.org/3/${tmdbType}/${tmdbId}/external_ids?api_key=${apiKey}`;
  try {
    const res = await fetch(url);
    if (!res.ok) return null;
    const data = await res.json();
    return data.imdb_id || null;
  } catch (e) {
    console.error(`Erro ao buscar External IDs para ${tmdbId}:`, e);
    return null;
  }
}
