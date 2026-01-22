package com.sebastianrodriguez.backend.repository;

import com.sebastianrodriguez.backend.entity.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByFranchiseId(Long franchiseId);

    @EntityGraph(attributePaths = "products")
    Optional<Branch> findWithProductsById(Long id);

    @Modifying
    @Query("update Branch b set b.deleted = true where b.franchise.id = :franchiseId")
    int softDeleteByFranchiseId(@Param("franchiseId") Long franchiseId);
}
