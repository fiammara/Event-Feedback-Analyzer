import React, { useEffect, useState } from 'react';
import { fetchEvents } from '../api/api';
import FeedbackForm from './FeedbackForm';

export default function EventList({ newEvent }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showFeedback, setShowFeedback] = useState({});

  useEffect(() => {
    const loadEvents = async () => {
      setLoading(true);
      try {
        const data = await fetchEvents();
        setEvents((Array.isArray(data) ? data : []).map(ev => ({ ...ev, feedbackList: ev.feedbackList || [] })));
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    loadEvents();
  }, []);

  useEffect(() => {
    if (newEvent) {
      setEvents(prev => [{ ...newEvent, feedbackList: [] }, ...prev]);
    }
  }, [newEvent]);

  const toggleFeedbackForm = (id) => {
    setShowFeedback(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const handleFeedbackAdded = (eventId, feedback) => {
    setEvents(prev => prev.map(ev => ev.id === eventId ? { ...ev, feedbackList: [...ev.feedbackList, feedback] } : ev));
  };

  if (loading) return <p className="text-center mt-10">Loading events...</p>;
  if (!events.length) return <p className="text-center mt-10">No events available. Add one above!</p>;

  return (
    <div className="mt-6 space-y-6 max-w-4xl mx-auto">
      {events.map(event => (
        <div key={event.id} className="border p-4 rounded shadow bg-white">
          <h2 className="text-lg font-bold">{event.title}</h2>
          <p>{event.description}</p>

          {/* Add Feedback Button */}
          <button
            onClick={() => toggleFeedbackForm(event.id)}
            className="mt-2 px-3 py-1 rounded bg-blue-500 text-white hover:bg-blue-600"
          >
            {showFeedback[event.id] ? 'Hide Feedback Form' : 'Add Feedback'}
          </button>

          {/* Feedback Form */}
          {showFeedback[event.id] && (
            <FeedbackForm
              eventId={event.id}
              onFeedbackAdded={(feedback) => handleFeedbackAdded(event.id, feedback)}
            />
          )}

          {/* Feedback List */}
          {event.feedbackList.length > 0 && (
            <div className="mt-4 border-t pt-2">
              <h3 className="font-semibold mb-2">Feedback:</h3>
              <ul className="space-y-1">
                {event.feedbackList.map((fb, idx) => (
                  <li key={idx} className="bg-gray-50 p-2 rounded border">
                    {fb.text}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}



