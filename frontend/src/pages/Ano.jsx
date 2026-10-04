import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { Avatar, goMode, useLoad, useToast } from '../ui.jsx';
import { useAuth } from '../auth.jsx';
import TeaCard from '../components/TeaCard.jsx';
import EventCard from '../components/EventCard.jsx';

export default function Ano() {
  const nav = useNavigate();
  const toast = useToast();
  const { me } = useAuth();
  const { data, reload } = useLoad(() => api('/me/ano'), []);
  const [sound, setSound] = useState(localStorage.getItem('mynoano_sound') !== 'off');
  const toggleSound = () => { localStorage.setItem('mynoano_sound', sound ? 'off' : 'on'); setSound(!sound); };
  const act = (path, method = 'POST', msg) => async () => { try { await api(path, { method }); if (msg) toast(msg); reload(); } catch (e) { toast(e.message); } };

  return (
    <div className="pad" style={{ paddingTop: 30 }}>
      <div className="anobar row sp">
        <div><h3>Your ANO space</h3><div className="mu">Only friends in your circle see this</div></div>
        <div className="row">
          <button className="chip" style={{ margin: 0 }} onClick={toggleSound}>Sound {sound ? 'on' : 'off'}</button>
          <button className="btn sm" onClick={() => goMode(nav, '/me', sound)}>Back to MYNO</button>
        </div>
      </div>
      <div className="row" style={{ marginBottom: 18 }}>
        <Avatar name={me.card.displayName} style={me.card.photoStyle} size={64} />
        <div><h2>{me.card.displayName}</h2><div className="mu">Inner circle</div></div>
      </div>

      <h3 style={{ marginBottom: 10 }}>Friends ({data?.friends.length ?? 0})</h3>
      <div className="card">
        {(data?.friends || []).map((f) => {
          const id = f.card.userId;
          return (
            <div key={id} className="row sp" style={{ padding: '6px 0' }}>
              <div className="row"><Avatar name={f.card.displayName} style={f.card.photoStyle} size={38} /><h3>{f.card.displayName}</h3></div>
              {f.anoStatus === 'ACCEPTED' && <span className="badge">In ANO circle</span>}
              {f.anoStatus === 'REQUESTED' && f.iRequestedAno && <span className="mu">Request sent</span>}
              {f.anoStatus === 'REQUESTED' && !f.iRequestedAno && <button className="btn sm" onClick={act(`/friends/${id}/ano/accept`, 'POST', 'Added to your ANO circle')}>Accept</button>}
              {f.anoStatus === 'NONE' && <button className="btn g sm" onClick={act(`/friends/${id}/ano/request`, 'POST', `ANO request sent to ${f.card.displayName}`)}>Invite to ANO</button>}
            </div>
          );
        })}
        {data && !data.friends.length && <div className="mu">Add friends from Nearby first.</div>}
      </div>

      <h3 style={{ margin: '18px 0 10px' }}>Events you host</h3>
      {(data?.events || []).map((e) => <EventCard key={e.id} e={e} />)}
      {data && !data.events.length && <div className="card mu">No ANO events yet. Create one and choose ANO circle.</div>}

      <h3 style={{ margin: '18px 0 10px' }}>Tea from ANO</h3>
      {(data?.tea || []).map((t) => <TeaCard key={t.id} t={t} onReact={() => {}} onShare={() => {}} />)}
      {data && !data.tea.length && <div className="card mu">Nothing yet. Post tea as ANO from the Tea tab.</div>}
    </div>
  );
}
