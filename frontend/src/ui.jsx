import { createContext, useCallback, useContext, useEffect, useState } from 'react';

export const G = [
  'linear-gradient(160deg,#C8102E,#5A000B 70%,#160609)',
  'linear-gradient(160deg,#E23D50,#8B0000 60%,#26070D)',
  'linear-gradient(160deg,#8B0000,#3A0008 65%,#080607)',
  'linear-gradient(160deg,#F0A0A5,#A51D35 55%,#310A12)',
  'linear-gradient(160deg,#A80F2D,#292020 70%,#080607)',
];

export const fmt = (t) => new Date(t).toLocaleString([], { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' });
export const hoursLeft = (t) => Math.max(1, Math.ceil((new Date(t) - Date.now()) / 36e5));
export const minsLeft = (t) => Math.max(1, Math.ceil((new Date(t) - Date.now()) / 6e4));
export const isoLocal = (ms) => new Date(ms - new Date().getTimezoneOffset() * 6e4).toISOString().slice(0, 16);

export const Avatar = ({ name, style = 0, size = 44 }) => (
  <div className="av" style={{ background: G[style % G.length], width: size, height: size }}>{(name || '?')[0].toUpperCase()}</div>
);

export const Chip = ({ on, onClick, children }) => (
  <button type="button" className={'chip' + (on ? ' on' : '')} onClick={onClick}>{children}</button>
);

export function Seg({ options, value, onChange }) {
  return (
    <div className="seg" style={{ margin: '12px 0' }}>
      {options.map(([k, label]) => (
        <button key={k} type="button" className={value === k ? 'on' : ''} onClick={() => onChange(k)}>{label}</button>
      ))}
    </div>
  );
}

export const Toggle = ({ on, onChange, label }) => (
  <button type="button" className={'sw' + (on ? ' on' : '')} role="switch" aria-checked={on} aria-label={label} onClick={() => onChange(!on)} />
);

export function Sheet({ onClose, children }) {
  return (
    <div className="sheet" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div>
        {children}
        <button className="btn g" style={{ marginTop: 10 }} onClick={onClose}>Close</button>
      </div>
    </div>
  );
}

const ToastCtx = createContext(() => {});
export const useToast = () => useContext(ToastCtx);

export function ToastProvider({ children }) {
  const [msg, setMsg] = useState('');
  const show = useCallback((m) => { setMsg(m); setTimeout(() => setMsg(''), 2400); }, []);
  return (
    <ToastCtx.Provider value={show}>
      {children}
      {msg && <div className="toast" style={{ position: 'fixed', maxWidth: 408, margin: '0 auto' }} role="status">{msg}</div>}
    </ToastCtx.Provider>
  );
}

/** Loads data on mount (and optionally on an interval). */
export function useLoad(fn, deps = [], every = 0) {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const load = useCallback(() => fn().then((x) => { setData(x); setError(''); }).catch((e) => setError(e.message)), deps);
  useEffect(() => {
    load();
    if (!every) return undefined;
    const t = setInterval(load, every);
    return () => clearInterval(t);
  }, [load, every]);
  return { data, error, reload: load, setData };
}

export function playSwitch(up) {
  try {
    const c = new (window.AudioContext || window.webkitAudioContext)();
    const o = c.createOscillator(), g = c.createGain(), t = c.currentTime;
    o.type = 'sine';
    o.frequency.setValueAtTime(up ? 180 : 420, t);
    o.frequency.exponentialRampToValueAtTime(up ? 520 : 150, t + 0.5);
    g.gain.setValueAtTime(0.0001, t);
    g.gain.exponentialRampToValueAtTime(0.15, t + 0.08);
    g.gain.exponentialRampToValueAtTime(0.0001, t + 0.6);
    o.connect(g); g.connect(c.destination); o.start(); o.stop(t + 0.65);
  } catch { /* audio unavailable */ }
}

/** MYNO <-> ANO switch: ember portal animation, optional sound, then navigate. */
export function goMode(navigate, to, sound = true) {
  if (sound) playSwitch(to === '/ano');
  const d = document.createElement('div');
  d.className = 'portal';
  document.getElementById('app')?.appendChild(d);
  setTimeout(() => d.remove(), 900);
  navigate(to);
}
