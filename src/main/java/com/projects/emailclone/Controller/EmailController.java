package com.projects.emailclone.Controller;

import com.projects.emailclone.Service.UserImplementation;
import com.projects.emailclone.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.projects.emailclone.Model.User;


@RestController
public class EmailController {
    public UserService userService;
    public UserImplementation userImplementation;

    @Autowired
    public EmailController(UserService userService, UserImplementation userImplementation) {
        this.userService = userService;
        this.userImplementation = userImplementation;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User request) {
        if (userService.findUser(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!!");
        }
        try {
            userService.saveUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration Successful!!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam("firstName") String firstName, @RequestParam("password") String password) {
        final boolean result = userImplementation.checkIfUserExists(firstName);
        if (result) {
            final boolean passwordChecker = userImplementation.checkPassword(firstName, password);
            if (passwordChecker)
                return ResponseEntity.status(HttpStatus.OK).body("Login Successfully");
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect Password");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not found");
    }

}
