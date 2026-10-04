import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';

export default function Login() {
  const nav = useNavigate();
  const { login } = useAuth();
  const [f, setF] = useState({ email: '', password: '' });
  const [err, setErr] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    try {
      const r = await api('/auth/login', { method: 'POST', body: f });
      await login(r.token);
      nav(r.profileComplete ? '/nearby' : '/create-profile');
    } catch (x) { setErr(x.message); }
  };

  return (
    <form className="pad" style={{ paddingTop: 48 }} onSubmit={submit}>
      <div className="logo">Myno<span className="or">Ano</span></div>
      <p className="mu" style={{ margin: '10px 0 26px' }}>See who and what is around you. Show only what you choose.</p>
      <h2>Log in</h2>
      <input className="in" type="email" placeholder="Email" autoComplete="email" value={f.email} onChange={(e) => setF({ ...f, email: e.target.value })} />
      <input className="in" type="password" placeholder="Password" autoComplete="current-password" value={f.password} onChange={(e) => setF({ ...f, password: e.target.value })} />
      <div className="err">{err}</div>
      <button className="btn">Log in</button>
      <p className="mu" style={{ textAlign: 'center' }}>New here? <Link to="/register">Create account</Link></p>
    </form>
  );
}
