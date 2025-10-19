package com.vaudoiseassurances.technicalexercise.repository;

import com.vaudoiseassurances.technicalexercise.model.ClientContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientContractRepository extends JpaRepository<ClientContract, Long> {
    
    List<ClientContract> findByClientIdAndEndDateIsNull(Long clientId);
    
    @Query("SELECT c FROM ClientContract c WHERE c.client.id = :clientId " +
           "AND (c.endDate IS NULL OR c.endDate > :currentDate)")
    List<ClientContract> findActiveContractsByClientId(
        @Param("clientId") Long clientId,
        @Param("currentDate") LocalDate currentDate
    );
    
    @Query("SELECT c FROM ClientContract c WHERE c.client.id = :clientId " +
           "AND (c.endDate IS NULL OR c.endDate > :currentDate) " +
           "AND c.updateDate >= :updateDate")
    List<ClientContract> findActiveContractsByClientIdAndUpdateDate(
        @Param("clientId") Long clientId,
        @Param("currentDate") LocalDate currentDate,
        @Param("updateDate") LocalDateTime updateDate
    );
    
    @Query("SELECT COALESCE(SUM(c.costAmount), 0) FROM ClientContract c " +
           "WHERE c.client.id = :clientId " +
           "AND (c.endDate IS NULL OR c.endDate > :currentDate)")
    BigDecimal sumActiveCostAmountByClientId(
        @Param("clientId") Long clientId,
        @Param("currentDate") LocalDate currentDate
    );
}