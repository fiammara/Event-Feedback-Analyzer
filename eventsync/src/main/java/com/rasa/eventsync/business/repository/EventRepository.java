package com.rasa.eventsync.business.repository;

import com.rasa.eventsync.business.repository.model.EventDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventDAO, Long> {

}
