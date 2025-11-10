package com.rasa.eventsync.business.mappers;

import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import com.rasa.eventsync.model.Feedback;
import com.rasa.eventsync.model.Sentiment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedbackMapStructMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "sentiment", source = "sentiment")
    Feedback feedbackDAOToFeedback(FeedbackDAO feedbackDAO);

    @Mapping(target = "sentiment", source = "sentiment")
    FeedbackDAO feedbackToDAO(Feedback feedback);

    default String map(Sentiment sentiment) {
        return sentiment == null ? null : sentiment.name();
    }

    default Sentiment map(String sentiment) {
        return sentiment == null ? null : Sentiment.valueOf(sentiment.toUpperCase());
    }

    List<Feedback> feedbackDAOToFeedbackList(List<FeedbackDAO> feedbackDAOList);
    List<FeedbackDAO> feedbackToDAOList(List<Feedback> feedbackList);
}


