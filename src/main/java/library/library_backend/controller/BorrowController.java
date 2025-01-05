package library.library_backend.controller;

import library.library_backend.entity.Book;
import library.library_backend.entity.Borrow;
import library.library_backend.entity.Purchase;
import library.library_backend.entity.User;
import library.library_backend.service.BookService;
import library.library_backend.service.BorrowService;
import library.library_backend.service.UserService;
import library.library_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Object> borrowRequest, @RequestHeader("Authorization") String authorizationHeader) {
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
            Long bookId = Long.parseLong(borrowRequest.get("bookId").toString());  // Extract the bookId
            LocalDate dueDate = LocalDate.parse(borrowRequest.get("dueDate").toString());

            Borrow borrow = borrowService.borrowBook(bookId,dueDate,username);
            return ResponseEntity.ok(borrow);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @GetMapping("/{bookId}/is-borrowed")
    public ResponseEntity<?> isBookBorrowed(@PathVariable Long bookId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract username from token
            String username = JwtUtil.extractUsername(authorizationHeader.substring(7));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token.");
            }

            // Check if the book is already borrowed by the user
            boolean isBorrowed = borrowService.isBookAlreadyBorrowed(username, bookId);

            return ResponseEntity.ok(isBorrowed); // Return true if borrowed, false otherwise
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        }
    }
    @GetMapping("/users")
    public ResponseEntity<?> getBorrowsByUser(@RequestHeader("Authorization") String authorizationHeader) {
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
            List<Borrow> borrows = borrowService.getBorrowsByUser(user);

            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
    @GetMapping
    public ResponseEntity<List<Borrow>> getBorrows() {
        List<Borrow> borrows = borrowService.getAllBorrows();
        return ResponseEntity.ok(borrows);
    }
    @PutMapping("/return/{borrowId}")
    public ResponseEntity<?> returnBook(@PathVariable Long borrowId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Check if the Authorization header is present and properly formatted
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Token is missing or invalid.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Extract token by removing the "Bearer " prefix
            String token = authorizationHeader.substring(7);
            System.out.println("Token: " + token);

            // Extract username from the token
            String username = JwtUtil.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (username == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid token.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Retrieve the Borrow entity using the borrowId
            Borrow borrow = borrowService.getBorrowById(borrowId);
            if (borrow == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Borrow record not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            System.out.println("role"+ user.getRoles());
            // Check if the username matches the one in the borrow record
            if (!borrow.getUser().getUsername().equals(username) && !Objects.equals(user.getRoles(), "ADMIN")) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "You are not authorized to return this book.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Book book = borrow.getBook();
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookService.saveBook(book);
            // Update the actual return date of the book
            borrow.setActualReturnDate(LocalDate.now());  // Set the actual return date to today's date
            borrowService.save(borrow);  // Save the updated borrow record

            Map<String, String> response = new HashMap<>();
            response.put("message", "Book returned successfully.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "An error occurred while returning the book.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
