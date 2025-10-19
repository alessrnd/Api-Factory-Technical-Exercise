package com.vaudoiseassurances.technicalexercise.repository;

import com.vaudoiseassurances.technicalexercise.model.Client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByDeletedFalse();
}