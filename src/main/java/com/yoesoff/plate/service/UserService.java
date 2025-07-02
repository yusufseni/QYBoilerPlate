package com.yoesoff.plate.service;

import com.yoesoff.plate.entity.User;
import com.yoesoff.plate.dto.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    public List<UserDTO> listAll() {
        return User.listAll().stream()
                .map(entity -> toDTO((User) entity))
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
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
    public UserDTO update(Long id, UserDTO dto) {
        User user = User.findById(id);
        if (user == null) return null;
        user.username = dto.username;
        user.email = dto.email;
        return toDTO(user);
    }

    @Transactional
    public boolean delete(Long id) {
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