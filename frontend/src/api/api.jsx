import axios from 'axios';

const BASE_URL ='http://localhost:8080';
//const BASE_URL = process.env.REACT_APP_API_BASE;
export const fetchEvents = async () => {
  const response = await axios.get(`${BASE_URL}/api/events`);
  return response.data || [];
};

export const createEvent = async (event) => {
  const response = await axios.post(`${BASE_URL}/api/events`, event);
  return response.data;
};

export async function fetchFeedbackSummary(eventId) {
  const response = await fetch(`${BASE_URL}/api/events/${eventId}/feedback/summary`);
  if (!response.ok) throw new Error(`Failed to fetch summary for event ${eventId}`);
  return response.json();
}

export async function addFeedback(feedback) {
  const response = await fetch(`${BASE_URL}/api/events/${feedback.eventId}/feedback`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(feedback),
  });

  if (!response.ok) throw new Error('Failed to add feedback');
  return response.json();
}