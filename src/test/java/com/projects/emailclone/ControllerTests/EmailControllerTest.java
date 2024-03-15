package com.projects.emailclone.ControllerTests;

import com.projects.emailclone.Controller.EmailController;
import com.projects.emailclone.Model.User;
import com.projects.emailclone.Service.UserImplementation;
import com.projects.emailclone.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmailControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserImplementation userImplementation;
    @InjectMocks
    private EmailController emailController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistration_Success() {
        User user = new User();
        when(userService.findUser(user)).thenReturn(false);
        ResponseEntity<String> response = emailController.registerUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration Successful!!", response.getBody());
    }

    @Test
    void testRegistration_Failed() {
        User user = new User();
        when(userService.findUser(user)).thenReturn(true);
        ResponseEntity<String> response = emailController.registerUser(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists!!", response.getBody());
    }

    @Test
    void userLogin_Success() {
        User user = new User();
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(true);
        when(userImplementation.checkPassword(user.getFirstName(), user.getPassword())).thenReturn(true);
        ResponseEntity<String> response = emailController.loginUser(user.getFirstName(), user.getPassword());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), "Login Successfully");
    }

    @Test
    void userLogin_User_NotFound() {
        User user = new User();
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(false);
        ResponseEntity<String> response = emailController.loginUser(user.getFirstName(), user.getPassword());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(response.getBody(), "User Not found");
    }

    @Test
    void userLogin_User_InCorrect_Password() {
        User user = new User();
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(true);
        when(userImplementation.checkPassword(user.getFirstName(), user.getPassword())).thenReturn(false);
        ResponseEntity<String> response = emailController.loginUser(user.getFirstName(), user.getPassword());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(response.getBody(), "Incorrect Password");
    }
}
