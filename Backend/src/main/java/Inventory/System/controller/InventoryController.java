package Inventory.System.controller;

import Inventory.System.dto.InventoryRequest;
import Inventory.System.dto.InventoryResponse;
import Inventory.System.dto.QuantityRequest;
import Inventory.System.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STOREKEEPER', 'SUPERVISOR')")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id,
                                                               @Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @PatchMapping("/{id}/add")
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<InventoryResponse> addQuantity(@PathVariable Long id,
                                                          @Valid @RequestBody QuantityRequest request) {
        return ResponseEntity.ok(inventoryService.addQuantity(id, request));
    }

    @PatchMapping("/{id}/remove")
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<Void> removeQuantity(@PathVariable Long id,
                                                @Valid @RequestBody QuantityRequest request) {
        inventoryService.removeQuantity(id, request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

}
