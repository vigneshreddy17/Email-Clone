package com.projects.emailclone.ControllerTests;

import com.projects.emailclone.Controller.EmailController;
import com.projects.emailclone.Model.MessageModel;
import com.projects.emailclone.Model.User;
import com.projects.emailclone.Service.MessageService;
import com.projects.emailclone.Service.UserImplementation;
import com.projects.emailclone.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmailControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserImplementation userImplementation;
    @Mock
    private MessageService messageService;

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
    @DirtiesContext
    void testRegistration_Success() {
        User user = new User();
        when(userService.findUser(user)).thenReturn(false);
        ResponseEntity<String> response = emailController.registerUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration Successful!!", response.getBody());
    }
    @Test
    @Disabled
    void testRegistration_Failure_UserAlreadyExists() {
        User existingUser = new User();
        when(userService.findUser(existingUser)).thenReturn(true);
        ResponseEntity<String> response = emailController.registerUser(existingUser);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists!!", response.getBody());
        verify(userService).findUser(existingUser);
    }


    @Test
    @DirtiesContext
    void userLogin_Success() {
        User user = new User();
        String result = "Login Successful, Welcome " + null + "\n1) Show Messages\n2) Send Messages\n3) Log-out";
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(true);
        when(userImplementation.checkPassword(user.getFirstName(), user.getPassword())).thenReturn(true);
        String response = emailController.loginUser(user.getFirstName(), user.getPassword(), request);
        assertEquals(result, response);
    }

    @Test
    @DirtiesContext
    void userLogin_User_NotFound() {
        User user = new User();
        String result = "User not found";
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(false);
        String response = emailController.loginUser(user.getFirstName(), user.getPassword(), request);
        assertEquals(result, response);
    }

    @Test
    @DirtiesContext
    void userLogin_User_InCorrect_Password() {
        User user = new User();
        String result = "Incorrect Password!!";
        when(userImplementation.checkIfUserExists(user.getFirstName())).thenReturn(true);
        when(userImplementation.checkPassword(user.getFirstName(), user.getPassword())).thenReturn(false);
        String response = emailController.loginUser(user.getFirstName(), user.getPassword(), request);
        assertEquals(result, response);
    }

    @Test
    @DirtiesContext
    void directToSelectedOption() {
        HttpSession session = mock(HttpSession.class);
        when(session.isNew()).thenReturn(false);

        ResponseEntity<String> response1 = emailController.directToSelectedOption(1);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals("Show Messages", response1.getBody());

        ResponseEntity<String> response2 = emailController.directToSelectedOption(2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Send Messages", response2.getBody());
    }

    @Test
    @DirtiesContext
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

    @Test
    void testSendMessage() throws Exception {
        HttpSession session = mock(HttpSession.class);
        String firstName = "mock";
        when(session.getAttribute("firstName")).thenReturn(firstName);
        doNothing().when(messageService).sendMessage(anyString(), anyString(), anyString());
        EmailController spy = spy(emailController);
        ResponseEntity<String> response = invokePrivateMethod(spy,
                new MessageModel("recipient", "message", firstName, false));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Message Sent!!", response.getBody());
    }

    private <T> T invokePrivateMethod(Object object, Object... args) throws Exception {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Method method = object.getClass().getDeclaredMethod("sendMessage", argTypes);
        method.setAccessible(true);
        return (T) method.invoke(object, args);
    }


}
