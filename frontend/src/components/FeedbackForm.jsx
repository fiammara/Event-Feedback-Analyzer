import React, { useState } from 'react';
import axios from 'axios';

export default function FeedbackForm({ eventId, onFeedbackAdded }) {
  const [text, setText] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!text.trim()) return;

    setLoading(true);
    try {
      const response = await axios.post(`http://localhost:8080/api/events/${eventId}/feedback`, { text });
      setText('');
      onFeedbackAdded(response.data); // notify parent if needed
    } catch (err) {
      console.error('Failed to add feedback', err);
      alert('Failed to add feedback. Check console.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="mt-2">
      <textarea
        placeholder="Write feedback..."
        value={text}
        onChange={(e) => setText(e.target.value)}
        className="border p-2 mb-2 w-full rounded-md focus:outline-none focus:ring-2 focus:ring-green-200"
        disabled={loading}
      />
      <button
        type="submit"
        className={loading ? 'btn-disabled' : 'btn btn-primary'}
        disabled={loading}
      >
        {loading ? 'Adding...' : 'Submit Feedback'}
      </button>
    </form>
  );
}

