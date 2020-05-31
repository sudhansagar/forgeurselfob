package com.forgeurself.ob.repos;

import com.forgeurself.ob.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	@Query("from User where email=:email and password=:password")
	User login(@Param("email") String email, @Param("password") String password);

}
