package com.projects.emailclone.Controller;

import com.projects.emailclone.Service.UserImplementation;
import com.projects.emailclone.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projects.emailclone.Model.User;

import java.util.Scanner;


@RestController
public class EmailController {
    public UserService userService;
    public UserImplementation userImplementation;
    final boolean isLoggedIn = false;
    HttpSession userSession = null;

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
    public String loginUser(@RequestParam("firstName") String firstName, @RequestParam("password") String password,
                HttpServletRequest request) {
        final boolean result = userImplementation.checkIfUserExists(firstName);
        if (result) {
            final boolean passwordChecker = userImplementation.checkPassword(firstName, password);
            if (passwordChecker) {
                userSession = request.getSession(true);
                userSession.setAttribute("firstName", firstName);
                return showOptions(userSession);
            }
            else
                return "Incorrect Password!!";
        }
        return "User not found";
    }

    @GetMapping("/show-options")
    public String showOptions(final HttpSession session) {
        String firstNameFromSession = (String) session.getAttribute("firstName");
        return "Login Successful, Welcome " + firstNameFromSession
                +"\n1) Show Messages\n2) Send Messages\n3) Log-out";
    }

    @PostMapping("/selected-option")
    public ResponseEntity<String> directToSelectedOption(@RequestParam("option") int option) {
        try {
            if (!userSession.isNew() && userSession != null) {
                return switch (option) {
                    case 1 -> ResponseEntity.status(HttpStatus.OK).body("Show Messages");
                    case 2 -> ResponseEntity.status(HttpStatus.OK).body("Send Messages");
                    case 3 -> ResponseEntity.status(HttpStatus.OK).body("Log out");
                    default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Option Selected");
                };
            }
        }
        catch (Exception exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid Option Selected");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Option Selected");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            if (!userSession.isNew()) {
                String firstName = (String) userSession.getAttribute("firstName");
                userSession.invalidate();
                return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully, Bye " + firstName);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request!!");
        }
        catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }



}
