import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Avatar, G, goMode, useLoad, useToast } from '../ui.jsx';
import TeaCard from '../components/TeaCard.jsx';
import EventCard from '../components/EventCard.jsx';

export default function Me() {
  const { me, logout } = useAuth();
  const nav = useNavigate();
  const toast = useToast();
  const m = me.card;
  const { data: profile } = useLoad(() => api(`/people/${me.userId}`), []);
  const { data: friends } = useLoad(() => api('/friends'), []);
  const { data: reqs, reload } = useLoad(() => api('/friends/requests'), []);
  const { data: waves } = useLoad(() => api('/waves'), []);
  const sound = localStorage.getItem('mynoano_sound') !== 'off';

  const accept = async (id) => { try { await api(`/friends/${id}/accept`, { method: 'POST' }); reload(); toast('Friend added'); } catch (e) { toast(e.message); } };

  return (
    <>
      <div className="hero" style={{ background: G[m.photoStyle % G.length] }}>
        <div className="top"><span className="pill">My MYNO</span><button className="pill o" onClick={() => goMode(nav, '/ano', sound)}>Go ANO</button></div>
        <div className="ini">{m.displayName[0].toUpperCase()}</div>
        <span className="badge">{m.selfieVerified ? 'Selfie verified' : 'Verified'}</span>
        {m.campusVerified && <span className="badge" style={{ marginTop: 6 }}>Campus verified</span>}
        <h1 style={{ marginTop: 8 }}>{m.displayName}</h1>
        <div style={{ color: '#ddd', marginTop: 6 }}>{m.bio}</div>
      </div>
      <div className="pad">
        <div>{[...m.interests].map((i) => <span key={i} className="chip on">{i}</span>)}</div>

        {reqs?.length > 0 && (
          <>
            <h3 style={{ margin: '18px 0 10px' }}>Friend requests</h3>
            {reqs.map((r) => (
              <div key={r.card.userId} className="card row sp">
                <div className="row"><Avatar name={r.card.displayName} style={r.card.photoStyle} size={38} /><h3>{r.card.displayName}</h3></div>
                <button className="btn sm" onClick={() => accept(r.card.userId)}>Accept</button>
              </div>
            ))}
          </>
        )}
        {waves?.length > 0 && (
          <>
            <h3 style={{ margin: '18px 0 10px' }}>Waves</h3>
            {waves.slice(0, 5).map((w) => (
              <Link key={w.id} to={`/people/${w.from.userId}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                <div className="card row"><Avatar name={w.from.displayName} style={w.from.photoStyle} size={38} /><div><h3>{w.from.displayName}</h3><div className="mu">{w.text}</div></div></div>
              </Link>
            ))}
          </>
        )}

        <h3 style={{ margin: '18px 0 10px' }}>Friends ({friends?.length ?? 0})</h3>
        <div className="row" style={{ overflowX: 'auto', gap: 14, paddingBottom: 4 }}>
          {(friends || []).map((f) => (
            <Link key={f.card.userId} to={`/people/${f.card.userId}`} style={{ textAlign: 'center', flex: 'none', textDecoration: 'none', color: 'inherit' }}>
              <Avatar name={f.card.displayName} style={f.card.photoStyle} size={52} /><div className="mu" style={{ marginTop: 4 }}>{f.card.displayName}</div>
            </Link>
          ))}
        </div>

        <h3 style={{ margin: '18px 0 10px' }}>Shared publicly</h3>
        {(profile?.publicPosts || []).map((t) => <TeaCard key={t.id} t={t} onReact={() => {}} onShare={() => {}} />)}
        {(profile?.publicEvents || []).map((e) => <EventCard key={e.id} e={e} />)}
        {profile && !profile.publicPosts.length && !profile.publicEvents.length && <div className="card mu">Nothing public yet. Post tea or host an event to fill your page.</div>}

        <Link to="/safety" className="btn g" style={{ display: 'block', textAlign: 'center', textDecoration: 'none', marginTop: 12 }}>Safety and privacy</Link>
        <button className="btn g" style={{ marginTop: 12 }} onClick={() => { logout(); nav('/login'); }}>Log out</button>
      </div>
    </>
  );
}
