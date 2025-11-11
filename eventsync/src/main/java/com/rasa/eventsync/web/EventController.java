package com.rasa.eventsync.web;

import com.rasa.eventsync.business.service.EventService;
import com.rasa.eventsync.configs.DescriptionVariables;
import com.rasa.eventsync.configs.HTMLResponseMessages;
import com.rasa.eventsync.model.Event;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.FeedbackSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Tag(name = DescriptionVariables.EVENT)
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @Operation(
        summary = "Find event by ID",
        description = "Returns an event for the given ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200),
        @ApiResponse(responseCode = "401", description = HTMLResponseMessages.HTTP_401),
        @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403),
        @ApiResponse(responseCode = "404", description = HTMLResponseMessages.HTTP_404),
        @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
        @Parameter(description = "ID of the event to retrieve", required = true)
        @PathVariable Long id) {

        log.info("Request received: Get event by ID {}", id);

        Optional<Event> event = eventService.findEventById(id);
        return event.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
        summary = "List all events",
        description = "Retrieve all events."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200),
        @ApiResponse(responseCode = "204", description = HTMLResponseMessages.HTTP_204)
    })
    public ResponseEntity<List<Event>> getAllEvents() {
        log.info("Fetching all events");

        List<Event> events = eventService.getAllEvents();

        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @PostMapping
    @Operation(
        summary = "Create a new event",
        description = "Creates a new event and returns the created entity."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HTMLResponseMessages.HTTP_201),
        @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400),
        @ApiResponse(responseCode = "401", description = HTMLResponseMessages.HTTP_401),
        @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403),
        @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500)
    })
    public ResponseEntity<Event> createEvent(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Event object to create",
            required = true
        )
        @Valid @RequestBody Event event) {

        log.info("Request received to create a new event: {}", event);
        Event created = eventService.createEvent(event);
        log.info("Event created successfully with ID: {}", created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{eventId}/feedback")
    @Operation(summary = "Add feedback to an event", description = "Adds a new feedback to the specified event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HTMLResponseMessages.HTTP_201),
        @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500)
    })
    public ResponseEntity<Feedback> addFeedback(
        @PathVariable Long eventId,
        @Valid @RequestBody Feedback feedback) {

        log.info("Adding feedback to event {}: {}", eventId, feedback.getText());
        Feedback created = eventService.addFeedback(eventId, feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{eventId}/feedback/summary")
    @Operation(summary = "Get feedback count and sentiment summary for an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500)
    })
    public ResponseEntity<FeedbackSummary> getFeedbackSummary(@PathVariable Long eventId) {

        FeedbackSummary summary = eventService.getFeedbackSummary(eventId);
        return ResponseEntity.ok(summary);
    }
}

