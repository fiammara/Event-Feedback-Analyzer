import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE || '';

const axiosInstance = axios.create({

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