/** Lightweight animated SVG diagrams for core patterns. */

export function diagramFor(patternId) {
  switch (patternId) {
    case 'TWO_POINTERS':
      return twoPointers();
    case 'SLIDING_WINDOW':
      return slidingWindow();
    case 'TREE_DFS':
      return treeDfs();
    case 'TREE_BFS':
      return treeBfs();
    case 'FAST_SLOW_POINTERS':
      return fastSlow();
    case 'INTERVALS':
      return intervals();
    case 'TOP_K_ELEMENTS':
      return topK();
    case 'MODIFIED_BINARY_SEARCH':
      return modifiedBinarySearch();
    case 'BACKTRACKING':
      return backtracking();
    case 'GRAPHS':
      return graphs();
    case 'DYNAMIC_PROGRAMMING':
      return dynamicProgramming();
    case 'TWO_HEAPS':
      return twoHeaps();
    case 'TRIE':
      return trie();
    default:
      return '';
  }
}

function shell(title, body) {
  return `
    <div class="diagram-panel panel">
      <h3 data-diagram-title="${title}">${title}</h3>
      <div class="diagram-stage">${body}</div>
    </div>`;
}

function twoPointers() {
  return shell(
    'Two Pointers',
    `<svg viewBox="0 0 520 140" class="diagram-svg">
      ${[40,100,160,220,280,340,400,460].map((x,i)=>`
        <rect class="cell" x="${x-18}" y="50" width="36" height="36" rx="4"/>
        <text x="${x}" y="73" text-anchor="middle" class="cell-t">${i+1}</text>`).join('')}
      <g class="ptr left-ptr">
        <polygon points="40,40 32,28 48,28" />
        <text x="40" y="22" text-anchor="middle">L</text>
      </g>
      <g class="ptr right-ptr">
        <polygon points="460,40 452,28 468,28" />
        <text x="460" y="22" text-anchor="middle">R</text>
      </g>
    </svg>
    <p class="diagram-caption" data-diagram-caption="TWO_POINTERS">L and R move toward the goal — each step discards impossible pairs.</p>`
  );
}

function slidingWindow() {
  return shell(
    'Sliding Window',
    `<svg viewBox="0 0 520 140" class="diagram-svg">
      ${[50,110,170,230,290,350,410,470].map((x,i)=>`
        <rect class="cell" x="${x-18}" y="50" width="36" height="36" rx="4"/>
        <text x="${x}" y="73" text-anchor="middle" class="cell-t">${'abcxyzpq'[i]}</text>`).join('')}
      <rect class="window" x="92" y="42" width="156" height="52" rx="6" />
      <text x="170" y="30" text-anchor="middle" class="cell-t">window</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="SLIDING_WINDOW">Expand right to include; shrink left when the constraint breaks.</p>`
  );
}

function treeDfs() {
  return shell(
    'Tree DFS',
    `<svg viewBox="0 0 520 180" class="diagram-svg">
      <line x1="260" y1="40" x2="180" y2="90" class="edge"/>
      <line x1="260" y1="40" x2="340" y2="90" class="edge"/>
      <line x1="180" y1="90" x2="130" y2="140" class="edge"/>
      <line x1="180" y1="90" x2="220" y2="140" class="edge"/>
      <circle cx="260" cy="40" r="16" class="node active"/>
      <circle cx="180" cy="90" r="16" class="node"/>
      <circle cx="340" cy="90" r="16" class="node"/>
      <circle cx="130" cy="140" r="16" class="node"/>
      <circle cx="220" cy="140" r="16" class="node"/>
      <path class="dfs-path" d="M260 40 L180 90 L130 140" fill="none"/>
    </svg>
    <p class="diagram-caption" data-diagram-caption="TREE_DFS">Go deep along a branch, then backtrack — preorder / inorder / postorder.</p>`
  );
}

function treeBfs() {
  return shell(
    'Tree BFS',
    `<svg viewBox="0 0 520 180" class="diagram-svg">
      <line x1="260" y1="40" x2="180" y2="90" class="edge"/>
      <line x1="260" y1="40" x2="340" y2="90" class="edge"/>
      <line x1="180" y1="90" x2="130" y2="140" class="edge"/>
      <line x1="180" y1="90" x2="220" y2="140" class="edge"/>
      <circle cx="260" cy="40" r="16" class="node bfs-n0"/>
      <circle cx="180" cy="90" r="16" class="node bfs-n1"/>
      <circle cx="340" cy="90" r="16" class="node bfs-n1"/>
      <circle cx="130" cy="140" r="16" class="node bfs-n2"/>
      <circle cx="220" cy="140" r="16" class="node bfs-n2"/>
      <text x="40" y="50" class="cell-t">L0</text>
      <text x="40" y="100" class="cell-t">L1</text>
      <text x="40" y="150" class="cell-t">L2</text>
      <rect class="bfs-wave" x="40" y="24" width="440" height="32" rx="4" fill="none"/>
    </svg>
    <p class="diagram-caption" data-diagram-caption="TREE_BFS">Queue processes the tree level by level.</p>`
  );
}

