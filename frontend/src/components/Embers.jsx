export default function Embers() {
  return (
    <div className="embers" aria-hidden="true">
      {Array.from({ length: 16 }, (_, i) => (
        <i key={i} style={{ left: `${(i * 37) % 100}%`, '--dx': `${(i % 5 - 2) * 18}px`, animationDuration: `${6 + (i * 7) % 7}s`, animationDelay: `${(i * 13) % 6}s` }} />
      ))}
    </div>
  );
}
