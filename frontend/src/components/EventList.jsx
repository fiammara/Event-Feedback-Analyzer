import React, { useEffect, useState } from 'react';
import { fetchEvents, fetchFeedbackSummary, addFeedback } from '../api/api';
import FeedbackForm from './FeedbackForm';
import FeedbackSummaryChart from './FeedbackSummaryChart';

export default function EventList({ newEvent }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showFeedback, setShowFeedback] = useState({});
  const [showSummary, setShowSummary] = useState({});

  useEffect(() => {
    const loadEvents = async () => {
      setLoading(true);
      try {
        const data = await fetchEvents();
        const eventsArray = Array.isArray(data) ? data : [];

        const withSummaries = await Promise.all(
          eventsArray.map(async (ev) => {
            try {
              const summary = await fetchFeedbackSummary(ev.id);
              return {
                ...ev,
                summary: summary || {
                  eventId: ev.id,
                  eventTitle: ev.title,
                  feedbackCount: 0,
                  sentimentSummary: { positive: 0, neutral: 0, negative: 0 },
                },
                feedbackList: ev.feedbackList || [],
              };
            } catch (err) {
              console.warn(`No summary for event ${ev.id}:`, err.message);
              return {
                ...ev,
                summary: {
                  eventId: ev.id,
                  eventTitle: ev.title,
                  feedbackCount: 0,
                  sentimentSummary: { positive: 0, neutral: 0, negative: 0 },
                },
                feedbackList: ev.feedbackList || [],
              };
            }
          })
        );

        setEvents(withSummaries);
      } catch (err) {
        console.error('Error fetching events:', err);
        setEvents([]);
      } finally {
        setLoading(false);
      }
    };

    loadEvents();
  }, []);

  useEffect(() => {
    if (newEvent) {
      setEvents((prev) => [
        {
          ...newEvent,
          feedbackList: [],
          summary: {
            eventId: newEvent.id,
            eventTitle: newEvent.title,
            feedbackCount: 0,
            sentimentSummary: { positive: 0, neutral: 0, negative: 0 },
          },
        },
        ...prev,
      ]);
    }
  }, [newEvent]);

  const toggleFeedbackForm = (id) => setShowFeedback((p) => ({ ...p, [id]: !p[id] }));
  const toggleSummary = (id) => setShowSummary((p) => ({ ...p, [id]: !p[id] }));

  const handleFeedbackAdded = async (eventId, feedbackInput) => {
    try {
      const savedFeedback = await addFeedback({ ...feedbackInput, eventId });
      setEvents((prev) => prev.map((ev) => (ev.id === eventId ? { ...ev, feedbackList: [...(ev.feedbackList || []), savedFeedback] } : ev)));
      try {
        const updatedSummary = await fetchFeedbackSummary(eventId);
        setEvents((prev) => prev.map((ev) => (ev.id === eventId ? { ...ev, summary: updatedSummary } : ev)));
      } catch (err) {
        console.warn(`Failed to fetch updated summary for event ${eventId}:`, err.message);
      }
    } catch (err) {
      console.error('Failed to add feedback or update summary', err);
    }
  };

  if (loading) return <p className="text-center mt-10">Loading events...</p>;
  if (!events.length) return <p className="text-center mt-10">No events available. Add one above!</p>;

  return (
    <div className="mt-4 space-y-5 max-w-4xl mx-auto">
      {events.map((event) => (
        <div key={event.id} className="border rounded-lg shadow-sm bg-white hover:shadow-md transition-shadow duration-150 overflow-hidden">
          <div className="w-full bg-gray-100 px-4 py-3 md:px-5 md:py-4">
            <h2 className="text-base md:text-lg font-semibold text-gray-900 leading-tight">{event.title}</h2>
            {event.description && <p className="text-sm md:text-sm text-gray-600 mt-1 italic">{event.description}</p>}
          </div>

          <div className="px-4 py-3 md:px-5">
            <div className="flex items-start justify-between gap-3">
              <div className="flex-1">
                <span className="text-xs text-gray-500">{event.summary?.feedbackCount || 0} feedback</span>
              </div>
              <div className="flex-shrink-0 ml-3 flex flex-col items-end gap-2">
                <button onClick={() => toggleSummary(event.id)} className="btn btn-primary w-36 text-center">
                  {showSummary[event.id] ? 'Hide summary' : 'Show summary'}
                </button>
              </div>
            </div>

            {event.summary && (
              <div className="mt-3">
                {showSummary[event.id] && event.summary.feedbackCount > 0 ? (
                  <div style={{ width: '100%', minHeight: 140 }} className="mt-2">
                    <FeedbackSummaryChart summary={event.summary} />
                  </div>
                ) : (
                  event.summary.feedbackCount === 0 && <p className="text-gray-500 italic mt-2">No summary yet. Add some feedback!</p>
                )}
              </div>
            )}

            {/* Feedback section: header line with Add Feedback button */}
            <div className="mt-3 border-t pt-3">
              <div className="flex items-center justify-between">
                <div>
                  {/* Show header label only when the form is closed */}
                  {!showFeedback[event.id] && (
                    <h3 className="font-semibold mb-2 text-sm">Recent Feedbacks:</h3>
                  )}
                </div>
                <button onClick={() => toggleFeedbackForm(event.id)} className="btn btn-primary w-36 text-center">
                  {showFeedback[event.id] ? 'Hide' : 'Add Feedback'}
                </button>
              </div>

              {showFeedback[event.id] && (
                <div className="mt-2">
                  <FeedbackForm eventId={event.id} onFeedbackAdded={(fb) => handleFeedbackAdded(event.id, fb)} />
                  {/* when form is visible, move the label under the textarea and above the list */}
                  <h3 className="font-semibold mt-3 text-sm">Recent Feedbacks:</h3>
                </div>
              )}

              {event.feedbackList?.length > 0 ? (
                <ul className="space-y-2 mt-2">
                  {event.feedbackList.map((fb, idx) => {
                    // Determine sentiment label from several possible keys returned by backend
                    const sentiment = (fb.sentiment && (fb.sentiment.label || fb.sentiment)) || fb.sentimentLabel || fb.sentimentResult || fb.label || fb.category || fb.sentiment;
                    const s = (typeof sentiment === 'string' ? sentiment : '') || '';
                    const key = s.toUpperCase();
                    const colorClass = key === 'POSITIVE' || key === 'POS' || key === 'POSITIVE' ? 'border-l-4 border-green-500' : key === 'NEGATIVE' || key === 'NEG' ? 'border-l-4 border-red-500' : 'border-l-4 border-amber-400';
                    const badgeClass = key === 'POSITIVE' || key === 'POS' ? 'bg-green-100 text-green-800' : key === 'NEGATIVE' || key === 'NEG' ? 'bg-red-100 text-red-800' : 'bg-amber-100 text-amber-800';

                    return (
                      <li key={idx} className={`bg-gray-50 p-2 rounded border text-sm ${colorClass}`}> 
                        <div className="flex items-start justify-between">
                          <div className="flex-1 pr-2">{fb.text || 'No text'}</div>
                          <div className="flex-shrink-0">
                            <span className={`text-xs font-medium px-2 py-0.5 rounded ${badgeClass}`}>{(s && s.length > 0) ? s : 'NEUTRAL'}</span>
                          </div>
                        </div>
                      </li>
                    );
                  })}
                </ul>
              ) : (
                <p className="text-sm text-gray-500 italic mt-2">No feedback yet. Be the first to add one.</p>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
