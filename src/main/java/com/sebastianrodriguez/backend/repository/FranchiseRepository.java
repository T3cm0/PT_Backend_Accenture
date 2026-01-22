package com.sebastianrodriguez.backend.repository;

import com.sebastianrodriguez.backend.entity.Franchise;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    @EntityGraph(attributePaths = {"branches"})
    Optional<Franchise> findWithBranchesById(Long id);
}
