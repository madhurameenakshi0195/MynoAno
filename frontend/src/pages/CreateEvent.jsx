import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { Seg, isoLocal, useToast } from '../ui.jsx';

export default function CreateEvent() {
  const nav = useNavigate();
  const toast = useToast();
  const [f, setF] = useState({
    title: '', place: '', venueGroup: '', venueType: 'Mall', description: '', highlights: '',
    startsAt: isoLocal(Date.now()), endsAt: isoLocal(Date.now() + 3 * 36e5), lat: '', lng: '', visibility: 'PUBLIC',
  });
  const [photos, setPhotos] = useState([]);
  const [err, setErr] = useState('');
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  useEffect(() => {
    navigator.geolocation?.getCurrentPosition((p) => setF((x) => ({ ...x, lat: x.lat || p.coords.latitude.toFixed(5), lng: x.lng || p.coords.longitude.toFixed(5) })));
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    try {
      const body = {
        title: f.title, place: f.place, venueGroup: f.venueGroup, venueType: f.venueType, description: f.description,
        highlights: f.highlights.split(',').map((x) => x.trim()).filter(Boolean),
        lat: +f.lat, lng: +f.lng, visibility: f.visibility,
        startsAt: new Date(f.startsAt).toISOString(), endsAt: new Date(f.endsAt).toISOString(),
      };
      const ev = await api('/events', { method: 'POST', body });
      for (const file of photos) {
        const form = new FormData();
        form.append('file', file);
        await api(`/events/${ev.id}/photos`, { method: 'POST', form });
      }
      toast('Event published. It expires on its own at the end.');
      nav(`/events/${ev.id}`);
    } catch (x) { setErr(x.message); }
  };

  return (
    <form className="pad" onSubmit={submit}>
      <h1>Create event</h1>
      <input className="in" placeholder="Event name" value={f.title} onChange={set('title')} />
      <input className="in" placeholder="Place, e.g. Level 2 food court" value={f.place} onChange={set('place')} />
      <input className="in" placeholder="Venue group, e.g. Hauz Mall or Block C" value={f.venueGroup} onChange={set('venueGroup')} />
      <select className="in" aria-label="Venue type" value={f.venueType} onChange={set('venueType')}>
        {['Mall', 'Building', 'University', 'Outdoors', 'Other'].map((t) => <option key={t}>{t}</option>)}
      </select>
      <p className="mu" style={{ margin: '6px 0 0' }}>Starts</p>
      <input className="in" type="datetime-local" value={f.startsAt} onChange={set('startsAt')} />
      <p className="mu" style={{ margin: '6px 0 0' }}>Completion date. The event expires on its own.</p>
      <input className="in" type="datetime-local" value={f.endsAt} onChange={set('endsAt')} />
      <div className="row"><input className="in" placeholder="Latitude" value={f.lat} onChange={set('lat')} /><input className="in" placeholder="Longitude" value={f.lng} onChange={set('lng')} /></div>
      <textarea className="in" placeholder="What is this event about?" value={f.description} onChange={set('description')} />
      <input className="in" placeholder="What is inside (comma separated)" value={f.highlights} onChange={set('highlights')} />
      <label className="btn g" style={{ display: 'block', textAlign: 'center', margin: '6px 0' }}>
        Add photos from past events
        <input type="file" accept="image/*" multiple hidden onChange={(e) => setPhotos([...e.target.files].slice(0, 6))} />
      </label>
      <div className="mu">{photos.length ? `${photos.length} photo${photos.length > 1 ? 's' : ''} added` : ''}</div>
      <Seg options={[['PUBLIC', 'Public (MYNO)'], ['ANO', 'ANO circle']]} value={f.visibility} onChange={(v) => setF({ ...f, visibility: v })} />
      <div className="err">{err}</div>
      <button className="btn">Publish event</button>
    </form>
  );
}
