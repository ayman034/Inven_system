package Inventory.System.service;

import Inventory.System.dto.UserRequest;
import Inventory.System.dto.UserResponse;
import Inventory.System.dto.UserUpdateRequest;
import Inventory.System.exception.BadRequestException;
import Inventory.System.exception.ResourceNotFoundException;
import Inventory.System.model.User;
import Inventory.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new BadRequestException("Username hii tayari inatumika");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .disabled(false)
                .build();

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mtumiaji hajapatikana"));

        user.setFullName(request.getFullName());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public UserResponse toggleUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mtumiaji hajapatikana"));

        user.setDisabled(!user.isDisabled());
        return UserResponse.fromEntity(userRepository.save(user));
    }
}
