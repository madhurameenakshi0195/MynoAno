import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { G, fmt, useLoad, useToast } from '../ui.jsx';
import ShareSheet from '../components/ShareSheet.jsx';

export default function EventDetail() {
  const { id } = useParams();
  const nav = useNavigate();
  const toast = useToast();
  const { me } = useAuth();
  const { data: d, error, reload } = useLoad(() => api(`/events/${id}`), [id], 20000);
  const joined = d?.card.joined;
  const { data: chat, reload: reloadChat } = useLoad(() => (joined ? api(`/events/${id}/chat`) : Promise.resolve([])), [id, joined], 5000);
  const [msg, setMsg] = useState('');
  const [share, setShare] = useState(false);

  if (error) return <div className="pad"><div className="card">{error}</div><button className="btn g" onClick={() => nav(-1)}>Back</button></div>;
  if (!d) return null;
  const c = d.card;
  const mine = c.hostId === me.userId;
  const act = (fn) => async () => { try { await fn(); reload(); reloadChat(); } catch (e) { toast(e.message); } };
  const call = (path, method = 'POST') => api(`/events/${id}${path}`, { method });

  const send = async () => {
    if (!msg.trim()) return;
    try { await api(`/events/${id}/chat`, { method: 'POST', body: { body: msg } }); setMsg(''); reloadChat(); } catch (e) { toast(e.message); }
  };
  const addPhotos = async (files) => {
    for (const file of [...files].slice(0, 6)) {
      const form = new FormData();
      form.append('file', file);
      try { await api(`/events/${id}/photos`, { method: 'POST', form }); } catch (e) { toast(e.message); }
    }
    reload();
  };

  return (
    <div className="pad">
      <button className="pill" onClick={() => nav(-1)}>Back</button>
      <div style={{ height: 90, borderRadius: 16, background: G[c.id % G.length], margin: '12px 0' }} />
      <h2>{c.title}</h2>
      <div className="mu">{c.place} · {c.venueGroup}<br />{c.live ? 'Live now' : 'Starts ' + fmt(c.startsAt)}, ends {fmt(c.endsAt)} · hosted by {mine ? 'you' : c.hostName}</div>
      <div className="row" style={{ margin: '14px 0' }}>
        {[[c.going, 'going'], [d.hereNow, 'here now'], [c.visibility === 'ANO' ? 'ANO' : 'Public', 'visibility']].map(([v, l]) => (
          <div key={l} className="card" style={{ flex: 1, margin: 0, textAlign: 'center' }}><h2 className="or">{v}</h2><div className="mu">{l}</div></div>
        ))}
      </div>
      <button className="btn g" style={{ marginBottom: 12 }} onClick={() => setShare(true)}>Share with friends</button>

      <h3 style={{ margin: '10px 0' }}>From past editions</h3>
      {d.photos.length ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 6, marginBottom: 10 }}>
          {d.photos.map((p) => <img key={p.id} src={p.url} alt={p.caption || 'Past event photo'} style={{ width: '100%', aspectRatio: '1', objectFit: 'cover', borderRadius: 12 }} />)}
        </div>
      ) : <p className="mu">The host has not added photos yet.</p>}
      {mine && (
        <label className="btn g" style={{ display: 'block', textAlign: 'center', marginBottom: 10 }}>
          Add photos from earlier events
          <input type="file" accept="image/*" multiple hidden onChange={(e) => addPhotos(e.target.files)} />
        </label>
      )}

      {d.description && <p>{d.description}</p>}
      <h3 style={{ margin: '10px 0' }}>What is inside</h3>
      {d.highlights.map((h) => <div key={h} className="card" style={{ padding: '10px 14px', marginBottom: 8 }}>{h}</div>)}

      {joined ? (
        <>
          <button className={'btn' + (d.checkedIn ? ' g' : '')} style={{ margin: '6px 0 12px' }} onClick={act(() => call('/checkin', d.checkedIn ? 'DELETE' : 'POST'))}>
            {d.checkedIn ? 'Checked in. Tap to check out' : 'Check in'}
          </button>
          <div className="card">
            <div className="mu" style={{ marginBottom: 6 }}>{d.chatOpen ? 'Event chat. It closes when the event ends.' : 'Chat closed. This event has ended.'}</div>
            {(chat || []).map((m) => <p key={m.id} style={{ margin: '6px 0' }}><b>{m.senderId === me.userId ? 'You' : m.senderName}:</b> {m.body}</p>)}
            {d.chatOpen && (
              <div className="row">
                <input className="in" style={{ margin: 0 }} placeholder="Message the group" value={msg} onChange={(e) => setMsg(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && send()} />
                <button className="btn sm" onClick={send}>Send</button>
              </div>
            )}
            {mine && d.chatOpen && <button className="btn g sm" style={{ marginTop: 10 }} onClick={act(() => call('/end'))}>End event and close chat</button>}
          </div>
        </>
      ) : <p className="mu">Join the group to check in and use the chat.</p>}
      {!mine && <button className={'btn' + (joined ? ' g' : '')} style={{ marginTop: 10 }} onClick={act(() => call('/join', joined ? 'DELETE' : 'POST'))}>{joined ? 'Leave event group' : 'Join event group'}</button>}
      {share && <ShareSheet type="EVENT" id={c.id} isAno={c.visibility === 'ANO'} onClose={() => setShare(false)} />}
    </div>
  );
}
