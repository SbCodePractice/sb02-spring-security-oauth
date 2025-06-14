package com.sb02.practice.security.repository;

import com.sb02.practice.security.entity.Provider;
import com.sb02.practice.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderIdAndProvider(String providerId, Provider provider);

    boolean existsByEmail(String email);

    boolean existsByProviderIdAndProvider(String providerId, Provider provider);
}