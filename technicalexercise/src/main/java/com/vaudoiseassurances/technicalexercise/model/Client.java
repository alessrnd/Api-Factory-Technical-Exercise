package com.vaudoiseassurances.technicalexercise.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Contract;

import com.vaudoiseassurances.technicalexercise.enums.ClientType;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name="clients")
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Client type is required")
    private ClientType clientType;
    
    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Column(nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be valid (10-15 digits)")
    private String phone;
    
    @Past(message = "Birthdate must be in the past")
    @Column(updatable = false)
    private LocalDate birthdate;
    
    @Pattern(regexp = "^[a-z]{3}-[0-9]{3}$", message = "Company identifier must follow format: aaa-123")
    @Column(updatable = false)
    private String companyIdentifier;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<ClientContract> contracts = new ArrayList<>();
}
