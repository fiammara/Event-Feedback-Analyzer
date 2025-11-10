package com.rasa.eventsync.business.service;


import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.FeedbackSummary;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> getAllEvents();

    Event createEvent(Event event);

    Optional<Event> findEventById(Long id);

    Feedback addFeedback(Long eventId, Feedback feedback);

    FeedbackSummary getFeedbackSummary(Long eventId);
}
