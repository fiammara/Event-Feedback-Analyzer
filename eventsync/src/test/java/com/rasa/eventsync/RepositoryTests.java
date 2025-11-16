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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryTests {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private EventDAO savedEvent;

    @BeforeEach
    void setup() {
        EventDAO event = new EventDAO();
        event.setTitle("Test Event");
        event.setDescription("Description");
        savedEvent = eventRepository.save(event);
    }

    @Test
    void saveAndFindEvent() {
        Optional<EventDAO> found = eventRepository.findById(savedEvent.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Event", found.get().getTitle());
    }

    @Test
    void findAllEvents() {
        List<EventDAO> events = eventRepository.findAll();
        assertFalse(events.isEmpty());
        assertEquals(savedEvent.getTitle(), events.get(0).getTitle());
    }

    @Test
    void saveAndFindFeedback() {
        FeedbackDAO feedback = new FeedbackDAO();
        feedback.setText("Great event!");
        feedback.setEvent(savedEvent);

        FeedbackDAO savedFeedback = feedbackRepository.save(feedback);

        Optional<FeedbackDAO> found = feedbackRepository.findById(savedFeedback.getId());
        assertTrue(found.isPresent());
        assertEquals("Great event!", found.get().getText());
        assertEquals(savedEvent.getId(), found.get().getEvent().getId());
    }

    @Test
    void findAllFeedbacks() {
        FeedbackDAO feedback = new FeedbackDAO();
        feedback.setText("Awesome!");
        feedback.setEvent(savedEvent);
        feedbackRepository.save(feedback);

        List<FeedbackDAO> feedbacks = feedbackRepository.findAll();
        assertEquals(1, feedbacks.size());
        assertEquals("Awesome!", feedbacks.get(0).getText());
    }

    @Test
    void feedbackLinkedToEvent() {
        FeedbackDAO feedback = new FeedbackDAO();
        feedback.setText("Excellent!");
        feedback.setEvent(savedEvent);
        savedEvent.getFeedbackList().add(feedback);
        feedbackRepository.save(feedback);

        EventDAO eventFromDb = eventRepository.findById(savedEvent.getId()).orElseThrow();
        assertTrue(eventFromDb.getFeedbackList().contains(feedback));
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
        eventRepository.deleteAll();

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
