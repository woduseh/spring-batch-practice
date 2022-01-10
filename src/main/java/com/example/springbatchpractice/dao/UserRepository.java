package com.example.springbatchpractice.dao;

import com.example.springbatchpractice.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findAllByDeleteRes(LocalDate deleteRes);
}
