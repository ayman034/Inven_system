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
 * Creates the first ADMIN account when the system starts for the first time
 * (when the database contains no users).
 * This allows the first login so additional users can be created.
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
            System.out.println("The first ADMIN account has been created:");
            System.out.println("Username: " + defaultAdminUsername);
            System.out.println("Password haijaonyeshwa kwa usalama.");
            System.out.println("Please change the admin password immediately!");
            System.out.println("============================================");
        }
    }
}
