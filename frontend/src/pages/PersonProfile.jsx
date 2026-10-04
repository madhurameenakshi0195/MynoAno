import { Navigate, useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { G, useLoad, useToast } from '../ui.jsx';
import TeaCard from '../components/TeaCard.jsx';
import EventCard from '../components/EventCard.jsx';

export default function PersonProfile() {
  const { id } = useParams();
  const nav = useNavigate();
  const toast = useToast();
  const { me } = useAuth();
  const { data, error, reload } = useLoad(() => api(`/people/${id}`), [id]);
  if (+id === me.userId) return <Navigate to="/me" replace />;
  if (error) return <div className="pad"><div className="card">{error}</div><button className="btn g" onClick={() => nav(-1)}>Back</button></div>;
  if (!data) return null;
  const p = data.card;
  const run = (fn, msg) => async () => { try { await fn(); if (msg) toast(msg); reload(); } catch (e) { toast(e.message); } };
  const post = (path, body) => api(path, { method: 'POST', body });
  const openers = ['Wave', p.interests[0] ? `Ask about ${p.interests[0]}` : 'Say hi', p.commonInterests[0] ? `You both like ${p.commonInterests[0]}` : 'Hi there'];

  return (
    <>
      <div className="hero" style={{ background: G[p.photoStyle % G.length] }}>
        <div className="top"><button className="pill" onClick={() => nav(-1)}>Back</button>
          <button className="pill" onClick={run(() => post(`/safety/report/${id}`, { reason: 'Reported from profile' }), 'Reported. We will review this profile.')}>Report</button></div>
        <div className="ini">{p.displayName[0].toUpperCase()}</div>
        <span className="badge">{p.selfieVerified ? 'Selfie verified' : 'Verified'}</span>
        {p.campusVerified && <span className="badge" style={{ marginTop: 6 }}>Campus verified</span>}
        <h1 style={{ marginTop: 8 }}>{p.displayName}</h1>
        <div style={{ color: '#ddd' }}>Nearby</div>
      </div>
      <div className="pad">
        <p style={{ marginTop: 0 }}>{p.bio}</p>
        {p.commonInterests.length > 0 && <span className="chip on">{p.commonInterests.length} in common: {p.commonInterests.join(', ')}</span>}
        {p.mutualFriends > 0 && <span className="chip on">{p.mutualFriends} mutual friend{p.mutualFriends > 1 ? 's' : ''}</span>}
        {p.intent && <span className="chip on">Up for {p.intent}</span>}
        <div>{[...p.interests].map((i) => <span key={i} className="chip">{i}</span>)}</div>

        {p.friendStatus === 'NONE' && (
          <div className="card" style={{ marginTop: 10 }}>
            <h3>Break the ice</h3>
            <p className="mu" style={{ margin: '4px 0 10px' }}>Send a quick opener before you add them.</p>
            {openers.map((o) => <button key={o} type="button" className="chip" onClick={run(() => post(`/people/${id}/wave`, { text: o }), `Sent to ${p.displayName}`)}>{o}</button>)}
          </div>
        )}
        <div className="row" style={{ margin: '16px 0' }}>
          {p.friendStatus === 'NONE' && <button className="btn" onClick={run(() => post(`/friends/${id}/request`), 'Friend request sent')}>Add friend</button>}
          {p.friendStatus === 'SENT' && <button className="btn" disabled style={{ opacity: 0.6 }}>Request sent</button>}
          {p.friendStatus === 'RECEIVED' && <button className="btn" onClick={run(() => post(`/friends/${id}/accept`), 'Friend added')}>Accept request</button>}
          {p.friendStatus === 'FRIEND' && (
            <>
              <button className="btn" onClick={() => nav(`/chats/${id}`)}>Chat</button>
              <button className="btn g" onClick={run(() => post(`/friends/${id}/ano/request`), `ANO request sent to ${p.displayName}`)}>Send ANO request</button>
            </>
          )}
        </div>
        {p.friendStatus === 'FRIEND' && <button className="btn g" onClick={() => nav('/safety')}>Meet safely</button>}

        <h3 style={{ margin: '22px 0 10px' }}>Shared publicly</h3>
        {data.publicPosts.map((t) => <TeaCard key={t.id} t={t} onReact={() => {}} onShare={() => {}} />)}
        {data.publicEvents.map((e) => <EventCard key={e.id} e={e} />)}
        {!data.publicPosts.length && !data.publicEvents.length && <div className="card mu">Nothing shared publicly right now.</div>}
        <button className="btn g sm" style={{ marginTop: 10 }} onClick={run(async () => { await post(`/safety/block/${id}`); nav(-1); }, `${p.displayName} blocked. They can no longer see you.`)}>Block {p.displayName}</button>
      </div>
    </>
  );
}
