package com.prs.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prs.business.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
