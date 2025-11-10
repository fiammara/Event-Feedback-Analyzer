import React, { useState } from 'react';
import EventForm from './components/EventForm';
import EventList from './components/EventList';

function App() {
  const [refresh, setRefresh] = useState(false);
  const [newEvent, setNewEvent] = useState(null);

  const handleNewEvent = (event) => {
    setNewEvent(event); // pass to EventList
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="text-2xl font-bold mb-4">Events Dashboard</h1>

      <EventForm onEventAdded={handleNewEvent} />
      <EventList refresh={refresh} newEvent={newEvent} />
    </div>
  );
}

export default App;
