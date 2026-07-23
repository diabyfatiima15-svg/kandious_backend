package com.kandious.repository;

import com.kandious.entity.CommandeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeDetailRepository extends JpaRepository<CommandeDetail, Long> {
}