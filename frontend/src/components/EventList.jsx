 import React, { useEffect, useState } from 'react';
 import { fetchEvents, fetchFeedbackSummary, addFeedback } from '../api/api';
 import FeedbackForm from './FeedbackForm';
 import FeedbackSummaryChart from './FeedbackSummaryChart';

 export default function EventList({ newEvent }) {
   const [events, setEvents] = useState([]);
   const [loading, setLoading] = useState(true);
   const [showFeedback, setShowFeedback] = useState({});


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

   // Handle newEvent prop
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

   const toggleFeedbackForm = (id) => {
     setShowFeedback((prev) => ({ ...prev, [id]: !prev[id] }));
   };

   const handleFeedbackAdded = async (eventId, feedbackInput) => {
     try {
       // Add feedback via backend
       const savedFeedback = await addFeedback({ ...feedbackInput, eventId });

       console.log('Saved feedback:', savedFeedback);

       // Update local feedback list immediately
       setEvents((prev) =>
         prev.map((ev) =>
           ev.id === eventId
             ? { ...ev, feedbackList: [...(ev.feedbackList || []), savedFeedback] }
             : ev
         )
       );

       // Fetch updated summary safely
       let updatedSummary;
       try {
         updatedSummary = await fetchFeedbackSummary(eventId);
       } catch (err) {
         console.warn(`Failed to fetch updated summary for event ${eventId}:`, err.message);
         updatedSummary = {
           eventId,
           eventTitle: `Event ${eventId}`,
           feedbackCount: (events.find(ev => ev.id === eventId)?.feedbackList.length || 0),
           sentimentSummary: { positive: 0, neutral: 0, negative: 0 },
         };
       }

       setEvents((prev) =>
         prev.map((ev) => (ev.id === eventId ? { ...ev, summary: updatedSummary } : ev))
       );
     } catch (err) {
       console.error('Failed to add feedback or update summary', err);
     }
   };

   if (loading) {
     return <p className="text-center mt-10">Loading events...</p>;
   }

   if (!events.length) {
     return <p className="text-center mt-10">No events available. Add one above!</p>;
   }

   return (
     <div className="mt-6 space-y-6 max-w-4xl mx-auto">
       {events.map((event) => (
         <div key={event.id} className="border p-4 rounded shadow bg-white">
           <h2 className="text-lg font-bold">{event.title}</h2>
           <p>{event.description}</p>

         {/* Feedback Summary / Chart */}
         {event.summary && (
           <div className="mt-4 bg-gray-50 p-3 rounded">
             {event.summary.feedbackCount > 0 ? (
               <>
                 <h4 className="font-semibold mb-2">
                   Feedback Summary ({event.summary.feedbackCount} total):
                 </h4>
                 <div style={{ width: '100%', height: 150 }}>
                   <FeedbackSummaryChart summary={event.summary} />
                 </div>
               </>
             ) : (
               <p className="text-gray-500 italic">No summary yet. Add some feedback!</p>
             )}
           </div>
         )}

           {/* Add Feedback Button */}
           <button
             onClick={() => toggleFeedbackForm(event.id)}
             className="mt-3 px-3 py-1 rounded bg-blue-500 text-white hover:bg-blue-600"
           >
             {showFeedback[event.id] ? 'Hide Feedback Form' : 'Add Feedback'}
           </button>

           {/* Feedback Form */}
           {showFeedback[event.id] && (
             <FeedbackForm
               eventId={event.id}
               onFeedbackAdded={(fb) => handleFeedbackAdded(event.id, fb)}
             />
           )}

           {/* Feedback List */}
           {event.feedbackList?.length > 0 && (
             <div className="mt-4 border-t pt-2">
               <h3 className="font-semibold mb-2">Feedback:</h3>
               <ul className="space-y-1">
                 {event.feedbackList.map((fb, idx) => (
                   <li key={idx} className="bg-gray-50 p-2 rounded border">
                     {fb.text || 'No text'}
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
