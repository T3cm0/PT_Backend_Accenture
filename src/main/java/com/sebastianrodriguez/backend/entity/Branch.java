package com.sebastianrodriguez.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad de sucursal con borrado logico y relacion a franquicia/productos.
 */
@Entity
@Table(name = "branches")
@SQLDelete(sql = "UPDATE branches SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    /**
     * Obtiene el identificador.
     *
     * @return id de la sucursal.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador.
     *
     * @param id id de la sucursal.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre.
     *
     * @return nombre de la sucursal.
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre.
     *
     * @param name nombre de la sucursal.
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
     * Obtiene la franquicia asociada.
     *
     * @return franquicia.
     */
    public Franchise getFranchise() {
        return franchise;
    }

    /**
     * Asigna la franquicia asociada.
     *
     * @param franchise franquicia.
     */
    public void setFranchise(Franchise franchise) {
        this.franchise = franchise;
    }

    /**
     * Obtiene los productos asociados.
     *
     * @return lista de productos.
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Asigna los productos asociados.
     *
     * @param products lista de productos.
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
