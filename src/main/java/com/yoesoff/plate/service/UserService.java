package com.yoesoff.plate.service;

import com.yoesoff.plate.entity.UserEntity;
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
        UserEntity userEntity = UserEntity.findById(id);
        return userEntity != null ? toDTO(userEntity) : null;
    }

    @Transactional
    public UserDTO create(UserDTO dto) {
        UserEntity userEntity = new UserEntity(dto.username, "default", dto.email);
        userEntity.persist();
        return toDTO(userEntity);
    }

    @Transactional
    public UserDTO update(UUID id, UserDTO dto) {
        UserEntity userEntity = UserEntity.findById(id);
        if (userEntity == null) return null;
        userEntity.username = dto.username;
        userEntity.email = dto.email;
        return toDTO(userEntity);
    }

    @Transactional
    public boolean delete(UUID id) {
        return UserEntity.deleteById(id);
    }

    private UserDTO toDTO(UserEntity userEntity) {
        UserDTO dto = new UserDTO();
        dto.id = userEntity.id;
        dto.username = userEntity.username;
        dto.email = userEntity.email;
        return dto;
    }
}