import { Link, useLocation } from 'react-router-dom';

const ICONS = {
  nearby: <><circle cx="12" cy="12" r="3" /><circle cx="12" cy="12" r="8" /></>,
  events: <><rect x="3" y="5" width="18" height="16" rx="3" /><path d="M3 10h18M8 3v4M16 3v4" /></>,
  tea: <path d="M4 8h13v6a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5zM17 10h2a2 2 0 0 1 0 4h-2" />,
  chats: <path d="M21 12a8 8 0 0 1-11.5 7.2L4 20l1-4.5A8 8 0 1 1 21 12z" />,
  me: <><circle cx="12" cy="8" r="4" /><path d="M4 21c1-5 15-5 16 0" /></>,
};

export default function TabBar() {
  const { pathname } = useLocation();
  const items = [['nearby', 'Nearby'], ['events', 'Events'], ['tea', 'Tea'], ['chats', 'Chats'], ['me', pathname === '/ano' ? 'ANO' : 'MYNO']];
  return (
    <nav>
      {items.map(([k, label]) => {
        const on = k === 'me' ? ['/me', '/ano', '/safety'].includes(pathname) : pathname.startsWith('/' + k);
        return (
          <Link key={k} to={'/' + k} className={on ? 'on' : ''}>
            <svg viewBox="0 0 24 24">{ICONS[k]}</svg>
            {label}
          </Link>
        );
      })}
    </nav>
  );
}
