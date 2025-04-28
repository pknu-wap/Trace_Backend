package com.example.trace.auth.repository;

import com.example.trace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderId(String providerId);
    Optional<User> findByProviderIdAndProvider(String providerId, String provider);
}