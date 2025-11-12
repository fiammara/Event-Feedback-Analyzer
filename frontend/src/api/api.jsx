import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE || '';
//const BASE_URL = 'http://localhost:8080';

// Configure axios with CORS settings
const axiosInstance = axios.create({
  // If BASE_URL is empty we use relative paths so requests go to same origin (e.g. /api/...)
  baseURL: BASE_URL || '',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

export const fetchEvents = async () => {
  const response = await axiosInstance.get('/api/events');
  return response.data || [];
};

export const createEvent = async (event) => {
  const response = await axiosInstance.post('/api/events', event);
  return response.data;
};

export async function fetchFeedbackSummary(eventId) {
  const response = await axiosInstance.get(`/api/events/${eventId}/feedback/summary`);
  return response.data;
}

export async function addFeedback(feedback) {
  const response = await axiosInstance.post(`/api/events/${feedback.eventId}/feedback`, feedback);
  return response.data;
}