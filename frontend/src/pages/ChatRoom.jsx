import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { Avatar, fmt, useLoad, useToast } from '../ui.jsx';

function Bubble({ m }) {
  const base = { maxWidth: '80%', padding: '10px 12px', borderRadius: 16, margin: '6px 0' };
  const style = m.mine ? { ...base, marginLeft: 'auto', background: 'var(--or)', color: '#000' } : { ...base, background: 'var(--s2)' };
  if (m.sharedType === 'EVENT') {
    return (
      <div style={style}>
        {m.shared ? (<><div style={{ fontWeight: 800 }}>{m.shared.title}</div><div style={{ fontSize: 13, margin: '4px 0 8px' }}>{m.shared.venueGroup}, ends {fmt(m.shared.endsAt)}</div>
          <Link className="btn sm" style={{ background: '#000', color: 'var(--or2)', textDecoration: 'none' }} to={`/events/${m.shared.id}`}>Open event</Link></>) : 'This shared event is no longer available'}
      </div>
    );
  }
  if (m.sharedType === 'TEA') {
    return <div style={style}><div style={{ fontWeight: 800 }}>Tea</div><div style={{ fontSize: 13 }}>{m.shared ? m.shared.body : 'This post has expired'}</div></div>;
  }
  return <div style={style}>{m.body}</div>;
}

export default function ChatRoom() {
  const { id } = useParams();
  const nav = useNavigate();
  const toast = useToast();
  const { data: person } = useLoad(() => api(`/people/${id}`), [id]);
  const { data: msgs, reload } = useLoad(() => api(`/chats/${id}`), [id], 4000);
  const [text, setText] = useState('');
  const end = useRef(null);
  useEffect(() => { end.current?.scrollIntoView(); }, [msgs?.length]);

  const send = async () => {
    if (!text.trim()) return;
    try { await api(`/chats/${id}`, { method: 'POST', body: { body: text } }); setText(''); reload(); } catch (e) { toast(e.message); }
  };
  const c = person?.card;

  return (
    <div className="pad" style={{ display: 'flex', flexDirection: 'column', minHeight: '100%' }}>
      <div className="row" style={{ marginBottom: 10 }}>
        <button className="pill" onClick={() => nav('/chats')}>Back</button>
        {c && <Avatar name={c.displayName} style={c.photoStyle} size={36} />}<h3>{c?.displayName}</h3>
      </div>
      <div style={{ flex: 1 }}>
        {(msgs || []).map((m) => <Bubble key={m.id} m={m} />)}
        {msgs && !msgs.length && <p className="mu" style={{ textAlign: 'center' }}>No messages yet.</p>}
        <div ref={end} />
      </div>
      <div className="row" style={{ position: 'sticky', bottom: 0, paddingTop: 10, background: '#000' }}>
        <input className="in" style={{ margin: 0 }} placeholder="Message" value={text} onChange={(e) => setText(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && send()} />
        <button className="btn sm" onClick={send}>Send</button>
      </div>
    </div>
  );
}
