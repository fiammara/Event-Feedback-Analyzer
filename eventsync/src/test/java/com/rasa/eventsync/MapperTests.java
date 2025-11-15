package com.rasa.eventsync;


import com.rasa.eventsync.business.mappers.EventMapStructMapper;
import com.rasa.eventsync.business.mappers.FeedbackMapStructMapper;
import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.Sentiment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MapperTests {

    @Autowired
    private EventMapStructMapper eventMapper;

    @Autowired
    private FeedbackMapStructMapper feedbackMapper;

    @Test
    void eventDaoToEvent_mapping() {

        FeedbackDAO feedbackDAO = new FeedbackDAO();
        feedbackDAO.setId(1L);
        feedbackDAO.setText("Great!");
        feedbackDAO.setSentiment(Sentiment.POSITIVE);

        EventDAO eventDAO = new EventDAO();
        eventDAO.setId(10L);
        eventDAO.setTitle("Event 1");
        eventDAO.setDescription("Description");
        eventDAO.setFeedbackList(List.of(feedbackDAO));

        Event event = eventMapper.eventDAOToEvent(eventDAO);

        assertEquals(eventDAO.getId(), event.getId());
        assertEquals(eventDAO.getTitle(), event.getTitle());
        assertEquals(1, event.getFeedbackList().size());
        assertEquals(feedbackDAO.getText(), event.getFeedbackList().get(0).getText());
        assertEquals(feedbackDAO.getSentiment().name(), event.getFeedbackList().get(0).getSentiment());
    }

    @Test
    void eventToDao_mapping() {

        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setText("Awesome");
        feedback.setSentiment("POSITIVE");

        Event event = new Event();
        event.setId(20L);
        event.setTitle("Test Event");
        event.setFeedbackList(List.of(feedback));

        EventDAO dao = eventMapper.eventToDAO(event);

        assertEquals(event.getId(), dao.getId());
        assertEquals(event.getTitle(), dao.getTitle());
        assertEquals(1, dao.getFeedbackList().size());
        assertEquals(feedback.getText(), dao.getFeedbackList().get(0).getText());
        assertEquals(feedback.getSentiment(), dao.getFeedbackList().get(0).getSentiment().name());
    }


    @Test
    void feedbackDaoToFeedback_mapping() {
        FeedbackDAO dao = new FeedbackDAO();
        dao.setId(5L);
        dao.setText("Nice!");
        dao.setSentiment(Sentiment.NEUTRAL);

        Feedback feedback = feedbackMapper.feedbackDAOToFeedback(dao);

        assertEquals(dao.getId(), feedback.getId());
        assertEquals(dao.getText(), feedback.getText());
        assertEquals(dao.getSentiment().name(), feedback.getSentiment());
    }

    @Test
    void feedbackToDao_mapping() {
        Feedback feedback = new Feedback();
        feedback.setId(6L);
        feedback.setText("Good");
        feedback.setSentiment("POSITIVE");

        FeedbackDAO dao = feedbackMapper.feedbackToDAO(feedback);

        assertEquals(feedback.getId(), dao.getId());
        assertEquals(feedback.getText(), dao.getText());
        assertEquals(Sentiment.valueOf(feedback.getSentiment()), dao.getSentiment());
    }
}
