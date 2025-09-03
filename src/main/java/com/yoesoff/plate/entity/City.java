package com.yoesoff.plate.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cities")
public class City extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false, length = 100)
    public String name;

    @Column(nullable = false, length = 100)
    public String province;

    public static City findByName(String name) {
        return find("name", name).firstResult();
    }

    public static City findByProvince(String province) {
        return find("province", province).firstResult();
    }
}
