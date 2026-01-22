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

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;
    private final ProductService productService;

    public BranchController(BranchService branchService, ProductService productService) {
        this.branchService = branchService;
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public BranchDetailResponse get(@PathVariable Long id) {
        return branchService.get(id);
    }

    @PutMapping("/{id}")
    public BranchSummaryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BranchUpdateRequest request
    ) {
        return branchService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{branchId}/products")
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable Long branchId,
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductResponse response = productService.create(branchId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{branchId}/products")
    public List<ProductResponse> listProducts(@PathVariable Long branchId) {
        return productService.listByBranch(branchId);
    }

    // Endpoints de productos individuales estan en ProductController.
}
