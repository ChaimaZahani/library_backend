package library.library_backend.controller;


import library.library_backend.entity.Purchase;

import library.library_backend.entity.User;
import library.library_backend.service.PurchaseService;
import library.library_backend.service.UserService;
import library.library_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    @RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private UserService userService;


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> purchaseBook(@RequestBody Map<String, Object> purchase, @RequestHeader("Authorization") String authorizationHeader) {

        // Check if the Authorization header is present and properly formatted
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token is missing or invalid.");
        }

        // Extract token by removing the "Bearer " prefix
        String token = authorizationHeader.substring(7);

        // Extract username from the token
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token.");
        }

        // Save the purchase using the service layer
        Purchase savedPurchase = purchaseService.purchaseBook(purchase,username);

        // Return the saved purchase along with a status code of CREATED
        return new ResponseEntity<>(savedPurchase, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getPurchases() {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getPurchasesByUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Check if the Authorization header is present and properly formatted
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token is missing or invalid.");
            }

            // Extract token by removing the "Bearer " prefix
            String token = authorizationHeader.substring(7);
            System.out.println("Token: " + token);

            // Extract username from the token
            String username = JwtUtil.extractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token.");
            }

            // Fetch user by username
            User user = new User();
            user = userService.getUserByUsername(username);// Assuming you have a username field
            List<Purchase> purchases = purchaseService.getPurchasesByUser(user);

            return ResponseEntity.ok(purchases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }




}
