package com.example.cafe.menu.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m WHERE m.status = ON_SALE")
    List<Menu> findAllWhereOrderStatusIsOnSale();

    @Query("SELECT m FROM Menu m WHERE m.id in :ids")
    List<Menu> findAllByIds(List<Long> menuIds);
}
