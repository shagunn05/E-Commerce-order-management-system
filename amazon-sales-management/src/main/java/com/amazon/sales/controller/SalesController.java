package com.amazon.sales.controller;

import com.amazon.sales.dto.PageResponseDto;
import com.amazon.sales.dto.SalesRequestDto;
import com.amazon.sales.dto.SalesResponseDto;
import com.amazon.sales.model.SalesFilter;
import com.amazon.sales.service.SalesFilterService;
import com.amazon.sales.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Amazon sales management and CRUD operations.
 * Base URL: /api/v1/sales
 */
@RestController
@RequestMapping("/api/v1/sales")
public class SalesController {

    private final SalesService salesService;
    private final SalesFilterService salesFilterService;

    public SalesController(SalesService salesService, SalesFilterService salesFilterService) {
        this.salesService = salesService;
        this.salesFilterService = salesFilterService;
    }

    @GetMapping
    public ResponseEntity<List<SalesResponseDto>> getAllSales() {
        return ResponseEntity.ok(salesService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesResponseDto> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(salesService.getSaleById(id));
    }

    @PostMapping
    public ResponseEntity<SalesResponseDto> createSale(@Valid @RequestBody SalesRequestDto request) {
        SalesResponseDto created = salesService.createSale(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesResponseDto> updateSale(@PathVariable Long id,
                                                       @Valid @RequestBody SalesRequestDto request) {
        SalesResponseDto updated = salesService.updateSale(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSale(@PathVariable Long id) {
        salesService.deleteSale(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sale record deleted successfully");
        response.put("deletedId", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SalesResponseDto>> searchSales(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "q", required = false) String q) {
        String query = (keyword != null && !keyword.isBlank()) ? keyword : q;
        return ResponseEntity.ok(salesFilterService.search(query));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SalesResponseDto>> filterSales(SalesFilter filter) {
        return ResponseEntity.ok(salesFilterService.filter(filter));
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageResponseDto<SalesResponseDto>> getPaginatedSales(SalesFilter filter) {
        return ResponseEntity.ok(salesFilterService.findPaginated(filter));
    }
}
