package com.example.demo1.repository;

import com.example.demo1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String username);

    @Query("SELECT u FROM User u WHERE u.verificationCode = :code")
    public User findByVerificationCode(@Param("code") String code);
}
