package com.mipt.producer.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Plan, Long> {
    @Query("SELECT p FROM Plan p WHERE p.isWrittenToDB = False OR p.isWrittenToBroker = False")
    List<Plan> findByProgress();
}
