package com.vaudoiseassurances.technicalexercise.config;

import com.vaudoiseassurances.technicalexercise.enums.ClientType;
import com.vaudoiseassurances.technicalexercise.model.Client;
import com.vaudoiseassurances.technicalexercise.model.ClientContract;
import com.vaudoiseassurances.technicalexercise.repository.ClientRepository;
import com.vaudoiseassurances.technicalexercise.repository.ClientContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final ClientContractRepository contractRepository;
    private final Faker faker = new Faker(Locale.of("fr", "CH"));

    private static final int INDIVIDUAL_CLIENTS_COUNT = 15;
    private static final int COMPANY_CLIENTS_COUNT = 10;
    private static final int MIN_CONTRACTS_PER_CLIENT = 1;
    private static final int MAX_CONTRACTS_PER_CLIENT = 3;

    @Override
    public void run(String... args) {
        if (clientRepository.count() == 0) {
            log.info("========================================");
            log.info("Starting data seeding...");
            log.info("========================================");
            seedData();
            log.info("========================================");
            log.info("Data seeding completed successfully!");
            log.info("========================================");
            logSummary();
        } else {
            log.info("Database already contains data. Skipping seeding.");
        }
    }

    private void seedData() {
        List<Client> clients = new ArrayList<>();
        
        log.info("Generating {} individual clients...", INDIVIDUAL_CLIENTS_COUNT);
        clients.addAll(generateIndividualClients(INDIVIDUAL_CLIENTS_COUNT));
        
        log.info("Generating {} company clients...", COMPANY_CLIENTS_COUNT);
        clients.addAll(generateCompanyClients(COMPANY_CLIENTS_COUNT));
        
        clientRepository.saveAll(clients);
        log.info("Saved {} clients", clients.size());
        
        log.info("Generating contracts for all clients...");
        List<ClientContract> contracts = generateContractsForClients(clients);
        contractRepository.saveAll(contracts);
        log.info("Saved {} contracts", contracts.size());
    }

    private List<Client> generateIndividualClients(int count) {
    List<Client> clients = new ArrayList<>();
    
    for (int i = 0; i < count; i++) {
        Client client = new Client();
        client.setClientType(ClientType.PERSON);
        client.setName(faker.name().fullName());
        client.setEmail(faker.internet().emailAddress());
        client.setPhone(generateSwissPhone());
        client.setBirthdate(generateBirthdate());
        client.setCompanyIdentifier(null);
        clients.add(client);
    }
    
    return clients;
}

    private List<Client> generateCompanyClients(int count) {
    List<Client> clients = new ArrayList<>();
    List<String> suffixes = List.of("SA", "Sàrl", "AG", "GmbH", "& Co");
    
    for (int i = 0; i < count; i++) {
        Client client = new Client();
        client.setClientType(ClientType.COMPANY);
        client.setName(faker.company().name() + " " + 
                       suffixes.get(faker.random().nextInt(suffixes.size())));
        client.setEmail(faker.internet().emailAddress());
        client.setPhone(generateSwissPhone());
        client.setBirthdate(null);
        client.setCompanyIdentifier(generateCompanyIdentifier());
        clients.add(client);
    }
    
    return clients;
}

    private List<ClientContract> generateContractsForClients(List<Client> clients) {
        List<ClientContract> contracts = new ArrayList<>();
        
        for (Client client : clients) {
            int contractCount = faker.random().nextInt(
                MIN_CONTRACTS_PER_CLIENT, 
                MAX_CONTRACTS_PER_CLIENT
            );
            
            for (int i = 0; i < contractCount; i++) {
                // Le premier contrat est toujours actif (70% pour les suivants)
                boolean isActive = (i == 0) || (faker.random().nextInt(100) < 70);
                ClientContract contract = generateContract(client, isActive);
                contracts.add(contract);
                
                log.debug("Created contract for client {}: {} CHF (active: {})", 
                    client.getName(), contract.getCostAmount(), isActive);
            }
        }
        
        return contracts;
    }

    private ClientContract generateContract(Client client, boolean isActive) {
        ClientContract contract = new ClientContract();
        contract.setClient(client);
        
        // Date de début aléatoire dans les 3 dernières années
        LocalDate startDate = faker.date()
            .past(1095, TimeUnit.DAYS)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        
        contract.setStartDate(startDate);
        
        // Contrat actif = pas de date de fin
        if (isActive) {
            contract.setEndDate(null);
        } else {
            // Contrat terminé = date de fin entre startDate et aujourd'hui
            LocalDate endDate = startDate.plusYears(faker.random().nextInt(1, 3));
            if (endDate.isAfter(LocalDate.now())) {
                endDate = LocalDate.now().minusDays(faker.random().nextInt(1, 365));
            }
            contract.setEndDate(endDate);
        }
        
        // Montant basé sur le type de client
        BigDecimal costAmount = switch (client.getClientType()) {
            case PERSON -> 
                // Particuliers : 500 à 3000 CHF
                BigDecimal.valueOf(faker.random().nextInt(500, 3000));
            case COMPANY -> 
                // Entreprises : 5000 à 30000 CHF
                BigDecimal.valueOf(faker.random().nextInt(5000, 30000));
        };
        
        contract.setCostAmount(costAmount);
        
        return contract;
    }

    private String generateSwissPhone() {
        return "+4179%07d".formatted(faker.random().nextInt(0, 9999999));
    }

    private LocalDate generateBirthdate() {
        return faker.date()
            .birthday(18, 80)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    private String generateCompanyIdentifier() {
        String letters = "";
        for (int i = 0; i < 3; i++) {
            letters += (char) ('a' + faker.random().nextInt(26));
        }
        String numbers = "%03d".formatted(faker.random().nextInt(100, 999));
        return letters + "-" + numbers;
    }

    private void logSummary() {
        long totalClients = clientRepository.count();
        long individualClients = clientRepository.findAll().stream()
            .filter(c -> c.getClientType() == ClientType.PERSON)
            .count();
        long companyClients = clientRepository.findAll().stream()
            .filter(c -> c.getClientType() == ClientType.COMPANY)
            .count();
        long totalContracts = contractRepository.count();
        long activeContracts = contractRepository.findAll().stream()
            .filter(c -> c.getEndDate() == null || c.getEndDate().isAfter(LocalDate.now()))
            .count();
        
        log.info("========================================");
        log.info("📊 SEEDING SUMMARY");
        log.info("========================================");
        log.info("Total Clients: {}", totalClients);
        log.info("  - Individual: {}", individualClients);
        log.info("  - Company: {}", companyClients);
        log.info("Total Contracts: {}", totalContracts);
        log.info("  - Active: {}", activeContracts);
        log.info("  - Inactive: {}", totalContracts - activeContracts);
        log.info("========================================");
    }
}
