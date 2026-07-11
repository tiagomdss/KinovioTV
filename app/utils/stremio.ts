// Cliente de Addons do Stremio para o KTV

export interface StremioAddon {
  manifestUrl: string;
  name: string;
  description?: string;
  icon?: string;
  version?: string;
  types: string[];
  resources: string[];
  catalogs?: Array<{
    type: string;
    id: string;
    name: string;
    extra?: any[];
  }>;
}

export interface StremioStream {
  name: string;
  title: string;
  url?: string;
  infoHash?: string;
  fileIdx?: number;
  externalUrl?: string;
}

// Lista padrão de Addons úteis (Cinemeta para metadados e o KinovioStream oficial)
const DEFAULT_ADDONS: StremioAddon[] = [
  {
    manifestUrl: 'https://v3-cinemeta.strem.io/manifest.json',
    name: 'Cinemeta',
    description: 'Metadados oficiais do Stremio (Filmes e Séries)',
    types: ['movie', 'series'],
    resources: ['catalog', 'meta'],
    catalogs: [
      { type: 'movie', id: 'top', name: 'Filmes Populares' },
      { type: 'series', id: 'top', name: 'Séries Populares' }
    ]
  },
  {
    manifestUrl: 'https://kinoviostream.martinsds.dev.br/manifest.json',
    name: 'KinovioStream',
    description: 'Provedor oficial de streams do KinovioTV',
    types: ['movie', 'series'],
    resources: ['stream']
  }
];

// Carregar addons salvos do localStorage com injeção automática de novos defaults
export function getInstalledAddons(): StremioAddon[] {
  if (typeof window === 'undefined') return DEFAULT_ADDONS;
  
  const saved = localStorage.getItem('ktv_stremio_addons');
  if (!saved) {
    localStorage.setItem('ktv_stremio_addons', JSON.stringify(DEFAULT_ADDONS));
    return DEFAULT_ADDONS;
  }
  
  try {
    const addons = JSON.parse(saved);
    let updated = false;
    
    // Injetar defaults que estejam faltando
    for (const def of DEFAULT_ADDONS) {
      if (!addons.some(a => a.manifestUrl === def.manifestUrl)) {
        addons.push(def);
        updated = true;
      }
    }
    
    if (updated) {
      localStorage.setItem('ktv_stremio_addons', JSON.stringify(addons));
    }
    return addons;
  } catch (e) {
    localStorage.setItem('ktv_stremio_addons', JSON.stringify(DEFAULT_ADDONS));
    return DEFAULT_ADDONS;
  }
}

// Função auxiliar para fazer fetch com fallback para proxy CORS se falhar diretamente
async function fetchWithCorsProxy(url: string): Promise<Response> {
  try {
    const res = await fetch(url);
    if (res.ok) return res;
  } catch (e) {
    console.warn(`Direct fetch falhou para ${url}, tentando via proxy CORS...`, e);
  }
  
  // Usar proxy público de CORS como fallback
  const proxyUrl = `https://corsproxy.io/?${encodeURIComponent(url)}`;
  return await fetch(proxyUrl);
}

// Instalar novo addon via link do manifesto
export async function installAddon(manifestUrl: string): Promise<StremioAddon> {
  // Garantir que a URL do manifesto esteja no formato correto (HTTP/HTTPS)
  let cleanUrl = manifestUrl.trim();
  if (cleanUrl.startsWith('stremio://')) {
    cleanUrl = cleanUrl.replace('stremio://', 'https://');
  }

  try {
    const response = await fetchWithCorsProxy(cleanUrl);
    if (!response.ok) throw new Error('Não foi possível carregar o manifesto do addon.');
    
    const manifest = await response.json();
    
    const addon: StremioAddon = {
      manifestUrl: cleanUrl,
      name: manifest.name,
      description: manifest.description,
      icon: manifest.icon,
      version: manifest.version,
      types: manifest.types || [],
      resources: manifest.resources || [],
      catalogs: manifest.catalogs || []
    };

    const currentAddons = getInstalledAddons();
    // Evitar duplicados
    if (!currentAddons.some(a => a.manifestUrl === cleanUrl)) {
      currentAddons.push(addon);
      localStorage.setItem('ktv_stremio_addons', JSON.stringify(currentAddons));
    }

    return addon;
  } catch (error: any) {
    console.error('Falha ao instalar addon do Stremio:', error);
    throw new Error(`Erro de Instalação: ${error.message}`);
  }
}

// Desinstalar addon
export function uninstallAddon(manifestUrl: string) {
  const currentAddons = getInstalledAddons();
  const updated = currentAddons.filter(a => a.manifestUrl !== manifestUrl);
  localStorage.setItem('ktv_stremio_addons', JSON.stringify(updated));
}

