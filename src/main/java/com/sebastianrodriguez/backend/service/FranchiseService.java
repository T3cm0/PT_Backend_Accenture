package com.sebastianrodriguez.backend.service;

import com.sebastianrodriguez.backend.dto.BranchDetailResponse;
import com.sebastianrodriguez.backend.dto.BranchTopStockProductResponse;
import com.sebastianrodriguez.backend.dto.FranchiseCreateRequest;
import com.sebastianrodriguez.backend.dto.FranchiseDetailResponse;
import com.sebastianrodriguez.backend.dto.FranchiseSummaryResponse;
import com.sebastianrodriguez.backend.dto.FranchiseUpdateRequest;
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
 * Logica de negocio para franquicias y reportes asociados.
 */
@Service
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    /**
     * Construye el servicio con sus repositorios.
     *
     * @param franchiseRepository repositorio de franquicias.
     * @param branchRepository repositorio de sucursales.
     * @param productRepository repositorio de productos.
     */
    public FranchiseService(
            FranchiseRepository franchiseRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository
    ) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    /**
     * Crea una franquicia.
     *
     * @param request datos de entrada.
     * @return resumen de la franquicia creada.
     */
    @Transactional
    public FranchiseSummaryResponse create(FranchiseCreateRequest request) {
        Franchise franchise = new Franchise();
        franchise.setName(request.name());
        Franchise saved = franchiseRepository.save(franchise);
        return toSummary(saved);
    }

    /**
     * Lista todas las franquicias activas.
     *
     * @return listado de franquicias.
     */
    @Transactional(readOnly = true)
    public List<FranchiseSummaryResponse> list() {
        return franchiseRepository.findAll()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Obtiene el detalle de una franquicia con sus sucursales y productos.
     *
     * @param id identificador de la franquicia.
     * @return detalle de la franquicia.
     */
    @Transactional(readOnly = true)
    public FranchiseDetailResponse get(Long id) {
        Franchise franchise = franchiseRepository.findWithBranchesById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        return toDetail(franchise);
    }

    /**
     * Obtiene el producto con mayor stock por sucursal para una franquicia.
     *
     * @param franchiseId identificador de la franquicia.
     * @return lista con el top de stock por sucursal.
     */
    @Transactional(readOnly = true)
    public List<BranchTopStockProductResponse> topStockByBranch(Long franchiseId) {
        if (!franchiseRepository.existsById(franchiseId)) {
            throw new NotFoundException("Franchise not found: " + franchiseId);
        }
        return productRepository.findTopStockByFranchiseId(franchiseId)
                .stream()
                .map(this::toTopStock)
                .toList();
    }

    /**
     * Actualiza el nombre de una franquicia.
     *
     * @param id identificador de la franquicia.
     * @param request datos a actualizar.
     * @return resumen actualizado.
     */
    @Transactional
    public FranchiseSummaryResponse update(Long id, FranchiseUpdateRequest request) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        franchise.setName(request.name());
        return toSummary(franchiseRepository.save(franchise));
    }

    /**
     * Elimina una franquicia con borrado logico y cascada sobre sucursales/productos.
     *
     * @param id identificador de la franquicia.
     */
    @Transactional
    public void delete(Long id) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        productRepository.softDeleteByFranchiseId(id);
        branchRepository.softDeleteByFranchiseId(id);
        franchiseRepository.delete(franchise);
    }

    /**
     * Mapea una entidad a su respuesta resumida.
     *
     * @param franchise entidad de franquicia.
     * @return DTO resumen.
     */
    private FranchiseSummaryResponse toSummary(Franchise franchise) {
        return new FranchiseSummaryResponse(franchise.getId(), franchise.getName());
    }

    /**
     * Mapea una franquicia a su detalle con sucursales y productos.
     *
     * @param franchise entidad de franquicia.
     * @return DTO de detalle.
     */
    private FranchiseDetailResponse toDetail(Franchise franchise) {
        List<BranchDetailResponse> branches = franchise.getBranches()
                .stream()
                .map(this::toDetail)
                .toList();
        return new FranchiseDetailResponse(franchise.getId(), franchise.getName(), branches);
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
     * Mapea una entidad producto a su respuesta.
     *
     * @param product entidad de producto.
     * @return DTO de producto.
     */
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getStock());
    }

    /**
     * Mapea un producto al reporte de top stock por sucursal.
     *
     * @param product producto con su sucursal cargada.
     * @return DTO de top stock.
     */
    private BranchTopStockProductResponse toTopStock(Product product) {
        Branch branch = product.getBranch();
        return new BranchTopStockProductResponse(
                branch.getId(),
                branch.getName(),
                product.getId(),
                product.getName(),
                product.getStock()
        );
    }
}
