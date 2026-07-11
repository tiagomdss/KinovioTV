<template>
  <div class="p-5 sm:p-8 lg:p-14 max-w-4xl mx-auto flex flex-col gap-8 animate-fade-in pb-28">
    <div>
      <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white">Configurações</h2>
      <p class="text-xs text-text-muted mt-1">Gerencie seus provedores, contas e preferências</p>
    </div>

    <!-- Cloud Account (Netflix style sync) -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-accent" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"/></svg>
        Sua Conta KinovioTV
      </h3>
      <div class="flex items-center justify-between p-4 rounded-xl bg-glass border border-glass-border">
        <div>
          <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide">E-mail Cadastrado</span>
          <p class="text-xs font-semibold text-white mt-0.5">{{ userEmail || 'usuario@kinovio.com' }}</p>
        </div>
        <button 
          id="btn-account-logout"
          @click="$emit('logout')"
          class="focusable px-4 py-2 bg-red-500/10 hover:bg-red-500/20 text-red-400 text-xs font-bold rounded-xl transition-smooth"
        >
          Sair da Conta
        </button>
      </div>
    </section>

    <!-- Playback behavior -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <div>
        <p class="text-[10px] font-bold uppercase tracking-[0.16em] text-accent">Reprodução</p>
        <h3 class="mt-1 text-sm font-bold text-white">Ao tocar em Assistir ou em um episódio</h3>
        <p class="mt-1 text-[11px] leading-relaxed text-text-secondary">Escolha se o KinovioTV inicia a melhor fonte disponível ou mostra os servidores antes de reproduzir.</p>
      </div>
      <div class="grid grid-cols-2 gap-2 rounded-xl border border-white/8 bg-black/20 p-1.5" role="radiogroup" aria-label="Modo de reprodução">
        <button
          class="focusable min-h-12 rounded-lg px-3 text-xs font-bold transition-smooth"
          :class="playbackMode === 'automatic' ? 'bg-accent text-white shadow-lg shadow-accent/20' : 'text-text-secondary hover:bg-white/5 hover:text-white'"
          role="radio"
          :aria-checked="playbackMode === 'automatic'"
          @click="$emit('update:playback-mode', 'automatic')"
        >Automático</button>
        <button
          class="focusable min-h-12 rounded-lg px-3 text-xs font-bold transition-smooth"
          :class="playbackMode === 'manual' ? 'bg-accent text-white shadow-lg shadow-accent/20' : 'text-text-secondary hover:bg-white/5 hover:text-white'"
          role="radio"
          :aria-checked="playbackMode === 'manual'"
          @click="$emit('update:playback-mode', 'manual')"
        >Escolher fonte</button>
      </div>
    </section>

    <!-- Stremio Addons -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-brand" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15a4.5 4.5 0 004.5 4.5H18a3.75 3.75 0 001.332-7.257 3 3 0 00-3.758-3.848 5.25 5.25 0 00-10.233 2.33A4.502 4.502 0 002.25 15z"/></svg>
        Addons do Stremio
      </h3>
      
      <div class="flex gap-2">
        <input 
          type="text" 
          v-model="newAddonUrl" 
          placeholder="Cole o link do manifesto (manifest.json)" 
          class="flex-1 glass-card px-4 py-2.5 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-accent/40 transition-smooth"
        />
        <button 
          id="btn-install-addon"
          @click="$emit('install-addon', newAddonUrl); newAddonUrl = ''"
          class="focusable px-4 py-2.5 bg-brand hover:bg-blue-600 text-white text-xs font-bold rounded-xl transition-smooth"
        >
          Instalar
        </button>
      </div>

      <div class="flex flex-col gap-2 max-h-48 overflow-y-auto no-scrollbar">
        <div 
          v-for="addon in addons" 
          :key="addon.manifestUrl"
          class="p-3 rounded-xl glass flex justify-between items-center"
        >
          <div>
            <h4 class="text-[11px] font-semibold text-white">{{ addon.name }}</h4>
            <p class="text-[9px] text-text-muted truncate max-w-[220px] mt-0.5">{{ addon.description || 'Sem descrição' }}</p>
          </div>
          <button 
            @click="$emit('uninstall-addon', addon.manifestUrl)"
            class="focusable p-2 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500/20 transition-fast"
          >
            <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"/></svg>
          </button>
        </div>
        <p v-if="addons.length === 0" class="text-[10px] text-text-muted text-center py-4">Nenhum addon instalado</p>
      </div>
    </section>

    <!-- Jellyfin / Emby Servers -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5.25 14.25h13.5m-13.5 0a3 3 0 01-3-3m3 3a3 3 0 100 6h13.5a3 3 0 100-6m-16.5-3a3 3 0 013-3h13.5a3 3 0 013 3m-19.5 0a4.5 4.5 0 01.9-2.7L5.737 5.1a3.375 3.375 0 012.7-1.35h7.126c1.062 0 2.062.5 2.7 1.35l2.587 3.45a4.5 4.5 0 01.9 2.7m0 0a3 3 0 01-3 3m0 3h.008v.008h-.008v-.008zm0-6h.008v.008h-.008v-.008zm-3 6h.008v.008h-.008v-.008zm0-6h.008v.008h-.008v-.008z"/></svg>
        Servidores Jellyfin / Emby
      </h3>

      <div class="flex flex-col gap-2">
        <input 
          type="text" 
          v-model="newServerUrl" 
          placeholder="URL do Servidor (ex: http://192.168.1.10:8096)" 
          class="glass-card px-4 py-2.5 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-emerald-500/40 transition-smooth"
        />
        <div class="flex gap-2">
          <input 
            type="password" 
            v-model="newServerApiKey" 
            placeholder="Chave de API" 
            class="flex-1 glass-card px-4 py-2.5 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-emerald-500/40 transition-smooth"
          />
          <button 
            id="btn-connect-server"
            @click="$emit('connect-server', { url: newServerUrl, apiKey: newServerApiKey }); newServerUrl = ''; newServerApiKey = ''"
            class="focusable px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl transition-smooth"
          >
            Conectar
          </button>
        </div>
      </div>

      <div class="flex flex-col gap-2 max-h-48 overflow-y-auto no-scrollbar">
        <div 
          v-for="server in servers" 
          :key="server.serverUrl"
          class="p-3 rounded-xl glass flex justify-between items-center"
        >
          <div>
            <h4 class="text-[11px] font-semibold text-white">{{ server.name }}</h4>
            <p class="text-[9px] text-text-muted mt-0.5">{{ server.serverUrl }}</p>
          </div>
          <button 
            @click="$emit('disconnect-server', server.serverUrl)"
            class="focusable p-2 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500/20 transition-fast"
          >
            <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"/></svg>
          </button>
        </div>
        <p v-if="servers.length === 0" class="text-[10px] text-text-muted text-center py-4">Nenhum servidor conectado</p>
      </div>
    </section>

    <!-- Debrid Provider -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 13.5l10.5-11.25L12 10.5h8.25L9.75 21.75 12 13.5H3.75z"/></svg>
        Serviço Debrid
      </h3>

      <div class="flex gap-2">
        <button 
          @click="$emit('set-debrid-provider', 'realdebrid')"
          class="flex-1 px-3 py-2 text-xs font-bold rounded-xl border transition-smooth"
          :class="debridProvider === 'realdebrid' ? 'bg-amber-600 text-white border-amber-500' : 'glass text-text-muted hover:text-white'"
        >
          RealDebrid
        </button>
        <button 
          @click="$emit('set-debrid-provider', 'alldebrid')"
          class="flex-1 px-3 py-2 text-xs font-bold rounded-xl border transition-smooth"
          :class="debridProvider === 'alldebrid' ? 'bg-amber-600 text-white border-amber-500' : 'glass text-text-muted hover:text-white'"
        >
          AllDebrid
        </button>
      </div>

      <input 
        type="password" 
        :value="debridToken"
        @input="$emit('update:debridToken', $event.target.value)"
        placeholder="Chave de API / Token" 
        class="glass-card px-4 py-2.5 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-amber-500/40 transition-smooth"
      />

      <button 
        @click="$emit('save-settings')"
        class="focusable py-2.5 bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold rounded-xl transition-smooth"
      >
        Salvar Configurações
      </button>
    </section>

    <!-- IPTV Playlists -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 7.5l16.5-4.125M12 6.75c-2.708 0-5.363.224-7.948.655C2.999 7.58 2.25 8.507 2.25 9.574v9.176A2.25 2.25 0 004.5 21h15a2.25 2.25 0 002.25-2.25V9.574c0-1.067-.75-1.994-1.802-2.169A47.865 47.865 0 0012 6.75zm-2.25 5.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm4.5 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"/></svg>
        Playlists IPTV (M3U)
      </h3>

      <div class="flex flex-col gap-2">
        <input 
          type="text" 
          v-model="newIptvName" 
          placeholder="Nome da Playlist" 
          class="glass-card px-4 py-2 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-purple-500/40 transition-smooth"
        />
        <div class="flex gap-2">
          <input 
            type="text" 
            v-model="newIptvUrl" 
            placeholder="URL .m3u ou caminho local" 
            class="flex-1 glass-card px-4 py-2 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-purple-500/40 transition-smooth"
          />
          <button 
            @click="$emit('add-iptv', { name: newIptvName, url: newIptvUrl }); newIptvName = ''; newIptvUrl = ''"
            class="focusable px-4 py-2 bg-brand hover:bg-blue-600 text-white text-xs font-bold rounded-xl transition-smooth"
          >
            Adicionar
          </button>
        </div>
      </div>

      <div class="flex flex-col gap-2 max-h-40 overflow-y-auto no-scrollbar">
        <div 
          v-for="(playlist, index) in iptvPlaylists" 
          :key="index"
          class="p-3 rounded-xl glass flex justify-between items-center"
        >
          <div>
            <h4 class="text-[11px] font-semibold text-white">{{ playlist.name }}</h4>
            <p class="text-[9px] text-text-muted truncate max-w-[200px] mt-0.5">{{ playlist.url }}</p>
          </div>
          <button 
            @click="$emit('remove-iptv', index)"
            class="p-2 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500/20 transition-fast"
          >
            <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"/></svg>
          </button>
        </div>
      </div>
    </section>

    <!-- TMDB Integration Settings -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-.778.099-1.533.284-2.253"/></svg>
        Metadados do TMDB & JustWatch
      </h3>
      <p class="text-[11px] text-text-secondary leading-relaxed">
        Adicione sua própria chave de API do TMDB para carregar sinopses em Português (PT-BR), país de origem, idiomas e os serviços de streaming oficiais JustWatch (Netflix, Disney+, etc.). Deixe em branco para usar a chave padrão pública.
      </p>
      
      <div class="flex flex-col gap-2">
        <input 
          type="password" 
          :value="tmdbApiKey"
          @input="$emit('update:tmdbApiKey', $event.target.value)"
          placeholder="Chave de API do TMDB (Ex: 8d8250...)" 
          class="glass-card px-4 py-2.5 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-blue-500/40 transition-smooth"
        />
        <div class="flex gap-2">
          <button 
            @click="$emit('reset-tmdb-key')"
            class="px-4 py-2 bg-glass border border-glass-border hover:bg-glass-hover text-text-secondary hover:text-white text-xs font-bold rounded-xl transition-smooth flex-1"
          >
            Usar Chave Padrão
          </button>
          <button 
            @click="$emit('save-settings')"
            class="focusable px-5 py-2 bg-brand hover:bg-blue-600 text-white text-xs font-bold rounded-xl transition-smooth flex-1"
          >
            Salvar Chave
          </button>
        </div>
      </div>
    </section>

    <!-- Trakt.tv Sincronização -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-rose-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/></svg>
        Histórico & Trakt.tv
      </h3>
      <p class="text-[11px] text-text-secondary leading-relaxed">
        Sincronize seu histórico de reprodução automaticamente com sua conta no Trakt.tv para marcar como assistido e salvar o progresso.
      </p>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full" :class="traktConnected ? 'bg-emerald-500 shadow-glow-success' : 'bg-red-500'"></span>
          <span class="text-xs text-white font-medium">{{ traktConnected ? 'Conectado ao Trakt' : 'Desconectado' }}</span>
        </div>
        <button 
          v-if="!traktConnected"
          @click="$emit('connect-trakt')"
          class="focusable px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-xl transition-smooth"
        >
          Conectar Conta
        </button>
        <button 
          v-else
          @click="$emit('disconnect-trakt')"
          class="focusable px-4 py-2 bg-red-950/40 text-red-400 border border-red-500/10 hover:bg-red-900/30 text-xs font-bold rounded-xl transition-smooth"
        >
          Desconectar
        </button>
      </div>
    </section>

    <!-- Legendas (OpenSubtitles) -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"/></svg>
        Legendas (OpenSubtitles)
      </h3>
      <p class="text-[11px] text-text-secondary leading-relaxed">
        Configure o suporte a legendas de forma manual ou automática nos vídeos. Opcionalmente, entre com sua conta gratuita do OpenSubtitles.
      </p>

      <div class="flex flex-col gap-3">
        <!-- Modo Auto/Manual -->
        <div class="flex items-center justify-between border-b border-glass-border pb-3">
          <span class="text-xs text-white font-medium">Download Automático de Legendas</span>
          <button 
            @click="$emit('update:subtitleAuto', !subtitleAuto)"
            class="px-3.5 py-1.5 rounded-lg text-[10px] font-bold uppercase transition-smooth border"
            :class="subtitleAuto ? 'bg-amber-600 text-white border-amber-500' : 'glass text-text-muted hover:text-white'"
          >
            {{ subtitleAuto ? 'Automático' : 'Manual' }}
          </button>
        </div>

        <!-- Idioma Padrão -->
        <div class="flex items-center justify-between border-b border-glass-border pb-3">
          <span class="text-xs text-white font-medium">Idioma de Preferência</span>
          <select 
            :value="subtitleLanguage"
            @change="$emit('update:subtitleLanguage', $event.target.value)"
            class="glass bg-cinema-deep border border-glass-border rounded-xl px-3 py-1.5 text-xs text-white focus:outline-none"
          >
            <option value="pt-BR">Português (BR)</option>
            <option value="en">Inglês</option>
            <option value="es">Espanhol</option>
            <option value="fr">Francês</option>
            <option value="off">Nenhum (Desativado)</option>
          </select>
        </div>

        <!-- Visualização de Temporadas -->
        <div class="flex items-center justify-between border-b border-glass-border pb-3">
          <span class="text-xs text-white font-medium">Estilo de Temporadas</span>
          <select 
            :value="seasonViewMode"
            @change="$emit('update:season-view-mode', $event.target.value)"
            class="glass bg-cinema-deep border border-glass-border rounded-xl px-3 py-1.5 text-xs text-white focus:outline-none"
          >
            <option value="posters">Pôsteres de Temporada (Netflix)</option>
            <option value="buttons">Botões Simples (Clássico)</option>
          </select>
        </div>

        <!-- Credenciais OpenSubtitles -->
        <div class="flex flex-col gap-2">
          <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide">Conta OpenSubtitles (Opcional)</span>
          <input 
            type="text" 
            :value="subtitleUsername"
            @input="$emit('update:subtitleUsername', $event.target.value)"
            placeholder="Usuário OpenSubtitles" 
            class="glass-card px-4 py-2.5 rounded-xl text-xs text-white focus:outline-none focus:border-amber-500/40 transition-smooth"
          />
          <input 
            type="password" 
            :value="subtitlePassword"
            @input="$emit('update:subtitlePassword', $event.target.value)"
            placeholder="Senha OpenSubtitles" 
            class="glass-card px-4 py-2.5 rounded-xl text-xs text-white focus:outline-none focus:border-amber-500/40 transition-smooth"
          />
        </div>
      </div>
    </section>

    <!-- Outras APIs (TVDB) -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-indigo-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"/></svg>
        Metadados do TVDB
      </h3>
      <p class="text-[11px] text-text-secondary leading-relaxed">
        Sua chave oficial do TheTVDB para metadados adicionais de séries e elencos.
      </p>
      <div class="flex flex-col gap-2">
        <input 
          type="password" 
          :value="tvdbApiKey"
          @input="$emit('update:tvdbApiKey', $event.target.value)"
          placeholder="Chave de API do TVDB" 
          class="glass-card px-4 py-2.5 rounded-xl text-xs text-white placeholder:text-text-muted focus:outline-none focus:border-indigo-500/40 transition-smooth"
        />
        <button 
          @click="$emit('save-settings')"
          class="focusable py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-xl transition-smooth"
        >
          Salvar Chave TVDB
        </button>
      </div>
    </section>

    <!-- Controle Parental -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-4">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        Controle Parental / Conteúdo Adulto (+18)
      </h3>
      <p class="text-[11px] text-text-secondary leading-relaxed">
        Se ativado, as capas e títulos de filmes e séries com conteúdo sensível ou de classificação etária +18 serão exibidos sem qualquer desfoque. Caso contrário, serão borrados automaticamente.
      </p>
      <div class="flex items-center justify-between">
        <span class="text-xs text-white font-medium">Exibir capas +18 sem desfoque</span>
        <button 
          @click="$emit('toggle-nsfw')"
          class="focusable px-4 py-2 rounded-xl text-xs font-bold transition-smooth border"
          :class="enableNsfw ? 'bg-accent text-white border-accent shadow-md shadow-accent/15' : 'bg-glass/10 text-text-secondary hover:bg-glass/30 hover:text-white border border-glass-border/40'"
        >
          {{ enableNsfw ? 'Habilitado' : 'Desabilitado' }}
        </button>
      </div>
    </section>

    <!-- About -->
    <section class="glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-2">
      <h3 class="text-sm font-bold text-white flex items-center gap-2">
        <svg class="w-5 h-5 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z"/></svg>
        Sobre
      </h3>
      <p class="text-[11px] text-text-secondary leading-relaxed">
        <strong class="text-white">KinovioTV</strong> — Seu cinema pessoal premium. Agregação de múltiplas fontes com organização inteligente.
      </p>
      <p class="text-[10px] text-text-muted">Versão 1.0.0</p>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue';

