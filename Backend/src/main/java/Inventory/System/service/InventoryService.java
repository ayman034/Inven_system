package Inventory.System.service;

import Inventory.System.dto.InventoryRequest;
import Inventory.System.dto.InventoryResponse;
import Inventory.System.dto.QuantityRequest;
import Inventory.System.exception.BadRequestException;
import Inventory.System.exception.ResourceNotFoundException;
import Inventory.System.model.Inventory;
import Inventory.System.model.Item;
import Inventory.System.model.Room;
import Inventory.System.repository.InventoryRepository;
import Inventory.System.repository.ItemRepository;
import Inventory.System.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long id) {
        return toResponse(findInventory(id));
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        Item item = findItemForUpdate(request.getItemId());
        Room room = findRoom(request.getRoomId());

        Inventory inventory = inventoryRepository.findByItemAndRoom(item, room).orElse(null);
        int current = inventory == null ? 0 : inventory.getQuantity();
        int remaining = remainingFor(item, inventory == null ? null : inventory.getId());

        if (request.getQuantity() > remaining) {
            throw new BadRequestException("The remaining quantity for this item is only " + remaining);
        }

        if (inventory == null) {
            inventory = Inventory.builder().item(item).room(room).quantity(request.getQuantity()).build();
        } else {
            inventory.setQuantity(current + request.getQuantity());
        }

        return toResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory inventory = findInventory(id);
        Item item = findItemForUpdate(request.getItemId());
        Room room = findRoom(request.getRoomId());

        inventoryRepository.findByItemAndRoom(item, room).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("This item already exists in this room");
            }
        });

        int remaining = remainingFor(item, id);
        if (request.getQuantity() > remaining) {
            throw new BadRequestException("The maximum allowed quantity is " + remaining);
        }

        inventory.setItem(item);
        inventory.setRoom(room);
        inventory.setQuantity(request.getQuantity());
        return toResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse addQuantity(Long id, QuantityRequest request) {
        Inventory inventory = findInventory(id);
        Item item = findItemForUpdate(inventory.getItem().getId());
        int remaining = remainingFor(item, id);

        if (request.getQuantity() > remaining) {
            throw new BadRequestException("The remaining quantity for this item is only " + remaining);
        }

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        return toResponse(inventoryRepository.save(inventory));
    }

    public void removeQuantity(Long id, Integer quantity) {
        Inventory inventory = findInventory(id);
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        if (quantity > inventory.getQuantity()) {
            throw new BadRequestException("You cannot remove more than " + inventory.getQuantity());
        }

        int newQuantity = inventory.getQuantity() - quantity;
        if (newQuantity == 0) inventoryRepository.delete(inventory);
        else {
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);
        }
    }

    public void deleteInventory(Long id) {
        inventoryRepository.delete(findInventory(id));
    }

    private Inventory findInventory(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory haijapatikana yenye id: " + id));
    }

    private Item findItemForUpdate(Long id) {
        return itemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item haijapatikana"));
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room haijapatikana"));
    }

    private int remainingFor(Item item, Long excludeInventoryId) {
        int allocated = inventoryRepository.findByItem(item).stream()
                .filter(i -> excludeInventoryId == null || !i.getId().equals(excludeInventoryId))
                .mapToInt(Inventory::getQuantity)
                .sum();
        return Math.max(0, item.getQuantity() - allocated);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .itemId(inventory.getItem().getId())
                .itemName(inventory.getItem().getName())
                .category(inventory.getItem().getCategory())
                .roomId(inventory.getRoom().getId())
                .roomName(inventory.getRoom().getName())
                .quantity(inventory.getQuantity())
                .build();
    }
}
