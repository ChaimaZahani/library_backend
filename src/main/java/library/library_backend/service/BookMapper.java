package library.library_backend.service;



import com.fasterxml.jackson.databind.JsonNode;
import library.library_backend.entity.AuthorDTO;
import library.library_backend.entity.Book;
import library.library_backend.entity.BookDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class BookMapper {


    public Book mapToEntity(BookDTO bookDTO) {


        return new Book(
                bookDTO.getTitle(),
                bookDTO.getKey(),
                bookDTO.getCover_Id(),
                bookDTO.getCoverEditionKey(),
                bookDTO.getAvailability(),
                bookDTO.getFirstPublishYear(),
                bookDTO.getAuthors(),
                bookDTO.getDescription()
        );
    }
    public BookDTO mapToDTO(JsonNode workNode) {
        BookDTO bookDTO = new BookDTO();
        // Map basic fields
        bookDTO.setKey(workNode.has("key") ? workNode.get("key").asText() : null);
        bookDTO.setTitle(workNode.has("title") ? workNode.get("title").asText() : null);
        bookDTO.setCover_Id(workNode.has("cover_id") ? workNode.get("cover_id").asText() : null);
        bookDTO.setCoverEditionKey(workNode.has("cover_edition_key") ? workNode.get("cover_edition_key").asText() : null);
        bookDTO.setFirstPublishYear(workNode.has("first_publish_year") ? workNode.get("first_publish_year").asInt() : 0);

        // Handle authors
        if (workNode.has("authors")) {
            List<AuthorDTO> authors = new ArrayList<>();
            for (JsonNode authorNode : workNode.get("authors")) {
                AuthorDTO authorDTO = new AuthorDTO();
                authorDTO.setKey(authorNode.has("key") ? authorNode.get("key").asText() : null);
                authorDTO.setName(authorNode.has("name") ? authorNode.get("name").asText() : null);
                authors.add(authorDTO);
            }
            bookDTO.setAuthors(authors);
        }
        // Handle availability
        if (workNode.has("availability") && workNode.get("availability").has("status")) {
            bookDTO.setAvailability(workNode.get("availability").get("status").asText());
        } else {
            bookDTO.setAvailability("private"); // Default value if "availability" or "status" is missing
        }

        return bookDTO;
    }

}
