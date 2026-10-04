import { Avatar, hoursLeft } from '../ui.jsx';

export default function TeaCard({ t, onReact, onShare, onDelete }) {
  return (
    <div className="card">
      <div className="row sp">
        <div className="row">
          <Avatar name={t.nameless ? '?' : t.authorName} style={t.nameless ? 4 : 0} size={34} />
          <div>
            <h3>{t.authorName}</h3>
            {t.nameless && <div className="mu" style={{ color: 'var(--or2)' }}>Verified person, name hidden</div>}
          </div>
        </div>
        <span className="badge">{t.source === 'ANO' ? 'ANO circle' : 'Public'}</span>
      </div>
      <p style={{ margin: '10px 0' }}>{t.body}</p>
      <div className="row" style={{ marginTop: 6 }}>
        <button className={'chip' + (t.iSpilled ? ' on' : '')} onClick={() => onReact(t.id, 'SPILLED')}>Spilled {t.spilled}</button>
        <button className={'chip' + (t.iSame ? ' on' : '')} onClick={() => onReact(t.id, 'SAME')}>Same {t.same}</button>
        <button className="chip" onClick={() => onShare(t)}>Share</button>
        {t.mine && onDelete && <button className="chip" onClick={() => onDelete(t.id)}>Delete</button>}
        <span className="mu" style={{ marginLeft: 'auto' }}>Gone in {hoursLeft(t.expiresAt)}h</span>
      </div>
    </div>
  );
}
