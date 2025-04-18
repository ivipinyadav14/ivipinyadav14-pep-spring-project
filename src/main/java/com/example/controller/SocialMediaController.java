package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

import java.util.Optional;
import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    //process new User registrations
    @PostMapping("/register")
    public Object register(@RequestBody Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (accountService.findByUsername(account.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Account savedAccount = accountService.register(account);
        return savedAccount;
    }


    //process User logins
    @PostMapping("/login")
    public Object login(@RequestBody Account account) {
        Optional<Account> validAccount = accountService.login(account.getUsername(), account.getPassword());

        if (validAccount.isPresent()) {
            return validAccount.get();
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    //creation of new messages
    @PostMapping("/messages")
    public Object createMessage(@RequestBody Message message) {
        if (message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Message savedMessage = messageService.createMessage(message);
        return savedMessage;
    }


    //retrieve all messages
    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }


    //retrieve a message by its ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Object> getMessageById(@PathVariable Integer messageId) {
        return messageService.getMessageById(messageId).<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok().build());
    }


    //delete a message identified by a message ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Object> deleteMessage(@PathVariable Integer messageId) {
        boolean deleted = messageService.deleteMessageById(messageId);
        if(deleted)
            return ResponseEntity.ok(1);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    //retrieve all messages written by a particular user
    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getMessagesByUser(@PathVariable Integer accountId) {
        return messageService.getMessagesByAccountId(accountId);
    }


    //update a message text identified by a message ID
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Object> updateMessage(@PathVariable Integer messageId, @RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean updated = messageService.updateMessageText(messageId, message.getMessageText());

        if(updated) {
            return ResponseEntity.ok(1);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
