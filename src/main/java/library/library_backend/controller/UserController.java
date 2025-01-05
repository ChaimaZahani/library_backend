package library.library_backend.controller;

import library.library_backend.entity.User;
import library.library_backend.repository.UserRepository;
import library.library_backend.service.UserService;
import library.library_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private  UserService userService;
    @Autowired
    private  UserRepository userRepository;



    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername())!= null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail())!= null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
    // Fetch user from the repository based on the provided username
    User existingUser = userRepository.findByUsername(user.getUsername());

    // Check if the user exists and the passwords match
        if (existingUser != null && new BCryptPasswordEncoder().matches(user.getPassword(), existingUser.getPassword())) {
        // Generate the JWT token
        String token = JwtUtil.generateToken(user.getUsername());

        // Set the token in the cookies (with HttpOnly and Secure flags)
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true) // Prevents client-side access to the cookie
                .secure(true)   // Ensures the cookie is sent over HTTPS only
                .maxAge(86400)  // 1 day
                .build();

        // Prepare the response body with a map
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        existingUser.setPassword(null);
        response.put("user", existingUser);
        // Return the response with the cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // Add cookie header
                .body(response); // Return a JSON response with a message and token
    }

    // If credentials are invalid, return an unauthorized response
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("message", "Invalid credentials");

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse);
}
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Clear the token cookie
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/logout")      // Make it available for the entire domain
                .maxAge(0)      // Expire the cookie
                .build();

        // Prepare the response body
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");

        // Return the response with the expired cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // Expire the cookie
                .body(response);
    }

    // Validate token endpoint
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        if (library.library_backend.util.JwtUtil.validateToken(token)) {
            String username = library.library_backend.util.JwtUtil.extractUsername(token);
            return ResponseEntity.ok("Token is valid for user: " + username);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        User users = userRepository.save(user);
        return ResponseEntity.ok(users);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
