package com.sebastianrodriguez.backend.service;

import com.sebastianrodriguez.backend.dto.ProductCreateRequest;
import com.sebastianrodriguez.backend.dto.ProductResponse;
import com.sebastianrodriguez.backend.dto.ProductUpdateRequest;
import com.sebastianrodriguez.backend.entity.Branch;
import com.sebastianrodriguez.backend.entity.Product;
import com.sebastianrodriguez.backend.exception.NotFoundException;
import com.sebastianrodriguez.backend.repository.BranchRepository;
import com.sebastianrodriguez.backend.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public ProductService(ProductRepository productRepository, BranchRepository branchRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public ProductResponse create(Long branchId, ProductCreateRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + branchId));
        Product product = new Product();
        product.setName(request.name());
        product.setStock(request.stock());
        product.setBranch(branch);
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listByBranch(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new NotFoundException("Branch not found: " + branchId);
        }
        return productRepository.findByBranchId(branchId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        product.setName(request.name());
        product.setStock(request.stock());
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        productRepository.delete(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getStock());
    }
}
