/** Canonical big-O classes for the self-check. Matching is by class, never by substring. */

export const COMPLEXITY_OPTS = [
  'O(1)',
  'O(log n)',
  'O(n)',
  'O(n log k)',
  'O(n log n)',
  'O(n^2)',
  'O(n^3)',
  'O(mn)',
  'O(V+E)',
  'O(2^n)',
  'O(n!)',
];

export function normalizeComplexity(value) {
  return String(value ?? '')
    .toLowerCase()
    .replace(/[οｏ]/g, 'o')
    .replace(/²/g, '^2')
    .replace(/³/g, '^3')
    .replace(/ⁿ/g, '^n')
    .replace(/√/g, 'sqrt')
    .replace(/α/g, 'alpha')
    .replace(/[·×]/g, '*')
    .replace(/\s+/g, '')
    .replace(/\*\*/g, '^');
}

export function complexityTerms(value) {
  const text = normalizeComplexity(value);
  const terms = [];
  for (let i = 0; i < text.length - 1; i++) {
    if (text[i] !== 'o' || text[i + 1] !== '(') continue;
    let depth = 0;
    let end = i + 1;
    for (; end < text.length; end++) {
      if (text[end] === '(') depth += 1;
      else if (text[end] === ')') {
        depth -= 1;
        if (depth === 0) {
          end += 1;
          break;
        }
      }
    }
    if (depth === 0 && end > i + 2) terms.push(text.slice(i, end));
    i = end - 1;
  }
  return terms;
}

function classifyBody(body) {
  const b = body;
  if (b === '1') return 'O(1)';
  if (b === 'v+e' || b === 'e+v') return 'O(V+E)';
  if (b.includes('!')) return 'O(n!)';
  if (b.includes('c(n,k)')) return 'O(2^n)';
  if (/[a-z]\^3/.test(b)) return 'O(n^3)';
  if (/[a-z]\^2/.test(b)) return 'O(n^2)';
  if (b.includes('^')) return 'O(2^n)';
  if (b.includes('alpha')) {
    if (b.includes('e') && /[nv]/.test(b)) return 'O(V+E)';
    return 'O(n)';
  }
  if (b.includes('log')) {
    const flat = b.replace(/\*/g, '');
    if (flat.startsWith('log')) return 'O(log n)';
    if (/nlogn/.test(flat) && !/logk|loga|logm/.test(flat)) return 'O(n log n)';
    return 'O(n log k)';
  }
  if (b === 'mn' || b === 'nm' || b === 'm*n' || b === 'n*m') return 'O(mn)';
  if (b.includes('*')) return 'O(mn)';
  return 'O(n)';
}

export function complexityClass(value) {
  const term = complexityTerms(value)[0];
  if (!term || !term.endsWith(')')) return '';
  return classifyBody(term.slice(2, -1));
}

export function complexityMatch(user, expected) {
  const selected = complexityClass(user);
  if (!selected) return false;
  return complexityTerms(expected).some((term) => classifyBody(term.slice(2, -1)) === selected);
}
