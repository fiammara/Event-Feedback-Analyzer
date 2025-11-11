package com.rasa.eventsync;


import com.rasa.eventsync.business.repository.EventRepository;
import com.rasa.eventsync.business.repository.FeedbackRepository;
import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import com.rasa.eventsync.model.Sentiment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class EventRepositoryIntegrationTests {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @BeforeEach
    void cleanup() {
        feedbackRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void saveEvent_shouldPersistEvent() {
        EventDAO event = new EventDAO();
        event.setTitle("Repository Event");
        event.setDescription("Testing DB");

        EventDAO saved = eventRepository.save(event);

        assertNotNull(saved.getId());
        assertEquals("Repository Event", saved.getTitle());
        assertEquals("Testing DB", saved.getDescription());
    }

    @Test
    void saveFeedback_shouldLinkToEvent() {
        EventDAO event = new EventDAO();
        event.setTitle("Event with Feedback");
        event.setDescription("Testing relationship");

        FeedbackDAO feedback = new FeedbackDAO();
        feedback.setText("Great!");
        feedback.setSentiment(Sentiment.POSITIVE);

        event.getFeedbackList().add(feedback);
        feedback.setEvent(event);

        EventDAO savedEvent = eventRepository.save(event); // cascade saves feedback

        Optional<EventDAO> fetchedEvent = eventRepository.findById(savedEvent.getId());
        assertTrue(fetchedEvent.isPresent());
        assertEquals(1, fetchedEvent.get().getFeedbackList().size());
        FeedbackDAO savedFeedback = fetchedEvent.get().getFeedbackList().get(0);
        assertEquals("Great!", savedFeedback.getText());
        assertEquals(Sentiment.POSITIVE, savedFeedback.getSentiment());
    }

    @Test
    void findAllEvents_shouldReturnAllEvents() {
        EventDAO e1 = new EventDAO();
        e1.setTitle("Event 1");
        e1.setDescription("Desc 1");

        EventDAO e2 = new EventDAO();
        e2.setTitle("Event 2");
        e2.setDescription("Desc 2");

        eventRepository.saveAll(List.of(e1, e2));

        List<EventDAO> events = eventRepository.findAll();
        assertEquals(2, events.size());
    }

    @Test
    void findFeedbackByEventId_shouldReturnFeedbackList() {
        EventDAO event = new EventDAO();
        event.setTitle("Event for Feedback");
        event.setDescription("Testing feedback list");

        FeedbackDAO f1 = new FeedbackDAO();
        f1.setText("Nice!");
        f1.setSentiment(Sentiment.NEUTRAL);
        f1.setEvent(event);

        FeedbackDAO f2 = new FeedbackDAO();
        f2.setText("Awesome!");
        f2.setSentiment(Sentiment.POSITIVE);
        f2.setEvent(event);

        event.getFeedbackList().addAll(List.of(f1, f2));

        EventDAO savedEvent = eventRepository.save(event);

        Optional<EventDAO> fetchedEvent = eventRepository.findById(savedEvent.getId());
        assertTrue(fetchedEvent.isPresent());
        assertEquals(2, fetchedEvent.get().getFeedbackList().size());
    }

}
