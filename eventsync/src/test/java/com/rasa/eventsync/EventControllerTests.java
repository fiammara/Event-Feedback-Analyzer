package com.rasa.eventsync;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasa.eventsync.business.handlers.EventNotFoundException;
import com.rasa.eventsync.business.service.EventService;
import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.FeedbackSummary;
import com.rasa.eventsync.web.EventController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTests {

    private static final String BASE_URL = "/api/events";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    private Event createSampleEvent(Long id, String title, String description) {
        Event e = new Event();
        e.setId(id);
        e.setTitle(title);
        e.setDescription(description);
        return e;
    }

    @Test
    void getEventById_shouldReturnEvent_whenEventExists() throws Exception {
        Event sampleEvent = createSampleEvent(1L, "Spring Conference", "Annual spring event");
        Mockito.when(eventService.findEventById(1L)).thenReturn(Optional.of(sampleEvent));

        mockMvc.perform(get(BASE_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Spring Conference"))
            .andExpect(jsonPath("$.description").value("Annual spring event"));
    }

    @Test
    void getEventById_shouldReturn404_whenEventNotFound() throws Exception {
        Mockito.when(eventService.findEventById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllEvents_shouldReturnList_whenEventsExist() throws Exception {
        Event e1 = createSampleEvent(1L, "Spring Conference", "Annual spring event");
        Event e2 = createSampleEvent(2L, "Tech Expo", "Technology exposition");
        List<Event> events = List.of(e1, e2);

        Mockito.when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Spring Conference"))
            .andExpect(jsonPath("$[1].title").value("Tech Expo"));
    }

    @Test
    void getAllEvents_shouldReturnNoContent_whenEmpty() throws Exception {
        Mockito.when(eventService.getAllEvents()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isNoContent());
    }

    @Test
    void createEvent_shouldReturnCreated_whenValid() throws Exception {
        Event toCreate = createSampleEvent(null, "New Event", "Event description");
        Event created = createSampleEvent(10L, "New Event", "Event description");

        Mockito.when(eventService.createEvent(any(Event.class))).thenReturn(created);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10L))
            .andExpect(jsonPath("$.title").value("New Event"))
            .andExpect(jsonPath("$.description").value("Event description"));
    }

    @Test
    void addFeedback_shouldReturnCreatedFeedback() throws Exception {
        Feedback feedbackRequest = new Feedback();
        feedbackRequest.setText("Great event!");

        Feedback feedbackResponse = new Feedback();
        feedbackResponse.setText("Great event!");

        Mockito.when(eventService.addFeedback(eq(1L), any(Feedback.class)))
            .thenReturn(feedbackResponse);

        mockMvc.perform(post(BASE_URL + "/1/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.text").value("Great event!"));

        Mockito.verify(eventService).addFeedback(eq(1L), any(Feedback.class));
    }

    @Test
    void getFeedbackSummary_shouldReturnSummary_whenEventExists() throws Exception {
        Map<String, Integer> sentiment = Map.of(
            "positive", 3,
            "neutral", 1,
            "negative", 1
        );

        FeedbackSummary summary = new FeedbackSummary();
        summary.setEventId(1L);
        summary.setEventTitle("Spring Conference");
        summary.setFeedbackCount(5);
        summary.setSentimentSummary(sentiment);

        Mockito.when(eventService.getFeedbackSummary(1L)).thenReturn(summary);

        mockMvc.perform(get(BASE_URL + "/1/feedback/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.eventId").value(1L))
            .andExpect(jsonPath("$.eventTitle").value("Spring Conference"))
            .andExpect(jsonPath("$.feedbackCount").value(5))
            .andExpect(jsonPath("$.sentimentSummary.positive").value(3))
            .andExpect(jsonPath("$.sentimentSummary.neutral").value(1))
            .andExpect(jsonPath("$.sentimentSummary.negative").value(1));

        Mockito.verify(eventService).getFeedbackSummary(1L);
    }

    @Test
    void addFeedback_shouldReturn404_whenEventNotFound() throws Exception {
        Feedback feedbackRequest = new Feedback();
        feedbackRequest.setText("Nice!");

        Mockito.when(eventService.addFeedback(eq(99L), any(Feedback.class)))
            .thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(post(BASE_URL + "/99/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getFeedbackSummary_shouldReturn404_whenEventNotFound() throws Exception {
        Mockito.when(eventService.getFeedbackSummary(42L))
            .thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(get(BASE_URL + "/42/feedback/summary"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createEvent_shouldReturn400_whenInvalidRequest() throws Exception {
        Event invalid = new Event();

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_shouldReturn400_whenTitleIsMissing() throws Exception {
        Event invalidEvent = new Event();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEvent)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void createEvent_shouldReturn400_whenMalformedJson() throws Exception {
        String badJson = "{ \"title\": \"Event\", \"description\": \"Desc\"";

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Malformed JSON request"));
    }

    @Test
    void getEventById_shouldReturn400_whenIdIsNotLong() throws Exception {
        mockMvc.perform(get("/api/events/notALong"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid value for parameter")));
    }

    @Test
    void getAllEvents_shouldReturn500_whenServiceThrows() throws Exception {
        Mockito.when(eventService.getAllEvents()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("An unexpected error occurred: Unexpected error")));
    }

}
