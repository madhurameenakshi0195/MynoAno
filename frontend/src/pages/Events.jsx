import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { Seg, Chip, useLoad } from '../ui.jsx';
import EventCard from '../components/EventCard.jsx';

const RADII = [1, 5, 10, 25];
const KEY = 'mynoano_event_loc';

export default function Events() {
  const nav = useNavigate();
  const [where, setWhere] = useState('me');
  const [pos, setPos] = useState(() => { try { return JSON.parse(localStorage.getItem(KEY)); } catch { return null; } });
  const [custom, setCustom] = useState({ lat: '', lng: '' });
  const [radius, setRadius] = useState(10);
  const [view, setView] = useState('list');

  useEffect(() => {
    if (where !== 'me' || !navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition((p) => {
      const v = { lat: p.coords.latitude, lng: p.coords.longitude };
      localStorage.setItem(KEY, JSON.stringify(v));
      setPos(v);
    });
  }, [where]);

  const center = where === 'custom' && custom.lat !== '' && custom.lng !== '' ? { lat: +custom.lat, lng: +custom.lng } : pos;
  const { data: groups } = useLoad(
    () => (center ? api(`/events?lat=${center.lat}&lng=${center.lng}&radius=${radius}`) : Promise.resolve([])),
    [center?.lat, center?.lng, radius], 30000);

  return (
    <div className="pad">
      <div className="row sp">
        <div><h1>Events</h1><div className="mu">Closest first, grouped by venue</div></div>
        <Link to="/events/new" className="btn sm" style={{ textDecoration: 'none', textAlign: 'center' }}>Create</Link>
      </div>
      <p className="mu" style={{ margin: '14px 0 6px' }}>Around</p>
      <select className="in" style={{ marginTop: 0 }} aria-label="Choose location" value={where} onChange={(e) => setWhere(e.target.value)}>
        <option value="me">Near me</option><option value="custom">A place I choose (lat, lng)</option>
      </select>
      {where === 'custom' && (
        <div className="row">
          <input className="in" placeholder="Latitude" value={custom.lat} onChange={(e) => setCustom({ ...custom, lat: e.target.value })} />
          <input className="in" placeholder="Longitude" value={custom.lng} onChange={(e) => setCustom({ ...custom, lng: e.target.value })} />
        </div>
      )}
      <div style={{ margin: '4px 0 12px' }}>{RADII.map((r) => <Chip key={r} on={radius === r} onClick={() => setRadius(r)}>{r} mile{r > 1 ? 's' : ''}</Chip>)}</div>
      <Seg options={[['list', 'List'], ['map', 'Busy map']]} value={view} onChange={setView} />
      {!center && <div className="card mu">Allow location, or choose a place, to see events around it.</div>}
      {view === 'map' && center ? <BusyMap groups={groups || []} center={center} radius={radius} onOpen={(g) => nav(`/events/${g.events[0].id}`)} /> : (groups || []).map((g) => (
        <div key={g.venueGroup}>
          <div className="row sp" style={{ margin: '6px 0 10px' }}>
            <h3>{g.venueGroup}</h3><span className="badge">{g.venueType}, {g.events.length} event{g.events.length > 1 ? 's' : ''}</span>
          </div>
          {g.events.map((e) => <EventCard key={e.id} e={e} />)}
        </div>
      ))}
      {center && groups && !groups.length && (
        <div className="card" style={{ textAlign: 'center' }}><h3>No events within {radius} mile{radius > 1 ? 's' : ''}</h3><p className="mu">Widen the distance or create one.</p></div>
      )}
    </div>
  );
}

function BusyMap({ groups, center, radius, onOpen }) {
  const sc = 125 / radius;
  const cosLat = Math.cos((center.lat * Math.PI) / 180);
  const top = [...groups].sort((a, b) => b.totalGoing - a.totalGoing).slice(0, 3);
  return (
    <>
      <div className="card" style={{ padding: 8 }}>
        <svg viewBox="0 0 300 300" style={{ width: '100%', display: 'block' }}>
          <defs><radialGradient id="hg"><stop offset="0" stopColor="#ff6a00" stopOpacity=".75" /><stop offset="1" stopColor="#ff6a00" stopOpacity="0" /></radialGradient></defs>
          {[1, 0.66, 0.33].map((f) => <circle key={f} cx="150" cy="150" r={125 * f} fill="none" stroke="#2a2a2a" strokeDasharray="3 5" />)}
          <text x="150" y="21" textAnchor="middle" fontSize="9" fill="#9b948a">{radius} mi</text>
          <circle cx="150" cy="150" r="5" fill="#fff" />
          {groups.map((g) => {
            const x = 150 + (g.lng - center.lng) * 69 * cosLat * sc;
            const y = 150 - (g.lat - center.lat) * 69 * sc;
            const r = 8 + Math.min(14, g.totalGoing / 6);
            const live = g.events.some((e) => e.live);
            const name = g.venueGroup.length > 16 ? g.venueGroup.slice(0, 15) + '...' : g.venueGroup;
            return (
              <g key={g.venueGroup} tabIndex={0} role="button" aria-label={`${g.venueGroup}, ${g.events.length} events`} style={{ cursor: 'pointer' }}
                 onClick={() => onOpen(g)} onKeyDown={(e) => e.key === 'Enter' && onOpen(g)}>
                <circle className={live ? 'pulse2' : ''} cx={x} cy={y} r={r * 2.4} fill="url(#hg)" />
                <circle cx={x} cy={y} r={r} fill="#ff6a00" stroke="#fff" strokeWidth={live ? 2 : 0} />
                <text x={x} y={y + 4} textAnchor="middle" fontSize="11" fontWeight="800" fill="#000">{g.events.length}</text>
                <text x={x} y={y + r + 13} textAnchor="middle" fontSize="9.5" fontWeight="700" fill="#f6f2ec">{name}</text>
              </g>
            );
          })}
        </svg>
      </div>
      <h3 style={{ margin: '6px 0 10px' }}>Busiest right now</h3>
      {top.map((g) => (
        <div key={g.venueGroup} className="card row sp" style={{ padding: 12, cursor: 'pointer' }} onClick={() => onOpen(g)}>
          <div><h3>{g.venueGroup}</h3><div className="mu">{g.events.length} event{g.events.length > 1 ? 's' : ''}, {g.totalGoing} going</div></div>
          <span className="badge">{g.venueType}</span>
        </div>
      ))}
    </>
  );
}
