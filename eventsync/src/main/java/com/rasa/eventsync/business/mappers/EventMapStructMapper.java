package com.rasa.eventsync.business.mappers;

import com.rasa.eventsync.business.repository.model.EventDAO;
import com.rasa.eventsync.model.Event;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", uses = FeedbackMapStructMapper.class)
public interface EventMapStructMapper {

    Event eventDAOToEvent(EventDAO eventDAO);

    EventDAO eventToDAO(Event event);

    List<Event> eventDAOToEventList(List<EventDAO> eventDAOList);

    List<EventDAO> eventToDAOList(List<Event> eventList);
}