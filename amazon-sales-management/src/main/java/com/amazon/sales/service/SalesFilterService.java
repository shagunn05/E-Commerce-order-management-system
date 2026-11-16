package com.amazon.sales.service;

import com.amazon.sales.dto.PageResponseDto;
import com.amazon.sales.dto.SalesResponseDto;
import com.amazon.sales.model.AmazonSale;
import com.amazon.sales.model.SalesFilter;
import com.amazon.sales.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing full-text search, multi-attribute filtering, and server-side pagination.
 */
@Service
@Transactional(readOnly = true)
public class SalesFilterService {

    private static final Logger log = LoggerFactory.getLogger(SalesFilterService.class);

    private final SalesRepository salesRepository;
    private final SalesService salesService;

    public SalesFilterService(SalesRepository salesRepository, SalesService salesService) {
        this.salesRepository = salesRepository;
        this.salesService = salesService;
    }

    public List<SalesResponseDto> search(String keyword) {
        log.info("Executing search with keyword: '{}'", keyword);
        List<AmazonSale> results = salesRepository.search(keyword);
        return results.stream().map(salesService::mapToDto).collect(Collectors.toList());
    }

    public List<SalesResponseDto> filter(SalesFilter filter) {
        log.info("Executing dynamic multi-column filter: category={}, status={}, fulfilment={}",
                filter.getCategory(), filter.getStatus(), filter.getFulfilment());
        List<AmazonSale> results = salesRepository.filter(filter);
        return results.stream().map(salesService::mapToDto).collect(Collectors.toList());
    }

    public PageResponseDto<SalesResponseDto> findPaginated(SalesFilter filter) {
        log.info("Executing paginated search/filter: page={}, size={}, sortBy={}, sortDir={}",
                filter.getPage(), filter.getSize(), filter.getSortBy(), filter.getSortDirection());
        PageResponseDto<AmazonSale> pageResult = salesRepository.findPaginated(filter);

        List<SalesResponseDto> dtoList = pageResult.getContent().stream()
                .map(salesService::mapToDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                dtoList,
                pageResult.getPageNumber(),
                pageResult.getPageSize(),
                pageResult.getTotalElements()
        );
    }
}