function fastSlow() {
  return shell(
    'Fast & Slow Pointers',
    `<svg viewBox="0 0 520 120" class="diagram-svg">
      ${[60,140,220,300,380].map((x,i)=>`
        <circle cx="${x}" cy="60" r="18" class="node"/>
        <text x="${x}" y="65" text-anchor="middle" class="cell-t">${i+1}</text>
        ${i<4?`<line x1="${x+18}" y1="60" x2="${x+62}" y2="60" class="edge"/>`:''}
      `).join('')}
      <path d="M380 60 C430 60, 430 20, 300 20" class="edge" fill="none"/>
      <text x="140" y="105" class="slow-label">slow</text>
      <text x="300" y="105" class="fast-label">fast</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="FAST_SLOW_POINTERS">Fast moves 2×; if they meet, a cycle exists.</p>`
  );
}

function intervals() {
  return shell(
    'Merge Intervals',
    `<svg viewBox="0 0 520 160" class="diagram-svg">
      <rect class="iv iv-a" x="40" y="40" width="120" height="28" rx="4"/>
      <rect class="iv iv-b" x="130" y="80" width="140" height="28" rx="4"/>
      <rect class="iv iv-c" x="300" y="40" width="90" height="28" rx="4"/>
      <rect class="iv iv-merge" x="40" y="120" width="230" height="28" rx="4"/>
      <text x="100" y="59" text-anchor="middle" class="cell-t">[1,4]</text>
      <text x="200" y="99" text-anchor="middle" class="cell-t">[3,7]</text>
      <text x="345" y="59" text-anchor="middle" class="cell-t">[9,12]</text>
      <text x="155" y="139" text-anchor="middle" class="cell-t">merged [1,7]</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="INTERVALS">Sort by start, then merge when the next start overlaps the current end.</p>`
  );
}

function topK() {
  return shell(
    'Top K Elements',
    `<svg viewBox="0 0 520 170" class="diagram-svg">
      ${[[120,130],[200,90],[280,130],[160,50],[240,50]].map(([x,y],i)=>`
        <circle cx="${x}" cy="${y}" r="18" class="node ${i<3?'heap-keep':''}"/>
        <text x="${x}" y="${y+5}" text-anchor="middle" class="cell-t">${[3,8,5,9,7][i]}</text>
      `).join('')}
      <line x1="160" y1="68" x2="120" y2="112" class="edge"/>
      <line x1="160" y1="68" x2="200" y2="72" class="edge"/>
      <line x1="240" y1="68" x2="200" y2="72" class="edge"/>
      <line x1="240" y1="68" x2="280" y2="112" class="edge"/>
      <text x="400" y="80" class="cell-t">heap size K</text>
      <rect class="heap-box" x="360" y="95" width="120" height="40" rx="6"/>
      <text x="420" y="120" text-anchor="middle" class="cell-t">top-K</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="TOP_K_ELEMENTS">Keep a bounded heap of size K; push/pop to retain only the best K.</p>`
  );
}

function modifiedBinarySearch() {
  return shell(
    'Modified Binary Search',
    `<svg viewBox="0 0 520 140" class="diagram-svg">
      ${[50,110,170,230,290,350,410,470].map((x,i)=>`
        <rect class="cell ${i>=2&&i<=5?'half-active':''}" x="${x-18}" y="50" width="36" height="36" rx="4"/>
        <text x="${x}" y="73" text-anchor="middle" class="cell-t">${[4,5,6,7,0,1,2,3][i]}</text>`).join('')}
      <g class="ptr mid-ptr">
        <polygon points="230,40 222,28 238,28" />
        <text x="230" y="22" text-anchor="middle">mid</text>
      </g>
      <text x="80" y="110" class="cell-t">lo</text>
      <text x="440" y="110" class="cell-t">hi</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="MODIFIED_BINARY_SEARCH">Decide which half is sorted or feasible, then discard the other half.</p>`
  );
}

