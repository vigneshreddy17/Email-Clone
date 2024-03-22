package com.projects.emailclone.Controller;

import com.projects.emailclone.Model.MessageModel;
import com.projects.emailclone.Service.MessageService;
import com.projects.emailclone.Service.MessageServiceImplementation;
import com.projects.emailclone.Service.UserImplementation;
import com.projects.emailclone.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projects.emailclone.Model.User;

import java.util.*;


@RestController
public class EmailController {
    private final UserService userService;
    private final UserImplementation userImplementation;
    private final MessageService messageService;
    HttpSession userSession = null;

    @Autowired
    public EmailController(final UserService userService, final UserImplementation userImplementation,
                           final MessageService messageService, final MessageServiceImplementation messageServiceImplementation) {
        this.userService = userService;
        this.userImplementation = userImplementation;
        this.messageService = messageService;
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
                    case 3 -> logout();
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

    @PostMapping("send-message")
    private ResponseEntity<String> sendMessage( @RequestBody MessageModel messageModel) {
        try {
            final String sender = (String) userSession.getAttribute("firstName");
            messageService.sendMessage(messageModel.getRecipient(), messageModel.getMessage(), sender);
            return ResponseEntity.status(HttpStatus.OK).body("Message Sent!!");
        }
        catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @GetMapping("/read-messages")
    private ResponseEntity<?> readMessages() {
        try {
            final String user = (String) userSession.getAttribute("firstName");
            final Optional<List<Object[]>> messages = messageService.readMessages(user);
            if (messages.isPresent()) {
                messageService.markAsRead(user);
                final List<Map<String, String>> responseData = new ArrayList<>();
                for (Object[] message : messages.get()) {
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("sender", (String) message[0]);
                    messageMap.put("message", (String) message[1]);
                    responseData.add(messageMap);
                }
                return ResponseEntity.ok(responseData);
            }
            else {
                return ResponseEntity.status(HttpStatus.OK).body("No messages to display!!");
            }
        }
        catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while reading messages");
        }
    }

    /* When User sends message, create a boolean flag i.e isRead which marks the flag as false
    * When read-message API is called mark all the flag true
    * TO-DO: Add the flag in sendMessage API and modify the read-message API
    * Add timestamp of when the message was sent in the send-message API
    * */

    @GetMapping("read-unread-messages")
    private ResponseEntity<?> readUnreadMessages() {
        try {
            final String user = (String) userSession.getAttribute("firstName");
            final Optional<List<Object[]>> unreadMessages = messageService.readUnreadMessages(user);
            if (unreadMessages.isPresent()) {
                messageService.markAsRead(user);
                final List<Map<String, String>> response = new ArrayList<>();
                for (Object[] message : unreadMessages.get()) {
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("sender", (String) message[0]);
                    messageMap.put("message", (String) message[1]);
                    response.add(messageMap);
                }
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Collections.emptyList());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while reading messages");
        }
    }



}
