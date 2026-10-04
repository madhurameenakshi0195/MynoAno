import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { Chip, G } from '../ui.jsx';

const INTERESTS = ['Music', 'Cricket', 'Coffee', 'Trekking', 'Art', 'Coding', 'Books', 'Anime', 'Startups', 'Gaming', 'Photography', 'Food'];

export default function CreateProfile() {
  const nav = useNavigate();
  const { refresh } = useAuth();
  const [name, setName] = useState('');
  const [bio, setBio] = useState('');
  const [interests, setInterests] = useState([]);
  const [style, setStyle] = useState(0);
  const [err, setErr] = useState('');

  const toggle = (i) => setInterests(interests.includes(i) ? interests.filter((x) => x !== i) : [...interests, i]);
  const submit = async (e) => {
    e.preventDefault();
    try {
      await api('/me/profile', { method: 'PUT', body: { displayName: name, bio, interests, photoStyle: style } });
      await refresh();
      nav('/nearby');
    } catch (x) { setErr(x.message); }
  };

  return (
    <form className="pad" style={{ paddingTop: 48 }} onSubmit={submit}>
      <h2>Build your MYNO profile</h2>
      <p className="mu">This is what nearby people see. Share only what you want.</p>
      <input className="in" placeholder="Display name" value={name} onChange={(e) => setName(e.target.value)} maxLength={40} />
      <textarea className="in" placeholder="Short bio (public)" value={bio} onChange={(e) => setBio(e.target.value)} maxLength={500} />
      <p className="mu" style={{ margin: '8px 0' }}>Pick interests</p>
      <div>{INTERESTS.map((i) => <Chip key={i} on={interests.includes(i)} onClick={() => toggle(i)}>{i}</Chip>)}</div>
      <p className="mu" style={{ margin: '8px 0' }}>Profile photo style (full-screen on your page)</p>
      <div className="row">
        {G.map((g, i) => (
          <button type="button" key={i} aria-label={`Photo style ${i + 1}`} className="av" onClick={() => setStyle(i)}
                  style={{ background: g, width: 50, height: 50, border: `3px solid ${style === i ? '#fff' : 'transparent'}` }} />
        ))}
      </div>
      <div className="err">{err}</div>
      <button className="btn">Enter MynoAno</button>
    </form>
  );
}
