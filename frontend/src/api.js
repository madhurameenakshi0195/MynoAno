const KEY = 'mynoano_token';
export const getToken = () => localStorage.getItem(KEY);
export const setToken = (t) => (t ? localStorage.setItem(KEY, t) : localStorage.removeItem(KEY));

/** Thin fetch wrapper: adds the JWT, parses JSON, throws Error(message) on failure. */
export async function api(path, { method = 'GET', body, form } = {}) {
  const headers = {};
  const token = getToken();
  if (token) headers.Authorization = 'Bearer ' + token;
  let data;
  if (form) data = form;
  else if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
    data = JSON.stringify(body);
  }
  const res = await fetch('/api' + path, { method, headers, body: data });
  if (res.status === 401 && token && !path.startsWith('/auth')) {
    setToken(null);
    window.location.href = '/login';
    return null;
  }
  if (!res.ok) {
    let msg = 'Something went wrong';
    try { msg = (await res.json()).error || msg; } catch { /* no body */ }
    throw new Error(msg);
  }
  if (res.status === 204) return null;
  return res.json().catch(() => null);
}
