/** Minimal line diff for solution comparison. */
export function lineDiff(a, b) {
  const left = String(a || '').split('\n');
  const right = String(b || '').split('\n');
  const max = Math.max(left.length, right.length);
  const rows = [];
  for (let i = 0; i < max; i++) {
    const L = left[i];
    const R = right[i];
    if (L === R) rows.push({ type: 'same', left: L ?? '', right: R ?? '' });
    else if (L == null) rows.push({ type: 'add', left: '', right: R });
    else if (R == null) rows.push({ type: 'del', left: L, right: '' });
    else rows.push({ type: 'change', left: L, right: R });
  }
  return rows;
}

export function renderDiff(a, b) {
  const rows = lineDiff(a, b);
  return `<div class="diff-view">${rows
    .map(
      (r) =>
        `<div class="diff-row ${r.type}"><pre class="diff-l">${escape(r.left)}</pre><pre class="diff-r">${escape(r.right)}</pre></div>`
    )
    .join('')}</div>`;
}

function escape(s) {
  return String(s)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;');
}
