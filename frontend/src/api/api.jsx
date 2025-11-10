import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/events';

export const fetchEvents = async () => {
  const response = await axios.get(BASE_URL);
  return response.data || [];
};

export const createEvent = async (event) => {
  const response = await axios.post(BASE_URL, event);
  return response.data; // always return backend-created event
};