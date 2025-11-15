package com.rasa.eventsync.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Schema(description = "Model of event data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Schema(description = "The unique id of the event")
    private Long id;

    @Schema(description = "The title of event")
    @NotBlank(message = "Event title is required")
    private String title;

    @Schema(description = "The description of event")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "List of feedbacks about the event")
    private List<Feedback> feedbackList = new ArrayList<>();

    @Schema(description = "Time of an event creation")
    private LocalDateTime createdAt = LocalDateTime.now();

}
