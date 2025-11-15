package com.rasa.eventsync;

import com.rasa.eventsync.business.repository.EventRepository;
import com.rasa.eventsync.business.repository.FeedbackRepository;
import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    // ----------------- EventRepository Tests -----------------

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

    // ----------------- FeedbackRepository Tests -----------------

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

}
