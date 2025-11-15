package com.rasa.eventsync.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Model of event feedback ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Schema(description = "The unique id of the feedback")
    private Long id;

    @Schema(description = "Id of event, related to the feedback")
    private Long eventId;

    @Schema(description = "Feedback text")
    private String text;

    @Schema(description = "Sentiment classification of the feedback")
    private String sentiment;

    @Schema(description = "Time of a feedback creation")
    private LocalDateTime createdAt = LocalDateTime.now();
}
