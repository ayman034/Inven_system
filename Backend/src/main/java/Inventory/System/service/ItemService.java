package Inventory.System.service;

import Inventory.System.dto.ItemRequest;
import Inventory.System.dto.ItemResponse;
import Inventory.System.exception.BadRequestException;
import Inventory.System.exception.ResourceNotFoundException;
import Inventory.System.model.Inventory;
import Inventory.System.model.Item;
import Inventory.System.repository.InventoryRepository;
import Inventory.System.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ItemResponse getItemById(Long id) { return toResponse(findItem(id)); }

    public ItemResponse createItem(ItemRequest request) {
        if (itemRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("Item hii tayari ipo");
        }
        Item item = Item.builder().name(request.getName().trim()).category(request.getCategory().trim()).quantity(request.getQuantity()).build();
        return toResponse(itemRepository.save(item));
    }

    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = findItem(id);
        int allocated = allocatedFor(id);
        if (request.getQuantity() < allocated) {
            throw new BadRequestException("Quantity haiwezi kuwa chini ya kiasi kilichotengwa tayari (" + allocated + ")");
        }
        if (itemRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new BadRequestException("Item nyingine yenye jina hili tayari ipo");
        }
        item.setName(request.getName().trim());
        item.setCategory(request.getCategory().trim());
        item.setQuantity(request.getQuantity());
        return toResponse(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        Item item = findItem(id);
        if (allocatedFor(id) > 0) {
            throw new BadRequestException("Item hii haiwezi kufutwa kwa sababu ina stock iliyotengwa kwenye room");
        }
        itemRepository.delete(item);
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item haijapatikana yenye id: " + id));
    }

    private int allocatedFor(Long itemId) {
        return inventoryRepository.findByItemId(itemId).stream().mapToInt(Inventory::getQuantity).sum();
    }

    private ItemResponse toResponse(Item item) {
        int allocated = allocatedFor(item.getId());
        return ItemResponse.builder().id(item.getId()).name(item.getName()).category(item.getCategory()).quantity(item.getQuantity()).allocated(allocated).remaining(Math.max(0, item.getQuantity() - allocated)).build();
    }
}
