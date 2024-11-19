package com.handson.ticketbot.repo;

import com.handson.ticketbot.model.NewProductGrey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewProductsGreyRepo extends JpaRepository<NewProductGrey, Long>{
    boolean existsByUrl(String url);
}
