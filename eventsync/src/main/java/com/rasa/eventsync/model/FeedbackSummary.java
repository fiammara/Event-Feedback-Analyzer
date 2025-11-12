package com.rasa.eventsync.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Schema(description = "Model of event feedback summaries ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSummary {

    @Schema(description = "Id of event, related to the feedback summary")
    private Long eventId;
    @Schema(description = "Title of event, related to the feedback summary")
    private String eventTitle;

    @Schema(description = "Count how many feedbacks are")
    private int feedbackCount;
    @Schema(description = "Summary of feedback sentiments (POSITIVE, NEUTRAL, NEGATIVE) for an event")
    private Map<String, Integer> sentimentSummary;

}
