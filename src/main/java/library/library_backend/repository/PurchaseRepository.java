package library.library_backend.repository;

import library.library_backend.entity.Purchase;
import library.library_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> getPurchasesByUser(User user);
    // You can add custom queries if necessary
}
