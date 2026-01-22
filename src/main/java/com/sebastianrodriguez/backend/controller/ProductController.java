package com.sebastianrodriguez.backend.controller;

import com.sebastianrodriguez.backend.dto.ProductResponse;
import com.sebastianrodriguez.backend.dto.ProductUpdateRequest;
import com.sebastianrodriguez.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones CRUD de productos.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Construye el controlador con su dependencia principal.
     *
     * @param productService servicio de productos.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Obtiene el detalle de un producto.
     *
     * @param id identificador del producto.
     * @return producto encontrado.
     */
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }

    /**
     * Actualiza el nombre y stock de un producto.
     *
     * @param id identificador del producto.
     * @param request datos a actualizar.
     * @return producto actualizado.
     */
    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        return productService.update(id, request);
    }

    /**
     * Elimina un producto (borrado logico).
     *
     * @param id identificador del producto.
     * @return respuesta sin contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
