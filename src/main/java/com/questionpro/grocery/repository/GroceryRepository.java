package com.questionpro.grocery.repository;


import com.questionpro.grocery.entity.Grocery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryRepository extends JpaRepository<Grocery, Long> {
    Grocery findByName(String name);

}