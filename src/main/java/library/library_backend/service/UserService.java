package library.library_backend.service;

import jakarta.persistence.EntityNotFoundException;
import library.library_backend.entity.Transaction;
import library.library_backend.entity.User;
import library.library_backend.repository.TransactionRepository;
import library.library_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers() {
        // Logic to fetch all users
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        // Logic to fetch a user by ID
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User updateUser(Long id, User updatedUser) {
        // Logic to update user details
        User existingUser = getUserById(id);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword()); // Set password directly without encoding
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        // Logic to delete a user
        List<Transaction> transactions = transactionRepository.findByUserId(id);
        transactionRepository.deleteAll(transactions);  // Delete all transactions for the user

        // Then delete the user
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
