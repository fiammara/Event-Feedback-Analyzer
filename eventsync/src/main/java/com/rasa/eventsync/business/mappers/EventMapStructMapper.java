package com.rasa.eventsync.business.mappers;

import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.model.Event;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring", uses = FeedbackMapStructMapper.class)
public interface EventMapStructMapper {

    Event eventDAOToEvent(EventDAO eventDAO);

    EventDAO eventToDAO(Event event);

}