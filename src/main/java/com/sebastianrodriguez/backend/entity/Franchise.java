package com.sebastianrodriguez.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad de franquicia con borrado logico y relacion a sucursales.
 */
@Entity
@Table(name = "franchises")
@SQLDelete(sql = "UPDATE franchises SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Franchise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches = new ArrayList<>();

    /**
     * Obtiene el identificador.
     *
     * @return id de la franquicia.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador.
     *
     * @param id id de la franquicia.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre.
     *
     * @return nombre de la franquicia.
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre.
     *
     * @param name nombre de la franquicia.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indica si esta marcada como eliminada.
     *
     * @return true si esta eliminada.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Marca el estado de borrado logico.
     *
     * @param deleted true para borrado logico.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Obtiene las sucursales asociadas.
     *
     * @return lista de sucursales.
     */
    public List<Branch> getBranches() {
        return branches;
    }

    /**
     * Asigna las sucursales asociadas.
     *
     * @param branches lista de sucursales.
     */
    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
