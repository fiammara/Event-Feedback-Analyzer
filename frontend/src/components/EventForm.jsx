import React, { useState } from 'react';
import { createEvent } from '../api/api';

export default function EventForm({ onEventAdded }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) return alert('Title is required!');

    setLoading(true);
    try {
      const createdEvent = await createEvent({ title, description });
      console.log('Event created:', createdEvent);
      setTitle('');
      setDescription('');
      onEventAdded(createdEvent);
    } catch (err) {
      console.error('Failed to create event:', err.response || err);
      alert('Failed to add event. Check console.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="mb-6 p-4 border rounded bg-white max-w-2xl mx-auto"
    >
      <h2 className="font-bold mb-2">Add New Event</h2>
      <input
        type="text"
        placeholder="Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="border p-2 mb-2 w-full"
        disabled={loading}
      />
      <textarea
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="border p-2 mb-2 w-full"
        disabled={loading}
      />
      <button
        type="submit"
        className={`px-4 py-2 rounded text-white ${
          loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-600'
        }`}
        disabled={loading}
      >
        {loading ? 'Adding...' : 'Add Event'}
      </button>
    </form>
  );
}
