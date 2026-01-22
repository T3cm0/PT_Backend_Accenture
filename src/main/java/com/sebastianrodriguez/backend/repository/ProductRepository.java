package com.sebastianrodriguez.backend.repository;

import com.sebastianrodriguez.backend.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para productos.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lista productos por sucursal.
     *
     * @param branchId identificador de la sucursal.
     * @return listado de productos.
     */
    List<Product> findByBranchId(Long branchId);

    /**
     * Marca como eliminados los productos de una sucursal (soft delete).
     *
     * @param branchId identificador de la sucursal.
     * @return cantidad de filas afectadas.
     */
    @Modifying
    @Query("update Product p set p.deleted = true where p.branch.id = :branchId")
    int softDeleteByBranchId(@Param("branchId") Long branchId);

    /**
     * Marca como eliminados los productos de una franquicia (soft delete).
     *
     * @param franchiseId identificador de la franquicia.
     * @return cantidad de filas afectadas.
     */
    @Modifying
    @Query("update Product p set p.deleted = true where p.branch.franchise.id = :franchiseId")
    int softDeleteByFranchiseId(@Param("franchiseId") Long franchiseId);

    /**
     * Obtiene el producto con mayor stock por sucursal para una franquicia.
     *
     * @param franchiseId identificador de la franquicia.
     * @return lista de productos con su sucursal asociada.
     */
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
