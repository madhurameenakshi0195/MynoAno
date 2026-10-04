import { useState } from 'react';
import { api } from '../api.js';
import { Seg, useLoad, useToast } from '../ui.jsx';
import TeaCard from '../components/TeaCard.jsx';
import ShareSheet from '../components/ShareSheet.jsx';

export default function Tea() {
  const toast = useToast();
  const [filter, setFilter] = useState('all');
  const [body, setBody] = useState('');
  const [source, setSource] = useState('MYNO');
  const [nameless, setNameless] = useState(false);
  const [share, setShare] = useState(null);
  const { data, reload } = useLoad(() => api(`/tea?filter=${filter}`), [filter], 20000);

  const post = async () => {
    if (!body.trim()) return toast('Write something first');
    try {
      await api('/tea', { method: 'POST', body: { body, source, nameless } });
      setBody('');
      toast(source === 'ANO' ? 'Posted to your ANO circle. Gone in 24h' : 'Posted publicly. Gone in 24h');
      reload();
    } catch (e) { toast(e.message); }
  };
  const react = async (id, type) => { try { await api(`/tea/${id}/react`, { method: 'POST', body: { type } }); reload(); } catch (e) { toast(e.message); } };
  const remove = async (id) => { await api(`/tea/${id}`, { method: 'DELETE' }); reload(); };

  return (
    <div className="pad">
      <h1>Tea</h1>
      <div className="mu" style={{ marginBottom: 14 }}>MYNO tea is public. ANO tea reaches only your circle. Every post disappears after 24 hours.</div>
      <div className="card">
        <textarea className="in" style={{ marginTop: 0 }} placeholder="Spill something..." value={body} onChange={(e) => setBody(e.target.value)} maxLength={1000} />
        <Seg options={[['MYNO', 'Post as MYNO'], ['ANO', 'Post as ANO']]} value={source} onChange={setSource} />
        <label className="mu row" style={{ margin: '10px 0' }}><input type="checkbox" checked={nameless} onChange={(e) => setNameless(e.target.checked)} /> Post nameless (still verified)</label>
        <button className="btn" onClick={post}>Post tea</button>
      </div>
      <Seg options={[['all', 'All'], ['MYNO', 'MYNO'], ['ANO', 'ANO']]} value={filter} onChange={setFilter} />
      {(data || []).map((t) => <TeaCard key={t.id} t={t} onReact={react} onShare={setShare} onDelete={remove} />)}
      {share && <ShareSheet type="TEA" id={share.id} isAno={share.source === 'ANO'} onClose={() => setShare(null)} />}
    </div>
  );
}
