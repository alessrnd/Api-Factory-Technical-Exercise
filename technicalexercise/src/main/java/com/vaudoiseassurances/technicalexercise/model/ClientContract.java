package com.vaudoiseassurances.technicalexercise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "Client is required")
    @JsonIgnore
    private Client client;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.01", message = "Cost must be greater than 0")
    private BigDecimal costAmount;
    
    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime updateDate;
    
    @PrePersist
    protected void onCreate() {
        this.updateDate = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
