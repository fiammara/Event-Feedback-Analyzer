package com.rasa.eventsync;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.rasa.eventsync.business.handlers.EventNotFoundException;
import com.rasa.eventsync.business.mappers.EventMapStructMapper;
import com.rasa.eventsync.business.mappers.FeedbackMapStructMapper;
import com.rasa.eventsync.business.repository.EventRepository;
import com.rasa.eventsync.business.repository.FeedbackRepository;
import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import com.rasa.eventsync.business.service.impl.EventServiceImpl;
import com.rasa.eventsync.business.service.impl.SentimentServiceImpl;
import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.FeedbackSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventMapStructMapper eventMapper;

    @Mock
    private FeedbackMapStructMapper feedbackMapper;

    @Mock
    private SentimentServiceImpl sentimentService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event sampleEvent;
    private EventDAO sampleEventDAO;
    private Feedback sampleFeedback;
    private FeedbackDAO sampleFeedbackDAO;

    @BeforeEach
    void setup() {
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setTitle("Test Event");
        sampleEvent.setDescription("Description");
        sampleEvent.setFeedbackList(new ArrayList<>());

        sampleEventDAO = new EventDAO();
        sampleEventDAO.setId(1L);
        sampleEventDAO.setTitle("Test Event");
        sampleEventDAO.setDescription("Description");

        sampleFeedback = new Feedback();
        sampleFeedback.setText("Great event!");

        sampleFeedbackDAO = new FeedbackDAO();
        sampleFeedbackDAO.setId(10L);
    }

    @Test
    void createEvent_shouldReturnCreatedEvent() {
        when(eventMapper.eventToDAO(sampleEvent)).thenReturn(sampleEventDAO);
        when(eventRepository.save(sampleEventDAO)).thenReturn(sampleEventDAO);
        when(eventMapper.eventDAOToEvent(sampleEventDAO)).thenReturn(sampleEvent);

        Event created = eventService.createEvent(sampleEvent);

        assertEquals(sampleEvent.getTitle(), created.getTitle());
        verify(eventRepository).save(sampleEventDAO);
    }

    @Test
    void findEventById_shouldReturnEventIfExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEventDAO));
        when(eventMapper.eventDAOToEvent(sampleEventDAO)).thenReturn(sampleEvent);

        Optional<Event> found = eventService.findEventById(1L);

        assertTrue(found.isPresent());
        assertEquals(sampleEvent.getTitle(), found.get().getTitle());
    }

    @Test
    void getAllEvents_shouldReturnMappedList() {
        List<EventDAO> daoList = List.of(sampleEventDAO);
        when(eventRepository.findAll()).thenReturn(daoList);
        when(eventMapper.eventDAOToEvent(sampleEventDAO)).thenReturn(sampleEvent);

        List<Event> result = eventService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals(sampleEvent.getTitle(), result.get(0).getTitle());
    }

    @Test
    void addFeedback_shouldAnalyzeSentimentAndSaveFeedback() {
        Feedback feedback = new Feedback();
        feedback.setText("I love this event!");

        FeedbackDAO feedbackDAO = new FeedbackDAO();
        feedbackDAO.setId(100L);

        Feedback savedFeedback = new Feedback();
        savedFeedback.setId(100L);
        savedFeedback.setText(feedback.getText());
        savedFeedback.setSentiment("POSITIVE");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEventDAO));
        when(sentimentService.analyzeSentiment(feedback.getText())).thenReturn("POSITIVE");
        when(feedbackMapper.feedbackToDAO(feedback)).thenReturn(feedbackDAO);
        when(feedbackRepository.save(feedbackDAO)).thenReturn(feedbackDAO);
        when(feedbackMapper.feedbackDAOToFeedback(feedbackDAO)).thenReturn(savedFeedback);

        Feedback result = eventService.addFeedback(1L, feedback);

        assertNotNull(result);
        assertEquals("POSITIVE", result.getSentiment());
        assertEquals("I love this event!", result.getText());

        verify(sentimentService).analyzeSentiment(feedback.getText());
        verify(feedbackRepository).save(feedbackDAO);
    }

    @Test
    void addFeedback_shouldThrowException_whenEventNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        Feedback feedback = new Feedback();
        feedback.setText("Missing event test");

        assertThrows(EventNotFoundException.class, () -> eventService.addFeedback(999L, feedback));

        verify(eventRepository).findById(999L);
        verifyNoInteractions(feedbackRepository);
    }

    @Test
    void getFeedbackSummary_shouldReturnCorrectCounts() {
        Feedback f1 = new Feedback(); f1.setSentiment("POSITIVE");
        Feedback f2 = new Feedback(); f2.setSentiment("POSITIVE");
        Feedback f3 = new Feedback(); f3.setSentiment("NEGATIVE");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Event");
        event.setFeedbackList(List.of(f1, f2, f3));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEventDAO));
        when(eventMapper.eventDAOToEvent(sampleEventDAO)).thenReturn(event);

        FeedbackSummary summary = eventService.getFeedbackSummary(1L);

        assertEquals(3, summary.getFeedbackCount());
        assertEquals(2, summary.getSentimentSummary().get("POSITIVE"));
        assertEquals(1, summary.getSentimentSummary().get("NEGATIVE"));
        assertEquals(0, summary.getSentimentSummary().get("NEUTRAL"));
    }

    @Test
    void getFeedbackSummary_shouldThrowException_whenEventNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getFeedbackSummary(999L));

        verify(eventRepository).findById(999L);
    }
}

