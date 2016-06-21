package com.petfinder.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.petfinder.domain.User;

import org.springframework.data.repository.query.Param;

import java.util.List;

import javax.transaction.Transactional;

public interface UserRepository extends CrudRepository<User, Long>{

    @Transactional
	User findOneByLogin(@Param("login") String login);

    @Transactional
	User findOneByEmail(@Param("email") String email);

    @Transactional
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.login=:login")
	Boolean existsByLogin(@Param("login") String login);

    @Transactional
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.email=:email")
    Boolean existsByEmail(@Param("email") String email);
    
    @Transactional
	@Query("SELECT u FROM User u WHERE u.emailNotification=true")
	List<User> getUsersToNotify();
}
