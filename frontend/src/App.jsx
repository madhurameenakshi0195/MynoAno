import { Navigate, Outlet, Route, Routes, useLocation } from 'react-router-dom';
import { useAuth } from './auth.jsx';
import TabBar from './components/TabBar.jsx';
import Embers from './components/Embers.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import VerifyOtp from './pages/VerifyOtp.jsx';
import CreateProfile from './pages/CreateProfile.jsx';
import Nearby from './pages/Nearby.jsx';
import Events from './pages/Events.jsx';
import EventDetail from './pages/EventDetail.jsx';
import CreateEvent from './pages/CreateEvent.jsx';
import Tea from './pages/Tea.jsx';
import Chats from './pages/Chats.jsx';
import ChatRoom from './pages/ChatRoom.jsx';
import Me from './pages/Me.jsx';
import Ano from './pages/Ano.jsx';
import PersonProfile from './pages/PersonProfile.jsx';
import Safety from './pages/Safety.jsx';

function AuthLayout() {
  return <div id="app"><div className="view"><Outlet /></div></div>;
}

function Shell() {
  const { me, ready } = useAuth();
  const { pathname } = useLocation();
  if (!ready) return null;
  if (!me) return <Navigate to="/login" replace />;
  if (!me.profileComplete) return <Navigate to="/create-profile" replace />;
  const ano = pathname === '/ano';
  return (
    <div id="app" className={ano ? 'ano' : ''}>
      <div className="view"><Outlet /></div>
      {ano && <Embers />}
      <TabBar />
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify" element={<VerifyOtp />} />
        <Route path="/create-profile" element={<CreateProfile />} />
      </Route>
      <Route element={<Shell />}>
        <Route path="/nearby" element={<Nearby />} />
        <Route path="/events" element={<Events />} />
        <Route path="/events/new" element={<CreateEvent />} />
        <Route path="/events/:id" element={<EventDetail />} />
        <Route path="/tea" element={<Tea />} />
        <Route path="/chats" element={<Chats />} />
        <Route path="/chats/:id" element={<ChatRoom />} />
        <Route path="/me" element={<Me />} />
        <Route path="/ano" element={<Ano />} />
        <Route path="/people/:id" element={<PersonProfile />} />
        <Route path="/safety" element={<Safety />} />
      </Route>
      <Route path="*" element={<Navigate to="/nearby" replace />} />
    </Routes>
  );
}
