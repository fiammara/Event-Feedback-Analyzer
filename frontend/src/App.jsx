import React, { useState } from 'react';
import EventForm from './components/EventForm';
import EventList from './components/EventList';

function App() {
  const [newEvent, setNewEvent] = useState(null);

  const handleNewEvent = (event) => {
    setNewEvent(event);
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-blue-100 font-sans">
      {/* Header */}
      <header className="text-center py-8 md:py-10 bg-gradient-to-r from-blue-200 via-blue-100 to-blue-200 shadow-md">
        <h1 className="text-3xl md:text-4xl lg:text-5xl font-extrabold text-gray-800 mb-1 md:mb-2">
          Event Feedback Analyzer
        </h1>
        <p className="text-gray-600 text-sm md:text-base max-w-2xl mx-auto">
          Events, Feedback, and AI-Evaluated Insights
        </p>
      </header>

      {/* Main Content - Event list is the focus; form is a smaller side card on wide screens */}
      <main className="max-w-6xl mx-auto px-4 mt-6">
        <div className="md:flex md:gap-8 items-start">
          {/* Main column: Event List */}
          <div className="md:w-2/3 space-y-6">
            <EventList newEvent={newEvent} />
          </div>

          {/* Side column: smaller Event Form */}
          <aside className="w-full md:w-1/3 mt-6 md:mt-0">
            <div className="bg-white p-4 md:p-6 rounded-2xl shadow border border-gray-200 sticky top-24">
              <h2 className="text-lg md:text-2xl font-semibold text-gray-700 mb-3 text-center">Add New Event</h2>
              <EventForm onEventAdded={handleNewEvent} compact />
            </div>
          </aside>
        </div>
      </main>

      {/* Footer */}
      <footer className="mt-16 py-8 text-center text-gray-500 text-sm border-t border-gray-200">
        &copy; 2025 Event Analyzer. All rights reserved.
      </footer>
    </div>
  );
}

export default App;
