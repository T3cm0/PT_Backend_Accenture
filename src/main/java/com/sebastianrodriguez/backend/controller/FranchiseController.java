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

@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {

    private final FranchiseService franchiseService;
    private final BranchService branchService;

    public FranchiseController(FranchiseService franchiseService, BranchService branchService) {
        this.franchiseService = franchiseService;
        this.branchService = branchService;
    }

    @PostMapping
    public ResponseEntity<FranchiseSummaryResponse> create(@Valid @RequestBody FranchiseCreateRequest request) {
        FranchiseSummaryResponse response = franchiseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<FranchiseSummaryResponse> list() {
        return franchiseService.list();
    }

    @GetMapping("/{id}")
    public FranchiseDetailResponse get(@PathVariable Long id) {
        return franchiseService.get(id);
    }

    @PutMapping("/{id}")
    public FranchiseSummaryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FranchiseUpdateRequest request
    ) {
        return franchiseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        franchiseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/branches")
    public ResponseEntity<BranchSummaryResponse> createBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchCreateRequest request
    ) {
        BranchSummaryResponse response = branchService.create(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/branches")
    public List<BranchSummaryResponse> listBranches(@PathVariable Long id) {
        return branchService.listByFranchise(id);
    }

    @GetMapping("/{id}/top-stock-products")
    public List<BranchTopStockProductResponse> topStockProducts(@PathVariable Long id) {
        return franchiseService.topStockByBranch(id);
    }
}
