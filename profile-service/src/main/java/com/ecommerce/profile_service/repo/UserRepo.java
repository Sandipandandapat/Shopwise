package com.ecommerce.profile_service.repo;

import com.ecommerce.profile_service.dto.UserResDto;
import com.ecommerce.profile_service.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    User findByEmail(String email);
}
