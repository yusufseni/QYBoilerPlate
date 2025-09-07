package com.yoesoff.plate.service;

import com.yoesoff.plate.entity.SessionEntity;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import io.quarkus.elytron.security.common.BcryptUtil;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    public Optional<UserEntity> authenticate(String username, String password) {
        UserEntity userEntity = UserEntity.find("username = ?1 and status = 'ACTIVE'", username).firstResult();
        if (userEntity != null && BcryptUtil.matches(password, userEntity.passwordHash)) {
            return Optional.of(userEntity);
        }
        return Optional.empty();
    }

    public boolean usernameExists(String username) {
        return UserEntity.count("username = ?1", username) > 0;
    }

    public boolean emailExists(String email) {
        return UserEntity.count("email = ?1", email) > 0;
    }

    @Transactional
    public UserEntity registerClient(OrganizationType organizationType, String username, String email, String password,
                                     String firstName, String lastName, String phoneNumber) {
        UserEntity userEntity = new UserEntity();
        userEntity.organizationType = organizationType != null ? organizationType : OrganizationType.PERSONAL;
        userEntity.username = username;
        userEntity.email = email;
        userEntity.passwordHash = BcryptUtil.bcryptHash(password);
        userEntity.role = UserRole.CLIENT;
        userEntity.firstName = firstName;
        userEntity.lastName = lastName;
        userEntity.phoneNumber = phoneNumber;
        userEntity.createdAt = LocalDateTime.now();
        userEntity.persist();
        return userEntity;
    }

    @Transactional
    public UserEntity registerFighter(String username, String email, String password,
                                      String firstName, String lastName, String phoneNumber,
                                      String fightName, String primaryDiscipline,
                                      String weightClass, String gym) {
        UserEntity fighter = new UserEntity();
        fighter.username = username;
        fighter.email = email;
        fighter.passwordHash = BcryptUtil.bcryptHash(password); // Use BcryptUtil
        fighter.role = UserRole.FIGHTER;
        fighter.firstName = firstName;
        fighter.lastName = lastName;
        fighter.phoneNumber = phoneNumber;
        fighter.fightName = fightName;
        fighter.primaryDiscipline = primaryDiscipline;
        fighter.weightClass = weightClass;
        fighter.gym = gym;
        fighter.createdAt = LocalDateTime.now();
        fighter.persist();
        return fighter;
    }

    @Transactional
    public SessionEntity createSession(UserEntity userEntity, int expiryDays) {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.userEntity = userEntity;
        sessionEntity.token = UUID.randomUUID().toString().replace("-", "");
        sessionEntity.createdAt = Instant.now();
        sessionEntity.expiresAt = Instant.now().plusSeconds(expiryDays * 24 * 3600L);
        sessionEntity.persist();
        return sessionEntity;
    }

    public Optional<UserEntity> findUserByToken(String token) {
        SessionEntity sessionEntity = SessionEntity.find("token = ?1 and expiresAt > ?2", token, Instant.now()).firstResult();
        return sessionEntity != null ? Optional.of(sessionEntity.userEntity) : Optional.empty();
    }

    @Transactional
    public void deleteSession(String token) {
        SessionEntity.delete("token = ?1", token);
    }
}