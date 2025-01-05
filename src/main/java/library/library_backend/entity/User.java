package library.library_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true, nullable = true)
    private String email;  // Nullable if email is optional

    private String password;

    @Column(nullable = false)
    private String roles;

    @JsonBackReference
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;


    @PrePersist
    public void setDefaultRole() {
        if (this.roles == null || this.roles.isEmpty()) {
            this.roles = "USER";  // Set default role to "USER"
        }
    }
}
