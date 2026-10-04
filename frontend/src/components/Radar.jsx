/** Decorative radar. Dot positions come from the user id, never from real distance. */
export default function Radar({ people, on, matchIntent, onPick }) {
  const dots = people.map((p) => {
    const a = (((p.userId * 137) % 360) * Math.PI) / 180;
    const r = 60 + (p.userId % 3) * 28;
    return { p, x: 150 + r * Math.cos(a), y: 150 + r * Math.sin(a) };
  });
  return (
    <svg viewBox="0 0 300 300" style={{ width: '100%', display: 'block', margin: '0 0 10px' }}>
      {[128, 96, 64, 32].map((r, i) => (
        <circle key={r} cx="150" cy="150" r={r} fill="none" stroke={on ? '#ff6a0055' : '#2a2a2a'} strokeDasharray={i % 2 ? '3 5' : undefined} />
      ))}
      {on && (
        <>
          <defs>
            <linearGradient id="sg" x1="0" y1="0" x2="1" y2="0">
              <stop offset="0" stopColor="#ff6a00" stopOpacity=".55" />
              <stop offset="1" stopColor="#ff6a00" stopOpacity="0" />
            </linearGradient>
            <radialGradient id="rg">
              <stop offset="0" stopColor="#ff6a00" stopOpacity=".2" />
              <stop offset="1" stopColor="#ff6a00" stopOpacity="0" />
            </radialGradient>
          </defs>
          <circle cx="150" cy="150" r="140" fill="url(#rg)" />
          <circle className="pulse" cx="150" cy="150" r="128" fill="#ff6a00" />
          <g className="sweep"><path d="M150 150 L278 150 A128 128 0 0 0 248 68 Z" fill="url(#sg)" /></g>
          {dots.map(({ p, x, y }) => (
            <g key={p.userId} tabIndex={0} role="button" aria-label={`${p.displayName}, nearby. Quick actions`} style={{ cursor: 'pointer' }}
               onClick={() => onPick(p)} onKeyDown={(e) => e.key === 'Enter' && onPick(p)}>
              <circle cx={x} cy={y} r="17" fill="#ff6a00" stroke={p.intentMatch ? '#fff' : 'none'} strokeWidth="3" />
              <text x={x} y={y + 5} textAnchor="middle" fontWeight="800" fontSize="14" fill="#000">{p.displayName[0].toUpperCase()}</text>
            </g>
          ))}
        </>
      )}
      <circle cx="150" cy="150" r="9" fill="#fff" />
    </svg>
  );
}
