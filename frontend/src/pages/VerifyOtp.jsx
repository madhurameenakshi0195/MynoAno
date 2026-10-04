import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { useAuth } from '../auth.jsx';
import { useToast } from '../ui.jsx';

export default function VerifyOtp() {
  const { state } = useLocation();
  const nav = useNavigate();
  const toast = useToast();
  const { login } = useAuth();
  const [code, setCode] = useState('');
  const [err, setErr] = useState('');
  if (!state?.email) return <Navigate to="/register" replace />;

  const submit = async (e) => {
    e.preventDefault();
    try {
      const r = await api('/auth/verify', { method: 'POST', body: { email: state.email, code } });
      await login(r.token);
      nav('/create-profile');
    } catch (x) { setErr(x.message); }
  };
  const resend = async () => { try { await api('/auth/resend', { method: 'POST', body: { email: state.email } }); toast('New code sent'); } catch (x) { setErr(x.message); } };

  return (
    <form className="pad" style={{ paddingTop: 48 }} onSubmit={submit}>
      <div className="logo">Myno<span className="or">Ano</span></div>
      <h2 style={{ marginTop: 24 }}>Verify your account</h2>
      <p className="mu">Enter the 6-digit code sent to {state.email} and your phone.</p>
      <input className="in" inputMode="numeric" maxLength={6} placeholder="6-digit code" value={code} onChange={(e) => setCode(e.target.value)}
             style={{ letterSpacing: 8, fontSize: 22, textAlign: 'center' }} />
      <div className="err">{err}</div>
      <button className="btn">Verify</button>
      <p className="mu" style={{ textAlign: 'center' }}><a href="#resend" onClick={(e) => { e.preventDefault(); resend(); }}>Resend code</a></p>
    </form>
  );
}
