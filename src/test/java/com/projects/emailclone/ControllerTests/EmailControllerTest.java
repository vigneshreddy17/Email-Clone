package com.projects.emailclone.ControllerTests;

import com.projects.emailclone.Controller.EmailController;
import com.projects.emailclone.Model.User;
import com.projects.emailclone.Service.UserImplementation;
import com.projects.emailclone.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmailControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserImplementation userImplementation;
    @Mock
    private HttpSession httpSession;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private EmailController emailController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(request.getSession(true)).thenReturn(httpSession);
        HttpSession session = mock(HttpSession.class);
        try {
            final Field field = EmailController.class.getDeclaredField("userSession");
            field.setAccessible(true);
            field.set(emailController, session);
            session.setAttribute("firstName", "Mock");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
        String result = "Login Successful, Welcome " + null + "\n1) Show Messages\n2) Send Messages\n3) Log-out";
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(true);
        when(userImplementation.checkPassword(user.getFirstName(), user.getPassword())).thenReturn(true);
        String response = emailController.loginUser(user.getFirstName(), user.getPassword(), request);
        assertEquals(result, response);
    }

    @Test
    void userLogin_User_NotFound() {
        User user = new User();
        String result = "User not found";
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(false);
        String response = emailController.loginUser(user.getFirstName(), user.getPassword(), request);
        assertEquals(result, response);
    }

    @Test
    void userLogin_User_InCorrect_Password() {
        User user = new User();
        String result = "Incorrect Password!!";
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(true);
        when(userImplementation.checkPassword(user.getFirstName(), user.getPassword())).thenReturn(false);
        String response = emailController.loginUser(user.getFirstName(), user.getPassword(), request);
        assertEquals(result, response);
    }

    @Test
    void directToSelectedOption() {
        HttpSession session = mock(HttpSession.class);
        when(session.isNew()).thenReturn(false);

        ResponseEntity<String> response1 = emailController.directToSelectedOption(1);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals("Show Messages", response1.getBody());

        ResponseEntity<String> response2 = emailController.directToSelectedOption(2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Send Messages", response2.getBody());

        ResponseEntity<String> response3 = emailController.directToSelectedOption(3);
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertEquals("Log out", response3.getBody());
    }

    @Test
    void testLogout() {
        HttpSession session = mock(HttpSession.class);
        when(session.isNew()).thenReturn(false);

        String firstName = "Mock";
        when(session.getAttribute("firstName")).thenReturn(firstName);

        try {
            final Field field = EmailController.class.getDeclaredField("userSession");
            field.setAccessible(true);
            field.set(emailController, session);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> response = emailController.logout();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String name = String.valueOf(session.getAttribute("firstName"));
        assertEquals("Logged out successfully, Bye " + name, response.getBody());
    }


}
