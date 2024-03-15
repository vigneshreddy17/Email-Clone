package com.projects.emailclone.Service;

import com.projects.emailclone.Model.User;
import com.projects.emailclone.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserImplementation implements UserService{

    public UserRepository userRepository;

    @Autowired
    public UserImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean checkIfUserExists(String firstName) {
        Optional<User> checkUser = userRepository.findByFirstName(firstName);
        return checkUser.isPresent();
    }

    @Override
    public User deleteUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public boolean findUser(User user) {
        final String firstName = user.getFirstName();
        final String lastName = user.getLastName();
        Optional<User> searchUser = userRepository.findByFirstNameAndLastName(firstName, lastName);
        return searchUser.isPresent();
    }

    public boolean checkPassword(final String firstName, final String password) {
        Optional<User> getUser = userRepository.findByFirstName(firstName);
        return getUser.map(user -> user.getPassword().equals(password)).orElse(false);
    }
}