// Buscar itens do catálogo de um addon
export async function getCatalogFromAddon(manifestUrl: string, type: 'movie' | 'series', catalogId: string): Promise<any[]> {
  const baseUrl = manifestUrl.replace('/manifest.json', '');
  const queryUrl = `${baseUrl}/catalog/${type}/${catalogId}.json`;
  
  try {
    const response = await fetchWithCorsProxy(queryUrl);
    if (!response.ok) return [];
    const data = await response.json();
    return data.metas || [];
  } catch (e) {
    console.warn(`Erro ao buscar catálogo ${catalogId} de ${manifestUrl}:`, e);
    return [];
  }
}

// Buscar itens de catálogo pesquisando termo
export async function searchCatalogFromAddon(manifestUrl: string, type: 'movie' | 'series', catalogId: string, query: string): Promise<any[]> {
  const baseUrl = manifestUrl.replace('/manifest.json', '');
  const queryUrl = `${baseUrl}/catalog/${type}/${catalogId}/search=${encodeURIComponent(query)}.json`;
  
  try {
    const response = await fetchWithCorsProxy(queryUrl);
    if (!response.ok) return [];
    const data = await response.json();
    return data.metas || [];
  } catch (e) {
    console.warn(`Erro ao pesquisar ${query} no catálogo ${catalogId} de ${manifestUrl}:`, e);
    return [];
  }
}

// Buscar metadados detalhados de um item
export async function getMetaDetails(manifestUrl: string, type: 'movie' | 'series', id: string): Promise<any> {
  const baseUrl = manifestUrl.replace('/manifest.json', '');
  const queryUrl = `${baseUrl}/meta/${type}/${id}.json`;
  
  try {
    const response = await fetchWithCorsProxy(queryUrl);
    if (!response.ok) return null;
    const data = await response.json();
    return data.meta || null;
  } catch (e) {
    console.warn(`Erro ao obter metadados para ${id} de ${manifestUrl}:`, e);
    return null;
  }
}

// Buscar links de reprodução (streams) de um conteúdo usando addons instalados
export async function getStreamsFromAddons(type: 'movie' | 'series', id: string): Promise<StremioStream[]> {
  const addons = getInstalledAddons();
  const streamAddons = addons.filter(a => {
    return a.resources.includes('stream') || 
           (typeof a.resources[0] === 'object' && (a.resources as any).some((r: any) => r.name === 'stream' || r === 'stream'));
  });
  
  if (streamAddons.length === 0) return [];

  const streamPromises = streamAddons.map(async (addon) => {
    // A rota do Stremio é: {base_url}/stream/{type}/{id}.json
    const baseUrl = addon.manifestUrl.replace('/manifest.json', '');
    const queryUrl = `${baseUrl}/stream/${type}/${id}.json`;

    try {
      const response = await fetchWithCorsProxy(queryUrl);
      if (!response.ok) return [];
      
      const data = await response.json();
      if (!data.streams) return [];

      // Mapear e identificar o addon de origem nos streams
      return data.streams.map((stream: any) => ({
        name: `[${addon.name}] ${stream.name || 'Stream'}`,
        title: stream.title || 'Sem título adicional',
        url: stream.url ? new URL(stream.url, baseUrl).toString() : undefined,
        infoHash: stream.infoHash,
        fileIdx: stream.fileIdx,
        externalUrl: stream.externalUrl,
        behaviorHints: stream.behaviorHints
      }));
    } catch (e) {
      console.warn(`Falha ao consultar streams do addon ${addon.name}:`, e);
      return [];
    }
  });

  const results = await Promise.all(streamPromises);
  return results.flat();
}

// Buscar legendas de um conteúdo usando os addons instalados
export async function getSubtitlesFromAddons(type: 'movie' | 'series', id: string): Promise<any[]> {
  const addons = getInstalledAddons();
  const subAddons = addons.filter(a => {
    return a.resources.includes('subtitles') || 
           (typeof a.resources[0] === 'object' && (a.resources as any).some((r: any) => r.name === 'subtitles' || r === 'subtitles'));
  });
  
  if (subAddons.length === 0) {
    const cinemeta = addons.find(a => a.name.includes('Cinemeta'));
    if (cinemeta) subAddons.push(cinemeta);
  }

  const subPromises = subAddons.map(async (addon) => {
    const baseUrl = addon.manifestUrl.replace('/manifest.json', '');
    const queryUrl = `${baseUrl}/subtitles/${type}/${id}.json`;
    try {
      const response = await fetchWithCorsProxy(queryUrl);
      if (!response.ok) return [];
      const data = await response.json();
      return data.subtitles || [];
    } catch (e) {
      console.warn(`Falha ao buscar legendas do addon ${addon.name}:`, e);
      return [];
    }
  });

  try {
    const results = await Promise.all(subPromises);
    return results.flat().map((sub: any) => ({
      id: sub.id || sub.url,
      lang: sub.lang || 'eng',
      url: sub.url,
      label: sub.lang ? sub.lang.toUpperCase() : 'LEG'
    }));
  } catch (err) {
    console.error('Erro ao processar legendas:', err);
    return [];
  }
}
