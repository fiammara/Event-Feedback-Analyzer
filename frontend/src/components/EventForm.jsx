import React, { useState } from 'react';
import { createEvent } from '../api/api';

export default function EventForm({ onEventAdded, compact = false }) {
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
      className={`mb-6 border bg-white ${compact ? 'p-3 max-w-full' : 'p-4 max-w-2xl mx-auto'} rounded-lg`}
    >
      {!compact && <h2 className="font-bold mb-2 text-xl">Add New Event</h2>}
      <input
        type="text"
        placeholder="Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="w-full mb-2 p-2 border border-gray-200 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-200"
        disabled={loading}
      />
      <textarea
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="w-full mb-3 p-2 border border-gray-200 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-200"
        rows={compact ? 3 : 4}
        disabled={loading}
      />
      <div className="flex justify-end">
        <button
          type="submit"
          className={loading ? 'btn-disabled' : 'btn btn-primary'}
          disabled={loading}
        >
          {loading ? 'Adding...' : (compact ? 'Add' : 'Add Event')}
        </button>
      </div>
    </form>
  );
}
