package com.sebastianrodriguez.backend.repository;

import com.sebastianrodriguez.backend.entity.Franchise;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para franquicias.
 */
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    /**
     * Obtiene una franquicia con sus sucursales cargadas.
     *
     * @param id identificador de la franquicia.
     * @return franquicia con sucursales si existe.
     */
    @EntityGraph(attributePaths = {"branches"})
    Optional<Franchise> findWithBranchesById(Long id);
}
