package com.inventory.service;

import com.inventory.dto.CreateRequestDTO;
import com.inventory.dto.InventoryRequestDTO;
import com.inventory.dto.UpdateRequestStatusDTO;
import com.inventory.exception.BadRequestException;
import com.inventory.exception.InvalidRequestStatusTransitionException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.InventoryRequest;
import com.inventory.model.RequestStatus;
import com.inventory.model.User;
import com.inventory.repository.InventoryRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryRequestService {
    private final InventoryRequestRepository requestRepository;

    public InventoryRequestService(InventoryRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional
    public InventoryRequestDTO createRequest(CreateRequestDTO dto, User currentUser) {
        if (currentUser.getDepartment() == null) {
            throw new BadRequestException("Department user must belong to a department");
        }
        if (dto.getQuantity() == null || dto.getQuantity() < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
        InventoryRequest request = new InventoryRequest();
        request.setDepartment(currentUser.getDepartment());
        request.setItemName(dto.getItemName());
        request.setQuantity(dto.getQuantity());
        request.setNeededBy(dto.getNeededBy());
        request.setDescription(dto.getDescription());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedBy(currentUser);
        return toDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<InventoryRequestDTO> getRequestsForDepartment(UUID departmentId) {
        return requestRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryRequestDTO> getAllRequests(String status) {
        if (status == null || status.isBlank()) {
            return requestRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).collect(Collectors.toList());
        }
        RequestStatus requestStatus;
        try {
            requestStatus = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status filter");
        }
        return requestRepository.findByStatusOrderByCreatedAtDesc(requestStatus).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public InventoryRequestDTO updateRequestStatus(UUID requestId, UpdateRequestStatusDTO dto, User admin) {
        InventoryRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestStatusTransitionException("Only pending requests can be updated");
        }
        if (dto.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }
        if (dto.getStatus() == RequestStatus.REJECTED && (dto.getRejectionReason() == null || dto.getRejectionReason().isBlank())) {
            throw new BadRequestException("Rejection reason is required for rejected requests");
        }
        if (dto.getStatus() == RequestStatus.APPROVED) {
            request.setRejectionReason(null);
        }
        request.setStatus(dto.getStatus());
        request.setRejectionReason(dto.getStatus() == RequestStatus.REJECTED ? dto.getRejectionReason() : null);
        return toDto(requestRepository.save(request));
    }

    private InventoryRequestDTO toDto(InventoryRequest request) {
        InventoryRequestDTO dto = new InventoryRequestDTO();
        dto.setId(request.getId());
        dto.setDepartmentId(request.getDepartment().getId());
        dto.setDepartmentName(request.getDepartment().getDepartmentName());
        dto.setItemName(request.getItemName());
        dto.setQuantity(request.getQuantity());
        dto.setNeededBy(request.getNeededBy());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
}
