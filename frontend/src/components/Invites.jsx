import { useState } from 'react';
import { api } from '../api.js';
import { Avatar, minsLeft, useLoad, useToast } from '../ui.jsx';

export default function Invites({ discoveryOn }) {
  const toast = useToast();
  const { data, reload } = useLoad(() => api('/invites'), [], 10000);
  const [text, setText] = useState('');
  const [minutes, setMinutes] = useState(30);

  const post = async () => {
    if (!text.trim()) return toast('Write what you are up for');
    if (!discoveryOn) return toast('Turn on Nearby Discovery to post');
    try { await api('/invites', { method: 'POST', body: { text, minutes: +minutes } }); setText(''); toast('Invite posted. It ends on its own.'); reload(); }
    catch (e) { toast(e.message); }
  };
  const join = async (id) => { try { await api(`/invites/${id}/join`, { method: 'POST' }); reload(); } catch (e) { toast(e.message); } };

  return (
    <>
      <div className="card">
        <h3>Start an open invite</h3>
        <p className="mu" style={{ margin: '4px 0 8px' }}>Anyone nearby can join. It disappears on its own.</p>
        <input className="in" style={{ marginTop: 0 }} placeholder="Chai in 30 minutes?" value={text} onChange={(e) => setText(e.target.value)} maxLength={140} />
        <select className="in" aria-label="Invite length" value={minutes} onChange={(e) => setMinutes(e.target.value)}>
          <option value="30">Lasts 30 minutes</option><option value="60">Lasts 1 hour</option><option value="120">Lasts 2 hours</option>
        </select>
        <button className="btn" onClick={post}>Post invite</button>
      </div>
      <h3 style={{ margin: '14px 0 10px' }}>{data?.length ?? 0} open right now</h3>
      {(data || []).map((v) => (
        <div className="card" key={v.id}>
          <div className="row">
            <Avatar name={v.authorName} size={38} />
            <div style={{ flex: 1 }}><h3>{v.mine ? 'You' : v.authorName}</h3><div className="mu">Ends in {minsLeft(v.expiresAt)} min</div></div>
            <span className="badge">{v.going} in</span>
          </div>
          <p style={{ margin: '10px 0' }}>{v.text}</p>
          {!v.mine && <button className={'btn sm' + (v.joined ? ' g' : '')} onClick={() => join(v.id)}>{v.joined ? 'You are in' : 'I am in'}</button>}
        </div>
      ))}
      {data && !data.length && <div className="card mu">No open invites nearby. Be the first.</div>}
    </>
  );
}
