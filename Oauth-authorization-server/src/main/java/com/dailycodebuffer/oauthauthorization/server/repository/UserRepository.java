package com.dailycodebuffer.oauthauthorization.server.repository;

import com.dailycodebuffer.oauthauthorization.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);
}
