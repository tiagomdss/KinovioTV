// Cliente de Integração com o Trakt.tv para scrobble e sincronização de histórico

const TRAKT_CLIENT_ID = '35f29910e5bd7fa6c888d30e54d3b66d6287cd4f02fa627732d84714f3583c27'; // Chave pública de testes KTV

export interface TraktSession {
  accessToken: string;
  refreshToken: string;
  username: string;
  createdAt: number;
}

export interface TraktDeviceCodeResponse {
  device_code: string;
  user_code: string;
  verification_url: string;
  expires_in: number;
  interval: number;
}

// Verifica se há conta conectada
export function isTraktConnected(): boolean {
  if (typeof window === 'undefined') return false;
  return !!localStorage.getItem('ktv_trakt_token');
}

// Gera URL para o usuário se autenticar no site do Trakt
export function getTraktAuthUrl(): string {
  const redirectUri = typeof window !== 'undefined' ? `${window.location.origin}/trakt-callback` : 'urn:ietf:wg:oauth:2.0:oob';
  return `https://trakt.tv/oauth/authorize?response_type=code&client_id=${TRAKT_CLIENT_ID}&redirect_uri=${encodeURIComponent(redirectUri)}`;
}

// Troca o código temporário por um Token de Acesso
export async function authenticateWithCode(code: string): Promise<string> {
  const redirectUri = typeof window !== 'undefined' ? `${window.location.origin}/trakt-callback` : 'urn:ietf:wg:oauth:2.0:oob';
  
  try {
    const response = await fetch('https://api.trakt.tv/oauth/token', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        code,
        client_id: TRAKT_CLIENT_ID,
        client_secret: '', // Apps client-side não utilizam secret
        redirect_uri: redirectUri,
        grant_type: 'authorization_code'
      })
    });

    if (!response.ok) throw new Error('Falha na autenticação do Trakt');
    const data = await response.json();
    
    if (data.access_token) {
      localStorage.setItem('ktv_trakt_token', data.access_token);
      localStorage.setItem('ktv_trakt_refresh_token', data.refresh_token);
      return data.access_token;
    }
    throw new Error('Token de acesso ausente');
  } catch (e: any) {
    console.error('Erro de autenticação no Trakt:', e);
    throw new Error(e.message);
  }
}

// Iniciar o fluxo de Device Code (QR Code / TV)
export async function getDeviceCode(): Promise<TraktDeviceCodeResponse> {
  try {
    const response = await fetch('https://api.trakt.tv/oauth/device/code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ client_id: TRAKT_CLIENT_ID })
    });

    if (!response.ok) throw new Error('Falha ao obter código de pareamento do Trakt.');
    return response.json();
  } catch (e: any) {
    console.error('Erro ao requisitar device code do Trakt:', e);
    throw e;
  }
}

// Poll para verificar se o usuário autorizou no navegador/celular
export async function pollDeviceToken(deviceCode: string): Promise<string | null> {
  try {
    const response = await fetch('https://api.trakt.tv/oauth/device/token', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        code: deviceCode,
        client_id: TRAKT_CLIENT_ID,
        client_secret: '' // Apps públicos/TV não usam secret
      })
    });

    if (response.status === 200) {
      const data = await response.json();
      if (data.access_token) {
        localStorage.setItem('ktv_trakt_token', data.access_token);
        localStorage.setItem('ktv_trakt_refresh_token', data.refresh_token);
        return data.access_token;
      }
    }
    return null;
  } catch (err) {
    console.warn('Erro ao consultar token do Trakt:', err);
    return null;
  }
}

// Desconectar conta do Trakt
export function disconnectTrakt() {
  if (typeof window === 'undefined') return;
  localStorage.removeItem('ktv_trakt_token');
  localStorage.removeItem('ktv_trakt_refresh_token');
}

// Envia status de scrobble (start | pause | stop) para sincronização em tempo real
export async function sendTraktScrobble(action: 'start' | 'pause' | 'stop', imdbId: string, progress: number, type: 'movie' | 'series', episodeNumber = 1, seasonNumber = 1): Promise<boolean> {
  if (typeof window === 'undefined') return false;
  const token = localStorage.getItem('ktv_trakt_token');
  if (!token) return false;

  const url = `https://api.trakt.tv/scrobble/${action}`;
  const payload: any = {
    progress,
    app_version: '1.0.0',
    app_date: '2026-07-05'
  };

  const idObject = { ids: { imdb: imdbId } };

  if (type === 'series') {
    payload.episode = {
      season: seasonNumber,
      number: episodeNumber,
      ...idObject
    };
  } else {
    payload.movie = idObject;
  }

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        'trakt-api-version': '2',
        'trakt-api-key': TRAKT_CLIENT_ID
      },
      body: JSON.stringify(payload)
    });

    return response.ok;
  } catch (err) {
    console.warn('Erro ao enviar scrobble para o Trakt:', err);
    return false;
  }
}
