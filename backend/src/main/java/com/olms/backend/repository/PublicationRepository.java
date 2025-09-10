package com.olms.backend.repository;

import com.olms.backend.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    Optional<Publication> findByNameIgnoreCase(String name);
}