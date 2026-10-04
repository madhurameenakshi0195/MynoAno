import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { api, getToken, setToken } from './api.js';

const Ctx = createContext(null);
export const useAuth = () => useContext(Ctx);

export function AuthProvider({ children }) {
  const [me, setMe] = useState(null);
  const [ready, setReady] = useState(false);

  const refresh = useCallback(async () => {
    if (!getToken()) { setMe(null); setReady(true); return; }
    try { setMe(await api('/me')); } catch { setMe(null); }
    setReady(true);
  }, []);

  useEffect(() => { refresh(); }, [refresh]);

  const login = async (token) => { setToken(token); await refresh(); };
  const logout = () => { setToken(null); setMe(null); };

  return <Ctx.Provider value={{ me, ready, refresh, login, logout }}>{children}</Ctx.Provider>;
}
