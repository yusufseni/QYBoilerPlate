package com.yoesoff.plate.service;

import com.yoesoff.plate.entity.Session;
import com.yoesoff.plate.entity.User;
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

    public Optional<User> authenticate(String username, String password) {
        User user = User.find("username = ?1 and status = 'ACTIVE'", username).firstResult();
        if (user != null && BcryptUtil.matches(password, user.passwordHash)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public boolean usernameExists(String username) {
        return User.count("username = ?1", username) > 0;
    }

    public boolean emailExists(String email) {
        return User.count("email = ?1", email) > 0;
    }

    @Transactional
    public User registerClient(String username, String email, String password,
                               String firstName, String lastName, String phoneNumber) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.passwordHash = BcryptUtil.bcryptHash(password);
        user.role = UserRole.CLIENT;
        user.firstName = firstName;
        user.lastName = lastName;
        user.phoneNumber = phoneNumber;
        user.createdAt = LocalDateTime.now();
        user.persist();
        return user;
    }

    @Transactional
    public User registerFighter(String username, String email, String password,
                                String firstName, String lastName, String phoneNumber,
                                String fightName, String primaryDiscipline,
                                String weightClass, String gym) {
        User fighter = new User();
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


    // Legacy method for backward compatibility
    @Transactional
    public User register(String username, String email, String password) {
        return registerClient(username, email, password, null, null, null);
    }

    @Transactional
    public Session createSession(User user, int expiryDays) {
        Session session = new Session();
        session.user = user;
        session.token = UUID.randomUUID().toString().replace("-", "");
        session.createdAt = Instant.now();
        session.expiresAt = Instant.now().plusSeconds(expiryDays * 24 * 3600L);
        session.persist();
        return session;
    }

    public Optional<User> findUserByToken(String token) {
        Session session = Session.find("token = ?1 and expiresAt > ?2", token, Instant.now()).firstResult();
        return session != null ? Optional.of(session.user) : Optional.empty();
    }

    @Transactional
    public void deleteSession(String token) {
        Session.delete("token = ?1", token);
    }
}