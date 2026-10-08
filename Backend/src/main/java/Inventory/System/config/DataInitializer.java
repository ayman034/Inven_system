package Inventory.System.config;

import Inventory.System.model.Role;
import Inventory.System.model.User;
import Inventory.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Creates the first ADMIN account when the system starts for the first time
 * (when the database contains no users).
 * This allows the first login so additional users can be created.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.default-admin.username}")
    private String defaultAdminUsername;

    @Value("${app.default-admin.password}")
    private String defaultAdminPassword;

    @Value("${app.default-admin.fullname}")
    private String defaultAdminFullName;

    @Override
    public void run(String... args) {
        ensureItemTableColumns();
        removeUniqueItemNameIndex();
        ensureRoomTableColumns();
        removeUniqueRoomNameIndex();
        ensureInventoryTableColumns();

        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .fullName(defaultAdminFullName)
                    .username(defaultAdminUsername)
                    .password(passwordEncoder.encode(defaultAdminPassword))
                    .role(Role.ADMIN)
                    .disabled(false)
                    .build();

            userRepository.save(admin);

            System.out.println("============================================");
            System.out.println("The first ADMIN account has been created:");
            System.out.println("Username: " + defaultAdminUsername);
            System.out.println("Password haijaonyeshwa kwa usalama.");
            System.out.println("Please change the admin password immediately!");
            System.out.println("============================================");
        }
    }

    private void ensureItemTableColumns() {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'items'";
        Set<String> columns = Set.copyOf(jdbcTemplate.queryForList(query, String.class));

        if (columns.contains("item_name") && columns.contains("name")) {
            jdbcTemplate.execute("UPDATE items SET name = COALESCE(NULLIF(name, ''), item_name) WHERE item_name IS NOT NULL AND (name IS NULL OR name = '')");
            jdbcTemplate.execute("ALTER TABLE items DROP COLUMN item_name");
        } else if (columns.contains("item_name") && !columns.contains("name")) {
            jdbcTemplate.execute("ALTER TABLE items CHANGE item_name name VARCHAR(255) NOT NULL");
        }

        if (columns.contains("item_category") && !columns.contains("category")) {
            jdbcTemplate.execute("ALTER TABLE items CHANGE item_category category VARCHAR(255) NOT NULL");
        }

        if (columns.contains("total_quantity") && columns.contains("quantity")) {
            jdbcTemplate.execute("UPDATE items SET quantity = COALESCE(quantity, total_quantity) WHERE total_quantity IS NOT NULL AND (quantity IS NULL OR quantity = 0)");
            jdbcTemplate.execute("ALTER TABLE items DROP COLUMN total_quantity");
        } else if (columns.contains("total_quantity") && !columns.contains("quantity")) {
            jdbcTemplate.execute("ALTER TABLE items CHANGE total_quantity quantity INT NOT NULL");
        }
    }

    private void removeUniqueItemNameIndex() {
        String query = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'items' AND NON_UNIQUE = 0 "
                + "GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'name' "
                + "AND INDEX_NAME <> 'PRIMARY'";

        for (String indexName : jdbcTemplate.queryForList(query, String.class)) {
            String escapedIndexName = indexName.replace("`", "``");
            jdbcTemplate.execute("ALTER TABLE items DROP INDEX `" + escapedIndexName + "`");
        }
    }

    private void ensureRoomTableColumns() {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'rooms'";
        Set<String> columns = Set.copyOf(jdbcTemplate.queryForList(query, String.class));

        if (columns.contains("room_name") && columns.contains("name")) {
            jdbcTemplate.execute("UPDATE rooms SET name = COALESCE(NULLIF(name, ''), room_name) WHERE room_name IS NOT NULL AND (name IS NULL OR name = '')");
            jdbcTemplate.execute("ALTER TABLE rooms DROP COLUMN room_name");
        } else if (columns.contains("room_name") && !columns.contains("name")) {
            jdbcTemplate.execute("ALTER TABLE rooms CHANGE room_name name VARCHAR(255) NOT NULL");
        }
    }

    private void removeUniqueRoomNameIndex() {
        String query = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'rooms' AND NON_UNIQUE = 0 "
                + "GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'name' "
                + "AND INDEX_NAME <> 'PRIMARY'";

        for (String indexName : jdbcTemplate.queryForList(query, String.class)) {
            String escapedIndexName = indexName.replace("`", "``");
            jdbcTemplate.execute("ALTER TABLE rooms DROP INDEX `" + escapedIndexName + "`");
        }
    }

    private void ensureInventoryTableColumns() {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'inventory'";
        Set<String> columns = Set.copyOf(jdbcTemplate.queryForList(query, String.class));

        if (columns.contains("allocated_quantity") && columns.contains("quantity")) {
            jdbcTemplate.execute("UPDATE inventory SET quantity = COALESCE(quantity, allocated_quantity) WHERE allocated_quantity IS NOT NULL AND (quantity IS NULL OR quantity = 0)");
            jdbcTemplate.execute("ALTER TABLE inventory DROP COLUMN allocated_quantity");
        } else if (columns.contains("allocated_quantity") && !columns.contains("quantity")) {
            jdbcTemplate.execute("ALTER TABLE inventory CHANGE allocated_quantity quantity INT NOT NULL");
        }
    }
}
