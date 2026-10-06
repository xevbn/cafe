package com.example.cafe.menu.model;

import com.example.cafe.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuStatus status;

    public Menu(String name, int price) {
        this.name = name;
        this.price = price;
        this.status = MenuStatus.ON_SALE;
    }

    public static Menu create(String name, int price) {
        return new Menu(name, price);
    }

    public void changeStatus(MenuStatus status) {
        this.status = status;
    }
}
