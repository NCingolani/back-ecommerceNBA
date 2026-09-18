package com.api.ecommerce.repository;
 
import com.api.ecommerce.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.Optional;
 
public interface WishlistRepository
        extends JpaRepository<Wishlist, Long> {
 
    Optional<Wishlist> findByUsuarioId(Long usuarioId);
 
}
