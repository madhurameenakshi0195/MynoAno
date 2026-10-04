import { Link } from 'react-router-dom';
import { fmt } from '../ui.jsx';
import { G } from '../ui.jsx';

export default function EventCard({ e }) {
  return (
    <Link to={`/events/${e.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ height: 56, background: G[e.id % G.length] }} />
        <div style={{ padding: 14 }}>
          <div className="row sp"><h3>{e.title}</h3>{e.live && <span className="badge">Live now</span>}</div>
          <div className="mu" style={{ margin: '4px 0 10px' }}>
            {e.place}{e.distanceMiles != null && ` · ${e.distanceMiles.toFixed(1)} mi`}<br />
            {e.live ? 'Live now' : 'Starts ' + fmt(e.startsAt)}, ends {fmt(e.endsAt)}
          </div>
          <div className="row sp">
            <span className="mu">{e.going} going · by {e.hostName}</span>
            <span className="or" style={{ fontWeight: 800 }}>{e.joined ? 'Joined' : 'View'}</span>
          </div>
        </div>
      </div>
    </Link>
  );
}
