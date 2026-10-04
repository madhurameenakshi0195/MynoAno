import { Avatar } from '../ui.jsx';

export default function PersonRow({ p, onClick, right }) {
  const tags = [
    p.commonInterests?.length ? `${p.commonInterests.length} in common: ${p.commonInterests.join(', ')}` : '',
    p.mutualFriends ? `${p.mutualFriends} mutual friend${p.mutualFriends > 1 ? 's' : ''}` : '',
  ].filter(Boolean);
  const small = { fontSize: 11, padding: '3px 8px', margin: '6px 4px 0 0' };
  return (
    <div className="card row" onClick={onClick} style={{ cursor: onClick ? 'pointer' : 'default' }}>
      <Avatar name={p.displayName} style={p.photoStyle} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <h3>{p.displayName}</h3>
        <div className="mu">{p.bio}</div>
        {p.intent && <span className={'chip' + (p.intentMatch ? ' on' : '')} style={small}>{p.intentMatch ? 'Same as you: ' : 'Up for '}{p.intent}</span>}
        {tags.map((t) => <span key={t} className="chip" style={small}>{t}</span>)}
      </div>
      {right}
    </div>
  );
}
