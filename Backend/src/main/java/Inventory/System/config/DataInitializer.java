package Inventory.System.config;

import Inventory.System.model.Role;
import Inventory.System.model.User;
import Inventory.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inaunda akaunti ya kwanza ya ADMIN mara mfumo unapoanza kwa mara ya kwanza
 * (kama hakuna users kabisa kwenye database).
 * Hii inafanya iwezekane kuingia mfumo mara ya kwanza na kuanza kuunda users wengine.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.username}")
    private String defaultAdminUsername;

    @Value("${app.default-admin.password}")
    private String defaultAdminPassword;

    @Value("${app.default-admin.fullname}")
    private String defaultAdminFullName;

    @Override
    public void run(String... args) {
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
            System.out.println("Akaunti ya kwanza ya ADMIN imeundwa:");
            System.out.println("Username: " + defaultAdminUsername);
            System.out.println("Password haijaonyeshwa kwa usalama.");
            System.out.println("Tafadhali badilisha password ya admin mara moja!");
            System.out.println("============================================");
        }
    }
}
