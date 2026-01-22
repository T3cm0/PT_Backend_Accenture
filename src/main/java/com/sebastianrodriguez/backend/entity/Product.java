package com.sebastianrodriguez.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad de producto con borrado logico y relacion a sucursal.
 */
@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /**
     * Obtiene el identificador.
     *
     * @return id del producto.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador.
     *
     * @param id id del producto.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre.
     *
     * @return nombre del producto.
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre.
     *
     * @param name nombre del producto.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene el stock.
     *
     * @return cantidad en stock.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Asigna el stock.
     *
     * @param stock cantidad en stock.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Indica si esta marcado como eliminado.
     *
     * @return true si esta eliminado.
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
     * Obtiene la sucursal asociada.
     *
     * @return sucursal.
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Asigna la sucursal asociada.
     *
     * @param branch sucursal.
     */
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
