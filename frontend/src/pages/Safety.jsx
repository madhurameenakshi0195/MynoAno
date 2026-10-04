import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { fmt, useLoad, useToast } from '../ui.jsx';

export default function Safety() {
  const nav = useNavigate();
  const toast = useToast();
  const { me, refresh } = useAuth();
  const { data: blocked, reload: reloadBlocked } = useLoad(() => api('/safety/blocked'), []);
  const { data: share, reload: reloadShare } = useLoad(() => api('/safety/share'), []);
  const { data: incoming } = useLoad(() => api('/safety/shared-with-me'), [], 15000);
  const { data: friends } = useLoad(() => api('/friends'), []);
  const [trusted, setTrusted] = useState('');
  const [minutes, setMinutes] = useState(60);

  // While sharing, keep the trusted friend's view fresh.
  useEffect(() => {
    if (!share || !navigator.geolocation) return undefined;
    const id = navigator.geolocation.watchPosition(
      (p) => api('/safety/share/location', { method: 'PUT', body: { lat: p.coords.latitude, lng: p.coords.longitude } }).catch(() => {}),
      () => {}, { maximumAge: 30000 });
    return () => navigator.geolocation.clearWatch(id);
  }, [share]);

  const run = (fn, msg) => async () => { try { await fn(); if (msg) toast(msg); } catch (e) { toast(e.message); } };
  const verify = run(async () => { await api('/me/selfie-verify', { method: 'POST' }); await refresh(); }, 'Selfie verified. Level 2 badge added');
  const start = () => {
    const go = (lat, lng) => run(async () => { await api('/safety/share', { method: 'POST', body: { trustedId: +(trusted || friends?.[0]?.card.userId), minutes: +minutes, lat, lng } }); reloadShare(); }, 'Sharing your live spot')();
    if (!navigator.geolocation) return go(null, null);
    navigator.geolocation.getCurrentPosition((p) => go(p.coords.latitude, p.coords.longitude), () => go(null, null));
  };

  return (
    <div className="pad">
      <button className="pill" onClick={() => nav('/me')}>Back</button>
      <h1 style={{ marginTop: 12 }}>Safety</h1>

      <div className="card row sp">
        <div>
          <h3>{me.card.selfieVerified ? 'Level 2: selfie verified' : 'Level 1: email and phone'}</h3>
          <div className="mu">{me.card.selfieVerified ? 'Your badge shows a selfie check' : 'Add a selfie check for a stronger badge'}</div>
        </div>
        {!me.card.selfieVerified && <button className="btn sm" onClick={verify}>Verify</button>}
      </div>

      <div className="card">
        <h3>Meet safely</h3>
        <p className="mu" style={{ margin: '4px 0 8px' }}>Share your live spot with one trusted friend while you meet someone.</p>
        {share ? (
          <div className="row sp"><span className="mu">Sharing until {fmt(share.expiresAt)}</span>
            <button className="btn g sm" onClick={run(async () => { await api('/safety/share', { method: 'DELETE' }); reloadShare(); }, 'Stopped sharing')}>Stop</button></div>
        ) : (
          <>
            <select className="in" aria-label="Trusted friend" value={trusted} onChange={(e) => setTrusted(e.target.value)}>
              {(friends || []).map((f) => <option key={f.card.userId} value={f.card.userId}>{f.card.displayName}</option>)}
            </select>
            <select className="in" aria-label="Duration" value={minutes} onChange={(e) => setMinutes(e.target.value)}>
              <option value="60">1 hour</option><option value="120">2 hours</option><option value="480">Until I stop</option>
            </select>
            <button className="btn" disabled={!friends?.length} onClick={start}>Start sharing</button>
          </>
        )}
      </div>

      {incoming?.length > 0 && (
        <div className="card">
          <h3>Friends sharing with you</h3>
          {incoming.map((s) => (
            <div key={s.ownerId} className="row sp" style={{ marginTop: 10 }}>
              <span>{s.ownerName}</span>
              {s.lat != null && <a href={`https://www.openstreetmap.org/?mlat=${s.lat}&mlon=${s.lng}#map=17/${s.lat}/${s.lng}`} target="_blank" rel="noreferrer">Open map</a>}
            </div>
          ))}
        </div>
      )}

      <div className="card">
        <h3>Blocked ({blocked?.length ?? 0})</h3>
        <div className="mu">Blocked people cannot see your profile, and you cannot see theirs.</div>
        {(blocked || []).map((b) => (
          <div key={b.userId} className="row sp" style={{ marginTop: 10 }}>
            <h3>{b.displayName}</h3>
            <button className="btn g sm" onClick={run(async () => { await api(`/safety/block/${b.userId}`, { method: 'DELETE' }); reloadBlocked(); })}>Unblock</button>
          </div>
        ))}
      </div>
    </div>
  );
}
