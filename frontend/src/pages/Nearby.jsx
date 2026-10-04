import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Chip, Seg, Sheet, Toggle, Avatar, useLoad, useToast } from '../ui.jsx';
import Radar from '../components/Radar.jsx';
import PersonRow from '../components/PersonRow.jsx';
import Invites from '../components/Invites.jsx';

const INTENTS = ['Chai', 'Study', 'Walk', 'Games', 'Food', 'Music'];
const GHOST = [['ALWAYS', 'Always on', null], ['TIMED60', '1 hour', 60], ['TIMED120', '2 hours', 120], ['EVENTS_ONLY', 'Events only', null]];

export default function Nearby() {
  const { me, refresh } = useAuth();
  const nav = useNavigate();
  const toast = useToast();
  const on = me.discoveryOn;
  const [view, setView] = useState('people');
  const [scope, setScope] = useState('campus');
  const [qa, setQa] = useState(null);
  const [ghost, setGhost] = useState(me.ghostMode === 'EVENTS_ONLY' ? 'EVENTS_ONLY' : me.ghostMode === 'TIMED' ? 'TIMED60' : 'ALWAYS');
  const { data, reload } = useLoad(() => (on ? api('/nearby') : Promise.resolve([])), [on], 15000);

  // Share my position while discovery is on. The server snaps it to a grid and never returns it to anyone.
  useEffect(() => {
    if (!on || !navigator.geolocation) return undefined;
    const send = (p) => api('/me/location', { method: 'PUT', body: { lat: p.coords.latitude, lng: p.coords.longitude } }).catch(() => {});
    const id = navigator.geolocation.watchPosition(send, () => toast('Location permission is needed for Nearby'), { maximumAge: 30000 });
    return () => navigator.geolocation.clearWatch(id);
  }, [on, toast]);

  const setDiscovery = async (value, key = ghost) => {
    const g = GHOST.find((x) => x[0] === key);
    const mode = key === 'EVENTS_ONLY' ? 'EVENTS_ONLY' : g[2] ? 'TIMED' : 'ALWAYS';
    try { await api('/me/discovery', { method: 'PUT', body: { on: value, ghostMode: mode, offAfterMinutes: g[2] } }); setGhost(key); await refresh(); reload(); }
    catch (e) { toast(e.message); }
  };
  const setIntent = async (k) => { await api('/me/intent', { method: 'PUT', body: { intent: me.card.intent === k ? null : k } }); await refresh(); reload(); };
  const block = async (p) => { await api(`/safety/block/${p.userId}`, { method: 'POST' }); setQa(null); toast(`${p.displayName} blocked. They can no longer see you.`); reload(); };
  const report = async (p) => { await api(`/safety/report/${p.userId}`, { method: 'POST', body: { reason: 'Reported from Nearby' } }); setQa(null); toast('Reported. We will review this profile.'); };

  const campusOnly = me.card.campus && scope === 'campus';
  const people = (data || []).filter((p) => !campusOnly || p.campus === me.card.campus);
  const visible = on && ghost !== 'EVENTS_ONLY';

  return (
    <div className="pad">
      <div className="row sp">
        <div>
          <h1>Nearby</h1>
          <div className="mu">{!on ? 'Discovery is off. You are hidden.' : ghost === 'EVENTS_ONLY' ? 'Visible only at events you join' : 'Visible to people close by'}</div>
        </div>
        <Toggle on={on} label="Nearby Discovery" onChange={(v) => setDiscovery(v)} />
      </div>
      <Seg options={[['people', 'People'], ['inv', 'Open invites']]} value={view} onChange={setView} />

      {view === 'inv' ? <Invites discoveryOn={on} /> : (
        <>
          <Radar people={people} on={visible} onPick={setQa} />
          <p className="mu" style={{ margin: '0 0 6px' }}>I am up for right now</p>
          <div style={{ marginBottom: 10 }}>{INTENTS.map((k) => <Chip key={k} on={me.card.intent === k} onClick={() => setIntent(k)}>{k}</Chip>)}</div>
          {me.card.campus ? (
            <Seg options={[['campus', 'My campus first'], ['wide', 'Wider area']]} value={scope} onChange={setScope} />
          ) : <div className="card mu" style={{ fontSize: 13 }}>Campus mode unlocks with a college email (.edu or .ac.in).</div>}
          {on && (
            <>
              <p className="mu" style={{ margin: '0 0 6px' }}>Auto-off timer</p>
              <div style={{ marginBottom: 10 }}>{GHOST.map(([k, label]) => <Chip key={k} on={ghost === k} onClick={() => setDiscovery(true, k)}>{label}</Chip>)}</div>
            </>
          )}
          {visible ? (
            <>
              <h3 style={{ margin: '6px 0 10px' }}>{people.length} people near you</h3>
              {people.map((p) => (
                <PersonRow key={p.userId} p={p} onClick={() => nav(`/people/${p.userId}`)}
                           right={<button className="pill" aria-label={`Quick actions for ${p.displayName}`} onClick={(e) => { e.stopPropagation(); setQa(p); }}>Nearby</button>} />
              ))}
              {!people.length && <div className="card mu">No one nearby right now.</div>}
            </>
          ) : on ? (
            <div className="card" style={{ textAlign: 'center' }}><h3>You are hidden from the radar</h3><p className="mu">People only see you at events you have joined.</p></div>
          ) : (
            <div className="card" style={{ textAlign: 'center' }}>
              <h3>Turn on Nearby Discovery</h3>
              <p className="mu">You will see people close by, and they will see your MYNO profile. Never your ANO.</p>
              <button className="btn sm" onClick={() => setDiscovery(true)}>Turn on</button>
            </div>
          )}
        </>
      )}

      {qa && (
        <Sheet onClose={() => setQa(null)}>
          <div className="row" style={{ marginBottom: 14 }}>
            <Avatar name={qa.displayName} style={qa.photoStyle} size={52} />
            <div><h2>{qa.displayName}</h2><div className="mu">Blocking hides your profile from them too</div></div>
          </div>
          <button className="btn" onClick={() => nav(`/people/${qa.userId}`)}>View profile</button>
          <div className="row" style={{ marginTop: 10 }}>
            <button className="btn g" onClick={() => block(qa)}>Block</button>
            <button className="btn g" onClick={() => report(qa)}>Report</button>
          </div>
        </Sheet>
      )}
    </div>
  );
}
