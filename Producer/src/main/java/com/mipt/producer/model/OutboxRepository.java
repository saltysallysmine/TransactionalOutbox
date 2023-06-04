package com.mipt.producer.model;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OutboxRepository extends JpaRepository<Plan, Long> {
}
