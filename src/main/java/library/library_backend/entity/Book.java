package library.library_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import library.library_backend.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String archiveId;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_categories", // You can remove this if you don't want a join table
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonManagedReference
    private List<Category> categories = new ArrayList<>();
    private String coverId;
    private String title;
    private int firstPublishYear;
    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_name")
    private List<AuthorDTO> authors;
    private String coverEditionKey; // Cover edition key
    private String availabilityStatus;
    @Column(name = "description", columnDefinition = "TEXT")
    private String Description ;
    private String ImageUrl;
    private BigDecimal price;
    private int availableQuantity;
    @JsonBackReference
    @OneToMany(mappedBy = "book")
    private List<Transaction> transactions;
    @Column(nullable = false)
    private boolean deleted = false;  // Added deleted flag to mark the book as deleted


    // Constructor to create a book with required fields
    public Book(String title, String archiveId, String coverId, String coverEditionKey,
                String availabilityStatus,
                int firstPublishYear, List<AuthorDTO> authors, String Description) {
        this.title = title;
        this.archiveId = archiveId;
        this.coverId = coverId;
        this.coverEditionKey = coverEditionKey;
        this.availabilityStatus = availabilityStatus;
        this.firstPublishYear = firstPublishYear;
        this.authors = authors;
        this.Description = Description;
    }

    // Method to add a Category by ID
    public void addCategoryById(Long categoryId) {
        if (categoryId != null) {
            // Fetch the Category object from the repository by ID and add it to the list
            // For example:
            // Category category = categoryRepository.findById(categoryId).orElse(null);
            // If you want to add the category directly, you'd need access to the repository.

            Category category = new Category(); // This should be replaced with actual fetching logic
            category.setId(categoryId);
            this.categories.add(category);
        } else {
            System.out.println("Warning: Trying to add a null categoryId.");
        }
    }
    public void addCategoryByName(String categoryName, CategoryRepository categoryRepository) {
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            Category existingCategory = categoryRepository.findByName(categoryName.trim());
            if (existingCategory == null) {
                // Create a new category if it does not exist
                Category newCategory = new Category();
                newCategory.setName(categoryName.trim());
                existingCategory = categoryRepository.save(newCategory); // Persist the new category
            }
            // Add the category to the book's categories
            this.categories.add(existingCategory);
        } else {
            System.out.println("Warning: Trying to add a category with a null or empty name.");
        }
    }


}

