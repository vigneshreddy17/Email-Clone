package com.projects.emailclone.Repository;

import com.projects.emailclone.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT u FROM User u WHERE u.firstName = :firstName")
    Optional<User> findByFirstName(String firstName);

    @Override
    List<User> findAll();
}