function backtracking() {
  return shell(
    'Backtracking',
    `<svg viewBox="0 0 520 180" class="diagram-svg">
      <line x1="260" y1="30" x2="160" y2="80" class="edge"/>
      <line x1="260" y1="30" x2="360" y2="80" class="edge"/>
      <line x1="160" y1="80" x2="100" y2="140" class="edge"/>
      <line x1="160" y1="80" x2="220" y2="140" class="edge bt-prune"/>
      <line x1="360" y1="80" x2="300" y2="140" class="edge"/>
      <line x1="360" y1="80" x2="420" y2="140" class="edge"/>
      <circle cx="260" cy="30" r="14" class="node active"/>
      <circle cx="160" cy="80" r="14" class="node bt-step"/>
      <circle cx="360" cy="80" r="14" class="node"/>
      <circle cx="100" cy="140" r="14" class="node bt-step"/>
      <circle cx="220" cy="140" r="14" class="node bt-dead"/>
      <circle cx="300" cy="140" r="14" class="node"/>
      <circle cx="420" cy="140" r="14" class="node"/>
      <path class="bt-path" d="M260 30 L160 80 L100 140" fill="none"/>
      <text x="220" y="170" text-anchor="middle" class="cell-t">prune</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="BACKTRACKING">Try a choice, recurse, then undo and try the next option.</p>`
  );
}

function graphs() {
  return shell(
    'Graphs',
    `<svg viewBox="0 0 520 150" class="diagram-svg">
      <line x1="80" y1="80" x2="200" y2="40" class="edge"/>
      <line x1="80" y1="80" x2="200" y2="120" class="edge"/>
      <line x1="200" y1="40" x2="340" y2="80" class="edge"/>
      <line x1="200" y1="120" x2="340" y2="80" class="edge"/>
      <line x1="340" y1="80" x2="450" y2="80" class="edge"/>
      <circle cx="80" cy="80" r="16" class="node active"/>
      <circle cx="200" cy="40" r="16" class="node"/>
      <circle cx="200" cy="120" r="16" class="node"/>
      <circle cx="340" cy="80" r="16" class="node"/>
      <circle cx="450" cy="80" r="16" class="node"/>
    </svg>
    <p class="diagram-caption" data-diagram-caption="GRAPHS">Visit each neighbor once. BFS uses a queue for the nearest unseen node.</p>`
  );
}

function dynamicProgramming() {
  return shell(
    'Dynamic Programming',
    `<svg viewBox="0 0 520 140" class="diagram-svg">
      ${[40, 110, 180, 250, 320, 390].map((x, i) => `
        <rect class="cell${i < 3 ? ' active' : ''}" x="${x}" y="48" width="52" height="36" rx="4"/>
        <text x="${x + 26}" y="72" text-anchor="middle" class="cell-t">dp${i}</text>`).join('')}
      <path d="M92 66 H110 M162 66 H180" class="edge" fill="none"/>
    </svg>
    <p class="diagram-caption" data-diagram-caption="DYNAMIC_PROGRAMMING">Name dp[i] first. Each cell is filled only from cells already known.</p>`
  );
}

function twoHeaps() {
  return shell(
    'Two Heaps',
    `<svg viewBox="0 0 520 150" class="diagram-svg">
      <polygon points="120,110 70,40 170,40" class="cell"/>
      <polygon points="400,110 350,40 450,40" class="cell"/>
      <text x="120" y="80" text-anchor="middle" class="cell-t">max</text>
      <text x="400" y="80" text-anchor="middle" class="cell-t">min</text>
      <text x="260" y="78" text-anchor="middle" class="cell-t">median</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="TWO_HEAPS">The left heap holds the smaller half. The median sits on a root.</p>`
  );
}

function trie() {
  return shell(
    'Trie',
    `<svg viewBox="0 0 520 160" class="diagram-svg">
      <circle cx="260" cy="28" r="12" class="node active"/>
      <line x1="260" y1="40" x2="160" y2="80" class="edge"/>
      <line x1="260" y1="40" x2="360" y2="80" class="edge"/>
      <circle cx="160" cy="90" r="12" class="node"/>
      <circle cx="360" cy="90" r="12" class="node"/>
      <text x="140" y="94" text-anchor="end" class="cell-t">a</text>
      <text x="380" y="94" class="cell-t">b</text>
      <line x1="160" y1="102" x2="120" y2="136" class="edge"/>
      <circle cx="120" cy="144" r="10" class="node"/>
      <text x="100" y="148" text-anchor="end" class="cell-t">t</text>
    </svg>
    <p class="diagram-caption" data-diagram-caption="TRIE">Each edge is one character. A word ends only when the node is marked.</p>`
  );
}
