package com.sebastianrodriguez.backend.controller;

import com.sebastianrodriguez.backend.dto.BranchCreateRequest;
import com.sebastianrodriguez.backend.dto.BranchSummaryResponse;
import com.sebastianrodriguez.backend.dto.BranchTopStockProductResponse;
import com.sebastianrodriguez.backend.dto.FranchiseCreateRequest;
import com.sebastianrodriguez.backend.dto.FranchiseDetailResponse;
import com.sebastianrodriguez.backend.dto.FranchiseSummaryResponse;
import com.sebastianrodriguez.backend.dto.FranchiseUpdateRequest;
import com.sebastianrodriguez.backend.service.BranchService;
import com.sebastianrodriguez.backend.service.FranchiseService;
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
 * Controlador REST para operaciones de franquicias y sus sucursales.
 */
@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {

    private final FranchiseService franchiseService;
    private final BranchService branchService;

    /**
     * Construye el controlador con sus dependencias.
     *
     * @param franchiseService servicio de franquicias.
     * @param branchService servicio de sucursales.
     */
    public FranchiseController(FranchiseService franchiseService, BranchService branchService) {
        this.franchiseService = franchiseService;
        this.branchService = branchService;
    }

    /**
     * Crea una franquicia.
     *
     * @param request datos de la franquicia.
     * @return franquicia creada.
     */
    @PostMapping
    public ResponseEntity<FranchiseSummaryResponse> create(@Valid @RequestBody FranchiseCreateRequest request) {
        FranchiseSummaryResponse response = franchiseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas las franquicias activas.
     *
     * @return listado resumido de franquicias.
     */
    @GetMapping
    public List<FranchiseSummaryResponse> list() {
        return franchiseService.list();
    }

    /**
     * Obtiene el detalle de una franquicia con sus sucursales y productos.
     *
     * @param id identificador de la franquicia.
     * @return detalle de la franquicia.
     */
    @GetMapping("/{id}")
    public FranchiseDetailResponse get(@PathVariable Long id) {
        return franchiseService.get(id);
    }

    /**
     * Actualiza el nombre de una franquicia.
     *
     * @param id identificador de la franquicia.
     * @param request datos a actualizar.
     * @return franquicia actualizada.
     */
    @PutMapping("/{id}")
    public FranchiseSummaryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FranchiseUpdateRequest request
    ) {
        return franchiseService.update(id, request);
    }

    /**
     * Elimina una franquicia con borrado logico y cascada sobre sucursales/productos.
     *
     * @param id identificador de la franquicia.
     * @return respuesta sin contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        franchiseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Crea una sucursal dentro de una franquicia.
     *
     * @param id identificador de la franquicia.
     * @param request datos de la sucursal.
     * @return sucursal creada.
     */
    @PostMapping("/{id}/branches")
    public ResponseEntity<BranchSummaryResponse> createBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchCreateRequest request
    ) {
        BranchSummaryResponse response = branchService.create(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista sucursales de una franquicia.
     *
     * @param id identificador de la franquicia.
     * @return listado de sucursales.
     */
    @GetMapping("/{id}/branches")
    public List<BranchSummaryResponse> listBranches(@PathVariable Long id) {
        return branchService.listByFranchise(id);
    }

    /**
     * Retorna el producto con mayor stock por sucursal para una franquicia.
     *
     * @param id identificador de la franquicia.
     * @return lista con el top de stock por sucursal.
     */
    @GetMapping("/{id}/top-stock-products")
    public List<BranchTopStockProductResponse> topStockProducts(@PathVariable Long id) {
        return franchiseService.topStockByBranch(id);
    }
}
