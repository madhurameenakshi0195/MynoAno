import { useState } from 'react';
import { api } from '../api.js';
import { Chip, Sheet, useLoad, useToast } from '../ui.jsx';

/** Share an event or a tea post with friends. ANO items are limited to the ANO circle (the server enforces it too). */
export default function ShareSheet({ type, id, isAno, onClose }) {
  const toast = useToast();
  const { data: friends } = useLoad(() => api('/friends'), []);
  const [sel, setSel] = useState({});
  const list = (friends || []).filter((f) => !isAno || f.anoStatus === 'ACCEPTED');

  const send = async () => {
    const ids = Object.keys(sel).filter((k) => sel[k]);
    if (!ids.length) return toast('Pick at least one friend');
    try {
      await Promise.all(ids.map((uid) => api(`/chats/${uid}`, { method: 'POST', body: { sharedType: type, sharedId: id } })));
      toast(`Shared with ${ids.length} friend${ids.length > 1 ? 's' : ''}`);
      onClose();
    } catch (e) { toast(e.message); }
  };

  return (
    <Sheet onClose={onClose}>
      <h2>Share with friends</h2>
      <p className="mu">{isAno ? 'This is an ANO item, so only your ANO circle can receive it.' : 'Pick friends to send this to.'}</p>
      <div>
        {list.map((f) => (
          <Chip key={f.card.userId} on={sel[f.card.userId]} onClick={() => setSel({ ...sel, [f.card.userId]: !sel[f.card.userId] })}>{f.card.displayName}</Chip>
        ))}
        {friends && !list.length && <p className="mu">{isAno ? 'No one in your ANO circle yet.' : 'Add some friends first.'}</p>}
      </div>
      <button className="btn" style={{ marginTop: 12 }} onClick={send}>Send</button>
    </Sheet>
  );
}
