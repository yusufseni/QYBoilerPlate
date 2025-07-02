package com.yoesoff.plate.repository;

import com.yoesoff.plate.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public List<User> findPaged(int offset, int size) {
        int page = offset / size;
        return findAll().page(Page.of(page, size)).list();
    }
}