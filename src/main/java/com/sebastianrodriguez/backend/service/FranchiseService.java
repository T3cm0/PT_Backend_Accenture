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

@Service
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public FranchiseService(
            FranchiseRepository franchiseRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository
    ) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public FranchiseSummaryResponse create(FranchiseCreateRequest request) {
        Franchise franchise = new Franchise();
        franchise.setName(request.name());
        Franchise saved = franchiseRepository.save(franchise);
        return toSummary(saved);
    }

    @Transactional(readOnly = true)
    public List<FranchiseSummaryResponse> list() {
        return franchiseRepository.findAll()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public FranchiseDetailResponse get(Long id) {
        Franchise franchise = franchiseRepository.findWithBranchesById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        return toDetail(franchise);
    }

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

    @Transactional
    public FranchiseSummaryResponse update(Long id, FranchiseUpdateRequest request) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        franchise.setName(request.name());
        return toSummary(franchiseRepository.save(franchise));
    }

    @Transactional
    public void delete(Long id) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        productRepository.softDeleteByFranchiseId(id);
        branchRepository.softDeleteByFranchiseId(id);
        franchiseRepository.delete(franchise);
    }

    private FranchiseSummaryResponse toSummary(Franchise franchise) {
        return new FranchiseSummaryResponse(franchise.getId(), franchise.getName());
    }

    private FranchiseDetailResponse toDetail(Franchise franchise) {
        List<BranchDetailResponse> branches = franchise.getBranches()
                .stream()
                .map(this::toDetail)
                .toList();
        return new FranchiseDetailResponse(franchise.getId(), franchise.getName(), branches);
    }

    private BranchDetailResponse toDetail(Branch branch) {
        List<ProductResponse> products = branch.getProducts()
                .stream()
                .map(this::toResponse)
                .toList();
        return new BranchDetailResponse(branch.getId(), branch.getName(), products);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getStock());
    }

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
