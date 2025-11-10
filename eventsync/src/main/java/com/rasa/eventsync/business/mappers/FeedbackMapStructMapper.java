package com.rasa.eventsync.business.mappers;

import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import com.rasa.eventsync.model.Feedback;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeedbackMapStructMapper {
    Feedback feedbackDAOToFeedback(FeedbackDAO feedbackDAO);

    FeedbackDAO feedbackToDAO(Feedback feedback);

    List<Feedback> feedbackDAOToFeedbackList(List<FeedbackDAO> feedbackDAOList);

    List<FeedbackDAO> feedbackToDAOList(List<Feedback> feedbackList);
}
