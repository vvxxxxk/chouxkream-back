package com.kream.chouxkream.user.repository;

import com.kream.chouxkream.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

}
