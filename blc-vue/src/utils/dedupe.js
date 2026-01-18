// Small helpers to keep list rendering stable even if backend returns duplicates.

function normalizeStr(v) {
  if (v === null || v === undefined) return '';
  return String(v).trim();
}

export function movieIdentityKey(m) {
  if (!m) return '';
  // Prefer ID; treat "1" and 1 as same.
  const id = m.id === 0 || m.id ? String(m.id).trim() : '';
  if (id) return `id:${id}`;
  // Fallback: a best-effort business key (used only if id is missing).
  const title = normalizeStr(m.title);
  const date = normalizeStr(m.date);
  const press = normalizeStr(m.press);
  return `k:${title}|${date}|${press}`;
}

export function dedupeMovies(items) {
  const arr = Array.isArray(items) ? items : [];
  const seen = new Set();
  const out = [];

  for (const m of arr) {
    const key = movieIdentityKey(m);
    if (!key) continue;
    if (seen.has(key)) continue;
    seen.add(key);
    out.push(m);
  }
  return out;
}
