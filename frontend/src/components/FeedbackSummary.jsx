import React, { useEffect, useState } from 'react';
import { fetchSummary } from '../api/api';

export default function FeedbackSummary({ eventId }) {
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    fetchSummary(eventId).then(data => setSummary(data));
  }, [eventId]);

  if (!summary) return <p>Loading summary...</p>;

  return (
    <div className="mt-2 text-sm text-gray-700">
      <p>Positive: {summary.positive}</p>
      <p>Neutral: {summary.neutral}</p>
      <p>Negative: {summary.negative}</p>
      <p>Total feedbacks: {summary.total}</p>
    </div>
  );
}
