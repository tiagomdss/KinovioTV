// Motor de Navegação Espacial (D-Pad/Controle Remoto) para TVs

let globalActiveElementId = '';

export function getActiveElementId() {
  return globalActiveElementId;
}

export function setActiveElementId(id: string) {
  globalActiveElementId = id;
  const el = document.getElementById(id);
  if (el) {
    // Remover classe focus de todos
    document.querySelectorAll('.focusable').forEach(item => {
      item.classList.remove('focused');
    });
    // Adicionar classe focus no elemento ativo
    el.classList.add('focused');
    el.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'nearest' });
  }
}

// Heurística de cálculo de distância espacial entre retângulos
function getDistance(rectA: DOMRect, rectB: DOMRect, direction: string): number {
  const centerA = {
    x: rectA.left + rectA.width / 2,
    y: rectA.top + rectA.height / 2
  };
  const centerB = {
    x: rectB.left + rectB.width / 2,
    y: rectB.top + rectB.height / 2
  };

  const dx = centerB.x - centerA.x;
  const dy = centerB.y - centerA.y;

  // Verificar se o elemento B está na direção correta
  switch (direction) {
    case 'left':
      if (dx >= 0) return Infinity;
      return Math.abs(dx) + Math.abs(dy) * 4; // Penalizar desalinhamento vertical
    case 'right':
      if (dx <= 0) return Infinity;
      return dx + Math.abs(dy) * 4;
    case 'up':
      if (dy >= 0) return Infinity;
      return Math.abs(dy) + Math.abs(dx) * 4; // Penalizar desalinhamento horizontal
    case 'down':
      if (dy <= 0) return Infinity;
      return dy + Math.abs(dx) * 4;
    default:
      return Infinity;
  }
}

export function navigate(direction: 'left' | 'right' | 'up' | 'down') {
  const currentEl = document.getElementById(globalActiveElementId);
  if (!currentEl) {
    // Se nenhum elemento estiver em foco, focar no primeiro elemento disponível
    const firstFocusable = document.querySelector('.focusable');
    if (firstFocusable) {
      setActiveElementId(firstFocusable.id);
    }
    return;
  }

  const currentRect = currentEl.getBoundingClientRect();
  const allFocusables = Array.from(document.querySelectorAll('.focusable:not([disabled])'));
  
  let closestEl: Element | null = null;
  let minDistance = Infinity;

  allFocusables.forEach(el => {
    if (el.id === globalActiveElementId) return;

    const targetRect = el.getBoundingClientRect();
    const distance = getDistance(currentRect, targetRect, direction);

    if (distance < minDistance) {
      minDistance = distance;
      closestEl = el;
    }
  });

  if (closestEl) {
    setActiveElementId((closestEl as HTMLElement).id);
  }
}

// Inicializar ouvintes globais
let isInitialized = false;

export function initSpatialNavigation() {
  if (typeof window === 'undefined' || isInitialized) return;
  isInitialized = true;

  window.addEventListener('keydown', (e) => {
    const keys = ['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Enter'];
    if (!keys.includes(e.key)) return;

    // Impedir scroll padrão do navegador
    e.preventDefault();

    if (e.key === 'Enter') {
      const activeEl = document.getElementById(globalActiveElementId);
      if (activeEl) {
        activeEl.click();
      }
    } else {
      const dirMap: Record<string, 'left' | 'right' | 'up' | 'down'> = {
        ArrowLeft: 'left',
        ArrowRight: 'right',
        ArrowUp: 'up',
        ArrowDown: 'down'
      };
      navigate(dirMap[e.key]);
    }
  });

  // Focar primeiro elemento ao iniciar
  setTimeout(() => {
    const firstFocusable = document.querySelector('.focusable');
    if (firstFocusable) {
      setActiveElementId(firstFocusable.id);
    }
  }, 300);
}
