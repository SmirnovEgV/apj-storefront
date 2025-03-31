package edu.byui.apj.storefront.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<String> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getUsername() != null) {
            // User is logged in - return username with 200 OK
            return ResponseEntity.ok("Logged in as: " + userDetails.getUsername());
        } else {
            // User is not logged in - return 204 No Content
            return ResponseEntity.noContent().build();
        }
    }
}
