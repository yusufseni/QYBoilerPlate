package com.yoesoff.plate.service;

import com.yoesoff.plate.entity.User;
import com.yoesoff.plate.dto.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import com.yoesoff.plate.repository.UserRepository;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<UserDTO> listAllPaged(int page, int size) {
        // Example using a repository or DAO with pagination support
        int pageIndex = Math.max(page - 1, 0); // Adjusting page index to be zero-based
        int offset = pageIndex * size;
        return this.userRepository.findPaged(offset, size)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO findById(UUID id) {
        User user = User.findById(id);
        return user != null ? toDTO(user) : null;
    }

    @Transactional
    public UserDTO create(UserDTO dto) {
        User user = new User(dto.username, "default", dto.email);
        user.persist();
        return toDTO(user);
    }

    @Transactional
    public UserDTO update(UUID id, UserDTO dto) {
        User user = User.findById(id);
        if (user == null) return null;
        user.username = dto.username;
        user.email = dto.email;
        return toDTO(user);
    }

    @Transactional
    public boolean delete(UUID id) {
        return User.deleteById(id);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.id;
        dto.username = user.username;
        dto.email = user.email;
        return dto;
    }
}