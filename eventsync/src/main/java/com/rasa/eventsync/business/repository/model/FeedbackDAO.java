package com.rasa.eventsync.business.repository.model;

import com.rasa.eventsync.model.Sentiment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Schema(description = "Database entity representing feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventDAO event;

    private String text;

    @Enumerated(EnumType.STRING)
    private String sentiment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
