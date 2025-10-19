package com.vaudoiseassurances.technicalexercise.controller;

import com.vaudoiseassurances.technicalexercise.dto.ClientContractDTO;
import com.vaudoiseassurances.technicalexercise.dto.ClientContractResponseDTO;
import com.vaudoiseassurances.technicalexercise.dto.ContractCostSumDTO;
import com.vaudoiseassurances.technicalexercise.dto.UpdateCostAmountDTO;
import com.vaudoiseassurances.technicalexercise.model.ClientContract;
import com.vaudoiseassurances.technicalexercise.services.ClientContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ClientContractController {

    private final ClientContractService contractService;

    @PostMapping
    public ResponseEntity<ClientContract> createContract(
            @Valid @RequestBody ClientContractDTO contractDTO) {
        ClientContract createdContract = contractService.createContract(contractDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    }

    @PatchMapping("/{id}/cost")
    public ResponseEntity<ClientContract> updateContractCostAmount(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCostAmountDTO updateDTO) {
        ClientContract updatedContract = contractService.updateContractCostAmount(
            id, updateDTO.costAmount());
        return ResponseEntity.ok(updatedContract);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClientContractResponseDTO>> getActiveContractsByClientId(
            @PathVariable Long clientId,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime updateDate) {
        List<ClientContractResponseDTO> contracts = contractService
            .getActiveContractsByClientId(clientId, updateDate);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/client/{clientId}/total-cost")
    public ResponseEntity<ContractCostSumDTO> getTotalActiveCostAmount(
            @PathVariable Long clientId) {
        ContractCostSumDTO result = contractService.getTotalActiveCostAmountForClient(clientId);
        return ResponseEntity.ok(result);
    }
}