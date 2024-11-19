package com.handson.ticketbot.repo;

import com.handson.ticketbot.model.NewProductsEventer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewProductsEventerRepo extends JpaRepository<NewProductsEventer, Long> {
}
