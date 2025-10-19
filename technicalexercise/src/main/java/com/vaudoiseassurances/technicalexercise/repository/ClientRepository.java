package com.vaudoiseassurances.technicalexercise.repository;

import com.vaudoiseassurances.technicalexercise.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
}