defineProps({
  userEmail: { type: String, default: '' },
  addons: { type: Array, default: () => [] },
  servers: { type: Array, default: () => [] },
  iptvPlaylists: { type: Array, default: () => [] },
  debridProvider: { type: String, default: 'realdebrid' },
  debridToken: { type: String, default: '' },
  tmdbApiKey: { type: String, default: '' },
  tvdbApiKey: { type: String, default: '' },
  traktConnected: { type: Boolean, default: false },
  subtitleAuto: { type: Boolean, default: true },
  subtitleLanguage: { type: String, default: 'pt-BR' },
  subtitleUsername: { type: String, default: '' },
  subtitlePassword: { type: String, default: '' },
  enableNsfw: { type: Boolean, default: false },
  seasonViewMode: { type: String, default: 'posters' },
  playbackMode: { type: String, default: 'automatic' },
});

defineEmits([
  'logout',
  'install-addon', 'uninstall-addon',
  'connect-server', 'disconnect-server',
  'set-debrid-provider', 'update:debridToken', 'update:tmdbApiKey', 'update:tvdbApiKey',
  'update:subtitleAuto', 'update:subtitleLanguage', 'update:subtitleUsername', 'update:subtitlePassword',
  'update:season-view-mode', 'update:playback-mode',
  'save-settings', 'add-iptv', 'remove-iptv', 'reset-tmdb-key',
  'connect-trakt', 'disconnect-trakt', 'toggle-nsfw'
]);

const newAddonUrl = ref('');
const newServerUrl = ref('');
const newServerApiKey = ref('');
const newIptvName = ref('');
const newIptvUrl = ref('');
</script>

<style scoped>
select option {
  background-color: #0b0b14 !important;
  color: #ffffff !important;
}
</style>
