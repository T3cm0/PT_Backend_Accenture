package com.sebastianrodriguez.backend.service;

import com.sebastianrodriguez.backend.dto.BranchCreateRequest;
import com.sebastianrodriguez.backend.dto.BranchDetailResponse;
import com.sebastianrodriguez.backend.dto.BranchSummaryResponse;
import com.sebastianrodriguez.backend.dto.BranchUpdateRequest;
import com.sebastianrodriguez.backend.dto.ProductResponse;
import com.sebastianrodriguez.backend.entity.Branch;
import com.sebastianrodriguez.backend.entity.Franchise;
import com.sebastianrodriguez.backend.entity.Product;
import com.sebastianrodriguez.backend.exception.NotFoundException;
import com.sebastianrodriguez.backend.repository.BranchRepository;
import com.sebastianrodriguez.backend.repository.FranchiseRepository;
import com.sebastianrodriguez.backend.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio para sucursales.
 */
@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;

    /**
     * Construye el servicio con sus repositorios.
     *
     * @param branchRepository repositorio de sucursales.
     * @param franchiseRepository repositorio de franquicias.
     * @param productRepository repositorio de productos.
     */
    public BranchService(
            BranchRepository branchRepository,
            FranchiseRepository franchiseRepository,
            ProductRepository productRepository
    ) {
        this.branchRepository = branchRepository;
        this.franchiseRepository = franchiseRepository;
        this.productRepository = productRepository;
    }

    /**
     * Crea una sucursal en una franquicia existente.
     *
     * @param franchiseId identificador de la franquicia.
     * @param request datos de la sucursal.
     * @return resumen de la sucursal creada.
     */
    @Transactional
    public BranchSummaryResponse create(Long franchiseId, BranchCreateRequest request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + franchiseId));
        Branch branch = new Branch();
        branch.setName(request.name());
        branch.setFranchise(franchise);
        Branch saved = branchRepository.save(branch);
        return toSummary(saved);
    }

    /**
     * Lista sucursales de una franquicia.
     *
     * @param franchiseId identificador de la franquicia.
     * @return listado de sucursales.
     */
    @Transactional(readOnly = true)
    public List<BranchSummaryResponse> listByFranchise(Long franchiseId) {
        if (!franchiseRepository.existsById(franchiseId)) {
            throw new NotFoundException("Franchise not found: " + franchiseId);
        }
        return branchRepository.findByFranchiseId(franchiseId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Obtiene el detalle de una sucursal con sus productos.
     *
     * @param id identificador de la sucursal.
     * @return detalle de la sucursal.
     */
    @Transactional(readOnly = true)
    public BranchDetailResponse get(Long id) {
        Branch branch = branchRepository.findWithProductsById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + id));
        return toDetail(branch);
    }

    /**
     * Actualiza el nombre de una sucursal.
     *
     * @param id identificador de la sucursal.
     * @param request datos a actualizar.
     * @return resumen actualizado.
     */
    @Transactional
    public BranchSummaryResponse update(Long id, BranchUpdateRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + id));
        branch.setName(request.name());
        return toSummary(branchRepository.save(branch));
    }

    /**
     * Elimina una sucursal con borrado logico y cascada sobre productos.
     *
     * @param id identificador de la sucursal.
     */
    @Transactional
    public void delete(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + id));
        productRepository.softDeleteByBranchId(id);
        branchRepository.delete(branch);
    }

    /**
     * Mapea una sucursal a su respuesta resumida.
     *
     * @param branch entidad de sucursal.
     * @return DTO resumen.
     */
    private BranchSummaryResponse toSummary(Branch branch) {
        return new BranchSummaryResponse(branch.getId(), branch.getName());
    }

    /**
     * Mapea una sucursal a su detalle con productos.
     *
     * @param branch entidad de sucursal.
     * @return DTO de detalle.
     */
    private BranchDetailResponse toDetail(Branch branch) {
        List<ProductResponse> products = branch.getProducts()
                .stream()
                .map(this::toResponse)
                .toList();
        return new BranchDetailResponse(branch.getId(), branch.getName(), products);
    }

    /**
     * Mapea un producto a su respuesta.
     *
     * @param product entidad de producto.
     * @return DTO de producto.
     */
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getStock());
    }
}
