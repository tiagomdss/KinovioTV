// Cliente de Conexão Emby/Jellyfin para o KTV

export interface JellyfinServer {
  serverUrl: string;
  apiKey: string;
  userId?: string;
  username?: string;
  name: string;
}

// Carregar configurações de servidores ativos do localStorage
export function getJellyfinServers(): JellyfinServer[] {
  if (typeof window === 'undefined') return [];
  const saved = localStorage.getItem('ktv_jellyfin_servers');
  return saved ? JSON.parse(saved) : [];
}

// Conectar e autenticar com um novo servidor Jellyfin/Emby
export async function connectJellyfinServer(serverUrl: string, apiKey: string, serverName = 'Jellyfin Server'): Promise<JellyfinServer> {
  const cleanUrl = serverUrl.trim().replace(/\/$/, '');

  try {
    // Validar conexão buscando o status do sistema do Jellyfin
    // A rota /System/Info fornece dados do servidor e valida o token API Key no header "X-Emby-Token"
    const response = await fetch(`${cleanUrl}/System/Info`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'X-Emby-Token': apiKey
      }
    });

    if (!response.ok) {
      throw new Error('Chave de API inválida ou servidor inacessível.');
    }

    const info = await response.json();
    const server: JellyfinServer = {
      serverUrl: cleanUrl,
      apiKey,
      name: info.ServerName || serverName
    };

    // Salvar nas configurações locais
    const currentServers = getJellyfinServers();
    if (!currentServers.some(s => s.serverUrl === cleanUrl)) {
      currentServers.push(server);
      localStorage.setItem('ktv_jellyfin_servers', JSON.stringify(currentServers));
    }

    return server;
  } catch (error: any) {
    console.error('Falha ao conectar ao Jellyfin:', error);
    throw new Error(`Erro de Conexão: ${error.message}`);
  }
}

// Remover servidor configurado
export function disconnectJellyfinServer(serverUrl: string) {
  const currentServers = getJellyfinServers();
  const updated = currentServers.filter(s => s.serverUrl !== serverUrl);
  localStorage.setItem('ktv_jellyfin_servers', JSON.stringify(updated));
}

// Reportar progresso de reprodução de volta para o Jellyfin
export async function reportPlaybackProgress(
  server: JellyfinServer, 
  itemId: string, 
  positionTicks: number, 
  isPaused: boolean, 
  isStopped: boolean
) {
  const body = {
    ItemId: itemId,
    PositionTicks: positionTicks, // Ticks de áudio/vídeo (1 tick = 100 nanosegundos)
    IsPaused: isPaused,
    IsStopped: isStopped
  };

  const endpoint = isStopped ? 'Stopped' : isPaused ? 'Progress' : 'Start';

  try {
    await fetch(`${server.serverUrl}/Sessions/Playing/${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Emby-Token': server.apiKey
      },
      body: JSON.stringify(body)
    });
  } catch (e) {
    console.warn(`Falha ao reportar progresso ao Jellyfin (${server.name}):`, e);
  }
}
