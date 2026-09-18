package Inventory.System.service;

import Inventory.System.dto.RoomRequest;
import Inventory.System.dto.RoomResponse;
import Inventory.System.exception.BadRequestException;
import Inventory.System.exception.ResourceNotFoundException;
import Inventory.System.model.Inventory;
import Inventory.System.model.Room;
import Inventory.System.repository.InventoryRepository;
import Inventory.System.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;

    public List<RoomResponse> getAllRooms() { return roomRepository.findAll().stream().map(this::toResponse).toList(); }

    public RoomResponse createRoom(RoomRequest request) {
        String name = request.getName().trim();
        if (roomRepository.existsByNameIgnoreCase(name)) throw new BadRequestException("Room hii tayari ipo");
        return toResponse(roomRepository.save(Room.builder().name(name).build()));
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = findRoom(id);
        String name = request.getName().trim();
        if (roomRepository.existsByNameIgnoreCaseAndIdNot(name, id)) throw new BadRequestException("Room nyingine yenye jina hili tayari ipo");
        room.setName(name);
        return toResponse(roomRepository.save(room));
    }

    public void deleteRoom(Long id) {
        Room room = findRoom(id);
        if (allocatedFor(id) > 0) throw new BadRequestException("Room hii haiwezi kufutwa kwa sababu ina stock iliyotengwa");
        roomRepository.delete(room);
    }

    private Room findRoom(Long id) { return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room haijapatikana yenye id: " + id)); }
    private int allocatedFor(Long roomId) { return inventoryRepository.findByRoomId(roomId).stream().mapToInt(Inventory::getQuantity).sum(); }
    private RoomResponse toResponse(Room room) { return RoomResponse.builder().id(room.getId()).name(room.getName()).allocated(allocatedFor(room.getId())).build(); }
}
