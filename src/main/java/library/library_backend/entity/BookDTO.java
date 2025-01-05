package library.library_backend.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.util.List;
@Data
public class BookDTO {

    private String key; // Book key
    private String title; // Book title

    private List<AuthorDTO> authors; // List of authors
    private int firstPublishYear; // First publication year
    @JsonProperty("cover_id")
    private String cover_Id; // Cover image ID
    private String coverEditionKey; // Cover edition key
    private List<String> subjects; // List of subjects
    private String availability; // Availability details
    private String Description ;

}