package com.rasa.eventsync.business.repository;

import com.rasa.eventsync.business.repository.model.FeedbackDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackDAO, Long> {

}
