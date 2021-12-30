package com.example.springbatchpractice.dao;

import com.example.springbatchpractice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
