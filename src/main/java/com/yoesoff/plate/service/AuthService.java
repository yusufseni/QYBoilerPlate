package com.yoesoff.plate.service;

import com.yoesoff.plate.entity.Session;
import com.yoesoff.plate.entity.User;
import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.Status;
import com.yoesoff.plate.enums.Themes;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.runtime.util.StringUtil;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthService {

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(User.find("username", username).firstResult());
    }

    public boolean usernameExists(String username) {
        return User.count("username", username) > 0;
    }

    public boolean emailExists(String email) {
        return User.count("email", email) > 0;
    }

    public User register(String username, String email, String plainPassword) {
        String hash = BcryptUtil.bcryptHash(plainPassword);
        User u = new User();
        u.organizationType = OrganizationType.PERSONAL;
        u.username = username;
        u.email = email;
        u.passwordHash = hash;
        u.status = Status.ACTIVE;
        u.themes = Themes.DARK;
        u.persist();
        return u;
    }

    public Optional<User> authenticate(String username, String plainPassword) {
        User u = User.find("username", username).firstResult();
        if (u == null) return Optional.empty();
        if (u.status != Status.ACTIVE) return Optional.empty();
        return BcryptUtil.matches(plainPassword, u.passwordHash) ? Optional.of(u) : Optional.empty();
    }

    public Session createSession(User user, int daysValid) {
        Session s = new Session();
        s.user = user;
        s.token = UUID.randomUUID().toString().replace("-", "");
        s.expiresAt = Instant.now().plus(daysValid, ChronoUnit.DAYS);
        s.persist();
        return s;
    }

    public Optional<User> findUserByToken(String token) {
        Session s = Session.find("token = ?1 and expiresAt > ?2", token, Instant.now()).firstResult();
        return Optional.ofNullable(s != null ? s.user : null);
    }

    public void deleteSession(String token) {
        Panache.getEntityManager().createQuery("delete from Session s where s.token = :t")
                .setParameter("t", token)
                .executeUpdate();
    }
}
