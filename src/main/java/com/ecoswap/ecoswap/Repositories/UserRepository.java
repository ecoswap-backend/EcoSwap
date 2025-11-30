package com.ecoswap.ecoswap.Repositories;

import com.ecoswap.ecoswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Necesario para el registro y login
    User findByMail(String mail);
}
