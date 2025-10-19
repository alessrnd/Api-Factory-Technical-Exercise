package com.vaudoiseassurances.technicalexercise.services;

import com.vaudoiseassurances.technicalexercise.dto.ClientDTO;
import com.vaudoiseassurances.technicalexercise.dto.ClientUpdateDTO;
import com.vaudoiseassurances.technicalexercise.enums.ClientType;
import com.vaudoiseassurances.technicalexercise.exception.ResourceNotFoundException;
import com.vaudoiseassurances.technicalexercise.exception.ValidationException;
import com.vaudoiseassurances.technicalexercise.model.Client;
import com.vaudoiseassurances.technicalexercise.model.ClientContract;
import com.vaudoiseassurances.technicalexercise.repository.ClientRepository;
import com.vaudoiseassurances.technicalexercise.repository.ClientContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientContractRepository contractRepository;

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        log.debug("Fetching all clients");
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        log.debug("Fetching client with id: {}", id);
        return clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    public Client createClient(ClientDTO clientDTO) {
        log.info("Creating new client: {}", clientDTO.name());
    
        validateClientType(clientDTO);
        
        Client client = new Client();
        mapDtoToEntity(clientDTO, client, true);
        Client savedClient = clientRepository.save(client);
        log.info("Client created with id: {}", savedClient.getId());
        return savedClient;
    }

    public Client updateClient(Long id, ClientUpdateDTO clientUpdateDTO) {
        log.info("Updating client with id: {}", id);
        Client client = getClientById(id);
        
        mapUpdateDtoToEntity(clientUpdateDTO, client);
        
        Client updatedClient = clientRepository.save(client);
        log.info("Client updated: {}", updatedClient.getId());
        return updatedClient;
    }

    public void deleteClient(Long id) {
        log.info("Deleting client with id: {}", id);
        Client client = getClientById(id);
        
        LocalDate today = LocalDate.now();
        List<ClientContract> activeContracts = contractRepository.findByClientIdAndEndDateIsNull(id);
        
        activeContracts.forEach(contract -> {
            contract.setEndDate(today);
            log.debug("Setting end date for contract id: {} to {}", contract.getId(), today);
        });
        
        contractRepository.saveAll(activeContracts);
        
        clientRepository.delete(client);
        log.info("Client deleted: {}", id);
    }

    private void validateClientType(ClientDTO dto) {
        if (dto.clientType() == ClientType.PERSON) {
            if (dto.birthdate() == null) {
                throw new ValidationException("Birthdate is required for PERSON client type");
            }
            if (dto.companyIdentifier() != null) {
                throw new ValidationException("Company identifier must be null for PERSON client type");
            }
        } else if (dto.clientType() == ClientType.COMPANY) {
            if (dto.companyIdentifier() == null || dto.companyIdentifier().isBlank()) {
                throw new ValidationException("Company identifier is required for COMPANY client type");
            }
            if (dto.birthdate() != null) {
                throw new ValidationException("Birthdate must be null for COMPANY client type");
            }
        }
    }

    private void mapDtoToEntity(ClientDTO dto, Client entity, boolean isCreation) {
        entity.setClientType(dto.clientType());
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPhone(dto.phone());
        
        if (isCreation) {
            entity.setBirthdate(dto.birthdate());
            entity.setCompanyIdentifier(dto.companyIdentifier());
        }
    }

    private void mapUpdateDtoToEntity(ClientUpdateDTO dto, Client entity) {
        entity.setClientType(dto.clientType());
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPhone(dto.phone());
    }
}