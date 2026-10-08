package Inventory.System.service;

import Inventory.System.dto.ItemRequest;
import Inventory.System.dto.ItemResponse;
import Inventory.System.dto.QuantityRequest;
import Inventory.System.exception.BadRequestException;
import Inventory.System.exception.ResourceNotFoundException;
import Inventory.System.model.Inventory;
import Inventory.System.model.Item;
import Inventory.System.repository.InventoryRepository;
import Inventory.System.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id) { return toResponse(findItem(id)); }

    public ItemResponse createItem(ItemRequest request) {
        Item item = Item.builder().name(request.getName().trim()).category(request.getCategory().trim()).quantity(request.getQuantity()).build();
        return toResponse(itemRepository.save(item));
    }

    public ItemResponse addStock(Long id, QuantityRequest request) {
        validatePositiveWholeNumber(request.getQuantity());
        Item item = findItemForUpdate(id);
        item.setQuantity(item.getQuantity() + request.getQuantity());
        return toResponse(itemRepository.save(item));
    }

    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = findItem(id);
        int allocated = allocatedFor(id);
        if (request.getQuantity() < allocated) {
            throw new BadRequestException("Quantity cannot be less than the quantity already allocated (" + allocated + ")");
        }
        item.setName(request.getName().trim());
        item.setCategory(request.getCategory().trim());
        item.setQuantity(request.getQuantity());
        return toResponse(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        Item item = findItem(id);
        if (allocatedFor(id) > 0) {
            throw new BadRequestException("This item cannot be deleted because it has stock allocated to a room");
        }
        itemRepository.delete(item);
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item haijapatikana yenye id: " + id));
    }

    private Item findItemForUpdate(Long id) {
        return itemRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Item haijapatikana yenye id: " + id));
    }

    private void validatePositiveWholeNumber(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity to add must be a whole number greater than 0");
        }
    }

    private int allocatedFor(Long itemId) {
        return inventoryRepository.findByItemId(itemId).stream().mapToInt(Inventory::getQuantity).sum();
    }

    private ItemResponse toResponse(Item item) {
        int allocated = allocatedFor(item.getId());
        return ItemResponse.builder().id(item.getId()).name(item.getName()).category(item.getCategory()).quantity(item.getQuantity()).allocated(allocated).remaining(Math.max(0, item.getQuantity() - allocated)).build();
    }
}
