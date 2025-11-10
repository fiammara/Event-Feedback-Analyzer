package com.rasa.eventsync.business.service.impl;

import com.rasa.eventsync.business.handlers.EventNotFoundException;
import com.rasa.eventsync.business.mappers.EventMapStructMapper;
import com.rasa.eventsync.business.mappers.FeedbackMapStructMapper;
import com.rasa.eventsync.business.repository.EventRepository;
import com.rasa.eventsync.business.repository.FeedbackRepository;
import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import com.rasa.eventsync.business.service.EventService;
import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.FeedbackSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;
    private final EventMapStructMapper eventMapper;
    private final FeedbackMapStructMapper feedbackMapper;
    private final SentimentServiceImpl sentimentService;
    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(EventRepository eventRepository,
                            FeedbackRepository feedbackRepository, EventMapStructMapper eventMapper, FeedbackMapStructMapper feedbackMapper, SentimentServiceImpl sentimentService, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
        this.eventMapper = eventMapper;
        this.feedbackMapper = feedbackMapper;
        this.sentimentService = sentimentService;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Event> getAllEvents() {

        List<EventDAO> eventDAOList = eventRepository.findAll();
        log.info("Got events list. Size is: {}", eventDAOList.size());
        return eventDAOList.stream()
            .map(eventMapper::eventDAOToEvent)
            .toList();
    }

    @Override
    public Event createEvent(Event event) {

        EventDAO savedDAO = eventRepository.save(eventMapper.eventToDAO(event));
        log.info("Created event with ID: {}", savedDAO.getId());

        return eventMapper.eventDAOToEvent(savedDAO);

    }

    @Override
    public Optional<Event> findEventById(Long id) {
        Optional<Event> eventOptional =
            eventRepository.findById(id)
                .map(eventMapper::eventDAOToEvent);

        log.info("Event with id {} is {}", id, eventOptional);
        return eventOptional;
    }

    @Override
    public Feedback addFeedback(Long eventId, Feedback feedback) {

        Event existingEvent = findEventById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));


        String sentiment = sentimentService.analyzeSentiment(feedback.getText());
        feedback.setSentiment(sentiment);

        FeedbackDAO feedbackDAO = feedbackMapper.feedbackToDAO(feedback);
        FeedbackDAO savedDAO = feedbackRepository.save(feedbackDAO);

        Feedback savedFeedback = feedbackMapper.feedbackDAOToFeedback(savedDAO);
        existingEvent.getFeedbackList().add(savedFeedback);

        log.info("Feedback added with ID: {} and sentiment: {}", savedDAO.getId(), sentiment);

        return savedFeedback;
    }
    @Override
    public FeedbackSummary getFeedbackSummary(Long eventId) {
        Event event = findEventById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        List<Feedback> feedbackList = event.getFeedbackList();

        Map<String, Integer> sentimentSummary = new HashMap<>();
        for (Feedback feedback : feedbackList) {
            String sentiment = feedback.getSentiment();
            sentimentSummary.put(sentiment, sentimentSummary.getOrDefault(sentiment, 0) + 1);
        }

        return new FeedbackSummary(
            event.getId(),
            event.getTitle(),
            feedbackList.size(),
            sentimentSummary
        );
    }

}
