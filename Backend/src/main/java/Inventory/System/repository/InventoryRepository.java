package Inventory.System.repository;

import Inventory.System.model.Inventory;
import Inventory.System.model.Item;
import Inventory.System.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByItem(Item item);
    List<Inventory> findByItemId(Long itemId);
    List<Inventory> findByRoomId(Long roomId);
    Optional<Inventory> findByItemAndRoom(Item item, Room room);
}
