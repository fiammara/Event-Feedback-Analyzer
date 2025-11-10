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
    private Long eventId;
    private String eventTitle;
    private int feedbackCount;
    private Map<String, Integer> sentimentSummary;
}
