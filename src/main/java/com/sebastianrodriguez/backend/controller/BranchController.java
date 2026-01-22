package com.sebastianrodriguez.backend.controller;

import com.sebastianrodriguez.backend.dto.BranchDetailResponse;
import com.sebastianrodriguez.backend.dto.BranchSummaryResponse;
import com.sebastianrodriguez.backend.dto.BranchUpdateRequest;
import com.sebastianrodriguez.backend.dto.ProductCreateRequest;
import com.sebastianrodriguez.backend.dto.ProductResponse;
import com.sebastianrodriguez.backend.service.BranchService;
import com.sebastianrodriguez.backend.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones sobre sucursales y productos por sucursal.
 */
@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;
    private final ProductService productService;

    /**
     * Construye el controlador con sus dependencias.
     *
     * @param branchService servicio de sucursales.
     * @param productService servicio de productos.
     */
    public BranchController(BranchService branchService, ProductService productService) {
        this.branchService = branchService;
        this.productService = productService;
    }

    /**
     * Obtiene el detalle de una sucursal con sus productos.
     *
     * @param id identificador de la sucursal.
     * @return detalle de la sucursal.
     */
    @GetMapping("/{id}")
    public BranchDetailResponse get(@PathVariable Long id) {
        return branchService.get(id);
    }

    /**
     * Actualiza el nombre de una sucursal.
     *
     * @param id identificador de la sucursal.
     * @param request datos a actualizar.
     * @return sucursal actualizada.
     */
    @PutMapping("/{id}")
    public BranchSummaryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BranchUpdateRequest request
    ) {
        return branchService.update(id, request);
    }

    /**
     * Elimina una sucursal con borrado logico y cascada sobre productos.
     *
     * @param id identificador de la sucursal.
     * @return respuesta sin contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Crea un producto dentro de una sucursal.
     *
     * @param branchId identificador de la sucursal.
     * @param request datos del producto.
     * @return producto creado.
     */
    @PostMapping("/{branchId}/products")
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable Long branchId,
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductResponse response = productService.create(branchId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista los productos de una sucursal.
     *
     * @param branchId identificador de la sucursal.
     * @return listado de productos.
     */
    @GetMapping("/{branchId}/products")
    public List<ProductResponse> listProducts(@PathVariable Long branchId) {
        return productService.listByBranch(branchId);
    }

    // Endpoints de productos individuales estan en ProductController.
}
