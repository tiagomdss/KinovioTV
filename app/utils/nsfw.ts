export function isNsfwContent(item: any): boolean {
  if (!item) return false;
  const title = (item.title || item.name || '').toLowerCase();
  const genre = (item.genre || '').toLowerCase();
  const description = (item.description || '').toLowerCase();
  
  // Lista robusta de palavras-chave para conteúdo maduro / adulto
  const nsfwKeywords = [
    'hentai', 'adult', 'erótico', 'erotico', 'erotica', 'nsfw', 'porn', 
    'sexy', 'nudez', 'ecchi', 'mature', 'sensual', 'sexo', 'sex ', 
    'softcore', 'hardcore', 'erotism', 'mature content',
    // Palavras-chave asiáticas para conteúdo adulto/sensível (Chinesas, Japonesas, Coreanas)
    '妓', '名妓', '成人', '情色', '色情', '淫', '私处', '强奸', '春宫', '金瓶梅', '肉蒲团',
    '官能', 'アダルト', 'エロ', '裏ビデオ', '美少女', '에로', '성인', '야동', 'mature 18',
    'classification 18', 'mature audience'
  ];
  
  const isKeywordMatch = nsfwKeywords.some(kw => 
    genre.includes(kw) || 
    title.includes(kw) || 
    description.includes(kw)
  );
  
  // Lista de classificações adultas (Incluindo classificação coreana de 19+)
  const rating = String(item.rating || '').toUpperCase();
  const classification = String(item.classification || '').toUpperCase();
  const isAdultRating = ['18', '19', '19+', 'TV-19', 'R-19', 'R', 'TV-MA', 'NC-17', 'X', 'MATURE', 'MA15+', 'MA15'].some(r => 
    rating === r || 
    classification === r || 
    rating.includes(r) || 
    classification.includes(r)
  );
  
  return isKeywordMatch || isAdultRating;
}
