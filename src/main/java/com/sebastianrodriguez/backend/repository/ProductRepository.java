package com.sebastianrodriguez.backend.repository;

import com.sebastianrodriguez.backend.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBranchId(Long branchId);

    @Modifying
    @Query("update Product p set p.deleted = true where p.branch.id = :branchId")
    int softDeleteByBranchId(@Param("branchId") Long branchId);

    @Modifying
    @Query("update Product p set p.deleted = true where p.branch.franchise.id = :franchiseId")
    int softDeleteByFranchiseId(@Param("franchiseId") Long franchiseId);

    @Query("""
            select p
            from Product p
            join fetch p.branch b
            where b.franchise.id = :franchiseId
            and p.stock = (
                select max(p2.stock)
                from Product p2
                where p2.branch = b
            )
            """)
    List<Product> findTopStockByFranchiseId(@Param("franchiseId") Long franchiseId);
}
