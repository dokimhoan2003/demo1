package com.example.demo1.repository;

import com.example.demo1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String username);

    @Query("SELECT u FROM User u WHERE u.verificationCode = :code")
    public User findByVerificationCode(@Param("code") String code);

    @Query("SELECT u FROM User u WHERE u.token = :token")
    public User findByToken(@Param("token") String token);

    public Page<User> findAll(Pageable pageable);


}
