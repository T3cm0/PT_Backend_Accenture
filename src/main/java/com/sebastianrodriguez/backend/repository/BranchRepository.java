package com.sebastianrodriguez.backend.repository;

import com.sebastianrodriguez.backend.entity.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para sucursales.
 */
public interface BranchRepository extends JpaRepository<Branch, Long> {

    /**
     * Lista sucursales por franquicia.
     *
     * @param franchiseId identificador de la franquicia.
     * @return listado de sucursales.
     */
    List<Branch> findByFranchiseId(Long franchiseId);

    /**
     * Obtiene una sucursal con sus productos cargados.
     *
     * @param id identificador de la sucursal.
     * @return sucursal con productos si existe.
     */
    @EntityGraph(attributePaths = "products")
    Optional<Branch> findWithProductsById(Long id);

    /**
     * Marca como eliminadas las sucursales de una franquicia (soft delete).
     *
     * @param franchiseId identificador de la franquicia.
     * @return cantidad de filas afectadas.
     */
    @Modifying
    @Query("update Branch b set b.deleted = true where b.franchise.id = :franchiseId")
    int softDeleteByFranchiseId(@Param("franchiseId") Long franchiseId);
}
