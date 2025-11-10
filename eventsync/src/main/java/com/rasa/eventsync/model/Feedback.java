package com.rasa.eventsync.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Schema(description = "Feedback text")
    private String text;

    @Schema(description = "Sentiment classification of the feedback")
    private String sentiment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
