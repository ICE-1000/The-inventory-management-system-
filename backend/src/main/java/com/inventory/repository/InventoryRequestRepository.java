package com.inventory.repository;

import com.inventory.model.InventoryRequest;
import com.inventory.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, UUID> {
    List<InventoryRequest> findByDepartmentIdOrderByCreatedAtDesc(UUID departmentId);
    List<InventoryRequest> findAllByOrderByCreatedAtDesc();
    List<InventoryRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);
}
