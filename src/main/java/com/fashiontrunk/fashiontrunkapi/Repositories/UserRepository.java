package com.fashiontrunk.fashiontrunkapi.Repositories;

import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}