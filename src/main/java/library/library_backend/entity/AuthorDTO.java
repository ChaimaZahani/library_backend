package library.library_backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;


@Data
@Embeddable
public class AuthorDTO {
    // Getters and setters
    @Column(name = "author_key") // Updated column name
    private String key; // Author key
    private String name; // Author name

}
