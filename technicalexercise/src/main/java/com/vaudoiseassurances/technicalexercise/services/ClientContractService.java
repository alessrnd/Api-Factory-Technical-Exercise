package com.vaudoiseassurances.technicalexercise.services;

import com.vaudoiseassurances.technicalexercise.dto.ClientContractDTO;
import com.vaudoiseassurances.technicalexercise.dto.ClientContractResponseDTO;
import com.vaudoiseassurances.technicalexercise.dto.ContractCostSumDTO;
import com.vaudoiseassurances.technicalexercise.exception.ResourceNotFoundException;
import com.vaudoiseassurances.technicalexercise.model.Client;
import com.vaudoiseassurances.technicalexercise.model.ClientContract;
import com.vaudoiseassurances.technicalexercise.repository.ClientContractRepository;
import com.vaudoiseassurances.technicalexercise.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientContractService {

    private final ClientContractRepository contractRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public ClientContract getContractById(Long id) {
        log.debug("Fetching contract with id: {}", id);
        return contractRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
    }

    public ClientContract createContract(ClientContractDTO contractDTO) {
        log.info("Creating new contract for client id: {}", contractDTO.clientId());
        
        Client client = clientRepository.findById(contractDTO.clientId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Client not found with id: " + contractDTO.clientId()));

        ClientContract contract = new ClientContract();
        contract.setClient(client);
        
        contract.setStartDate(contractDTO.startDate() != null ? 
            contractDTO.startDate() : LocalDate.now());
        
        contract.setEndDate(contractDTO.endDate());
        contract.setCostAmount(contractDTO.costAmount());
        
        ClientContract savedContract = contractRepository.save(contract);
        log.info("Contract created with id: {}", savedContract.getId());
        return savedContract;
    }

    public ClientContract updateContractCostAmount(Long id, BigDecimal newCostAmount) {
        log.info("Updating cost amount for contract id: {}", id);
        
        ClientContract contract = getContractById(id);
        contract.setCostAmount(newCostAmount);
        
        ClientContract updatedContract = contractRepository.save(contract);
        log.info("Contract cost updated: {} -> {}", id, newCostAmount);
        return updatedContract;
    }

    @Transactional(readOnly = true)
    public List<ClientContractResponseDTO> getActiveContractsByClientId(
            Long clientId, 
            LocalDateTime updateDateFilter) {
        log.debug("Fetching active contracts for client id: {}", clientId);
        
        clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        
        LocalDate today = LocalDate.now();
        List<ClientContract> contracts;
        
        if (updateDateFilter != null) {
            log.debug("Applying updateDate filter: {}", updateDateFilter);
            contracts = contractRepository.findActiveContractsByClientIdAndUpdateDate(
                clientId, today, updateDateFilter);
        } else {
            contracts = contractRepository.findActiveContractsByClientId(clientId, today);
        }
        
        log.debug("Found {} active contracts", contracts.size());
        return contracts.stream()
            .map(this::mapToResponseDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public ContractCostSumDTO getTotalActiveCostAmountForClient(Long clientId) {
        log.debug("Calculating total cost for client id: {}", clientId);
        
        clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        
        LocalDate today = LocalDate.now();
        BigDecimal sum = contractRepository.sumActiveCostAmountByClientId(clientId, today);
        
        BigDecimal totalSum = sum != null ? sum : BigDecimal.ZERO;
        log.info("Total active cost for client {}: {}", clientId, totalSum);
        
        return new ContractCostSumDTO(clientId, totalSum);
    }

    private ClientContractResponseDTO mapToResponseDTO(ClientContract contract) {
        return new ClientContractResponseDTO(
            contract.getId(),
            contract.getClient().getId(),
            contract.getStartDate(),
            contract.getEndDate(),
            contract.getCostAmount()
        );
    }
}