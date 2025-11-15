package com.rasa.eventsync;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasa.eventsync.business.repository.EventRepository;
import com.rasa.eventsync.business.repository.FeedbackRepository;
import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EventControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @BeforeEach
    void setup() {
        feedbackRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void getAllEvents_shouldReturnEvents() throws Exception {
        EventDAO e1 = new EventDAO();
        e1.setTitle("Event 1");
        e1.setDescription("Desc 1");
        EventDAO e2 = new EventDAO();
        e2.setTitle("Event 2");
        e2.setDescription("Desc 2");
        eventRepository.saveAll(List.of(e1, e2));

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllEvents_shouldReturnNoContent_whenEmpty() throws Exception {
        mockMvc.perform(get("/api/events"))
            .andExpect(status().isNoContent());
    }

    @Test
    void getEventById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/events/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createEvent_shouldReturn400_whenInvalid() throws Exception {
        Event invalid = new Event();
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void addFeedback_shouldReturn404_whenEventNotFound() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setText("Test");
        mockMvc.perform(post("/api/events/999/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Event not found")));
    }

    @Test
    void createEvent_shouldReturn400_whenMalformedJson() throws Exception {
        String badJson = "{ \"title\": \"Test Event\" ";

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Malformed JSON request")));
    }

    @Test
    void getEventById_shouldReturn400_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/events/abc"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid value for parameter")));
    }

    @Test
    void createEvent_shouldReturnCreated_whenValid() throws Exception {
        Event valid = new Event();
        valid.setTitle("New Event");
        valid.setDescription("Description");

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valid)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("New Event"))
            .andExpect(jsonPath("$.description").value("Description"))
            .andExpect(jsonPath("$.id").exists());
    }

}
