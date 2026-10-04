import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';

export default function Register() {
  const nav = useNavigate();
  const [f, setF] = useState({ email: '', phone: '', password: '' });
  const [err, setErr] = useState('');
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    try {
      await api('/auth/register', { method: 'POST', body: f });
      nav('/verify', { state: { email: f.email } });
    } catch (x) { setErr(x.message); }
  };

  return (
    <form className="pad" style={{ paddingTop: 48 }} onSubmit={submit}>
      <div className="logo">Myno<span className="or">Ano</span></div>
      <h2 style={{ marginTop: 24 }}>Create account</h2>
      <p className="mu">Every profile is verified. One account per email and phone number. Use a college email (.edu or .ac.in) to unlock campus mode.</p>
      <input className="in" type="email" placeholder="Email" value={f.email} onChange={set('email')} />
      <input className="in" type="tel" placeholder="Phone number" value={f.phone} onChange={set('phone')} />
      <input className="in" type="password" placeholder="Password (8+ characters)" autoComplete="new-password" value={f.password} onChange={set('password')} />
      <div className="err">{err}</div>
      <button className="btn">Send verification code</button>
      <p className="mu" style={{ textAlign: 'center' }}><Link to="/login">Back to log in</Link></p>
    </form>
  );
}
