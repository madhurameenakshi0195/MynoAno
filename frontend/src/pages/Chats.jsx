import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { Avatar, useLoad } from '../ui.jsx';

export default function Chats() {
  const nav = useNavigate();
  const { data: convos } = useLoad(() => api('/chats'), [], 8000);
  const { data: friends } = useLoad(() => api('/friends'), []);
  const talking = new Set((convos || []).map((c) => c.friend.userId));
  const fresh = (friends || []).filter((f) => !talking.has(f.card.userId));
  const preview = (m) => (m.shared || m.sharedType !== 'NONE' ? 'Shared ' + (m.sharedType === 'EVENT' ? 'an event' : 'a tea post') : m.body);

  return (
    <div className="pad">
      <h1>Chats</h1>
      <div className="mu" style={{ marginBottom: 14 }}>Message friends. Share events and tea right here.</div>
      {(convos || []).map((c) => (
        <div key={c.friend.userId} className="card row" style={{ cursor: 'pointer' }} tabIndex={0} onClick={() => nav(`/chats/${c.friend.userId}`)} onKeyDown={(e) => e.key === 'Enter' && nav(`/chats/${c.friend.userId}`)}>
          <Avatar name={c.friend.displayName} style={c.friend.photoStyle} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <h3>{c.friend.displayName}</h3>
            <div className="mu" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{c.last.mine ? 'You: ' : ''}{preview(c.last)}</div>
          </div>
          {c.unread > 0 && <span className="badge">{c.unread}</span>}
        </div>
      ))}
      {fresh.length > 0 && <h3 style={{ margin: '16px 0 10px' }}>Start a chat</h3>}
      {fresh.map((f) => (
        <div key={f.card.userId} className="card row" style={{ cursor: 'pointer' }} onClick={() => nav(`/chats/${f.card.userId}`)}>
          <Avatar name={f.card.displayName} style={f.card.photoStyle} /><h3>{f.card.displayName}</h3>
        </div>
      ))}
      {convos && friends && !convos.length && !fresh.length && <div className="card mu">Add friends from Nearby to start chatting.</div>}
    </div>
  );
}
