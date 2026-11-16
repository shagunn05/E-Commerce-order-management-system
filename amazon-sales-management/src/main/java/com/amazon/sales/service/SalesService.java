package com.amazon.sales.service;

import com.amazon.sales.dto.SalesRequestDto;
import com.amazon.sales.dto.SalesResponseDto;
import com.amazon.sales.exception.ResourceNotFoundException;
import com.amazon.sales.model.AmazonSale;
import com.amazon.sales.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating CRUD business operations on Amazon Sales records.
 */
@Service
public class SalesService {

    private static final Logger log = LoggerFactory.getLogger(SalesService.class);

    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Transactional(readOnly = true)
    public List<SalesResponseDto> getAllSales() {
        log.info("Fetching all sales records");
        return salesRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalesResponseDto getSaleById(Long id) {
        log.info("Fetching sale record by ID: {}", id);
        AmazonSale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amazon Sale record not found with id: " + id));
        return mapToDto(sale);
    }

    @Transactional
    public SalesResponseDto createSale(SalesRequestDto request) {
        log.info("Creating new sale record for Order ID: {}, SKU: {}", request.getOrderId(), request.getSku());
        AmazonSale sale = mapToEntity(request);
        AmazonSale saved = salesRepository.save(sale);
        log.info("Successfully created sale record with generated Index ID: {}", saved.getIndexId());
        return mapToDto(saved);
    }

    @Transactional
    public SalesResponseDto updateSale(Long id, SalesRequestDto request) {
        log.info("Updating sale record ID: {}", id);
        if (!salesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Amazon Sale record not found with id: " + id);
        }

        AmazonSale sale = mapToEntity(request);
        AmazonSale updated = salesRepository.update(id, sale);
        if (updated == null) {
            throw new ResourceNotFoundException("Amazon Sale record not found with id: " + id);
        }
        log.info("Successfully updated sale record ID: {}", id);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteSale(Long id) {
        log.info("Deleting sale record ID: {}", id);
        if (!salesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Amazon Sale record not found with id: " + id);
        }
        boolean deleted = salesRepository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Amazon Sale record not found with id: " + id);
        }
        log.info("Successfully deleted sale record ID: {}", id);
    }

    public SalesResponseDto mapToDto(AmazonSale entity) {
        if (entity == null) return null;
        SalesResponseDto dto = new SalesResponseDto();
        dto.setIndexId(entity.getIndexId());
        dto.setOrderId(entity.getOrderId());
        dto.setDate(entity.getDate());
        dto.setStatus(entity.getStatus());
        dto.setFulfilment(entity.getFulfilment());
        dto.setSalesChannel(entity.getSalesChannel());
        dto.setShipServiceLevel(entity.getShipServiceLevel());
        dto.setStyle(entity.getStyle());
        dto.setSku(entity.getSku());
        dto.setCategory(entity.getCategory());
        dto.setSize(entity.getSize());
        dto.setAsin(entity.getAsin());
        dto.setCourierStatus(entity.getCourierStatus());
        dto.setQty(entity.getQty());
        dto.setCurrency(entity.getCurrency());
        dto.setAmount(entity.getAmount());
        dto.setShipCity(entity.getShipCity());
        dto.setShipState(entity.getShipState());
        dto.setShipPostalCode(entity.getShipPostalCode());
        dto.setShipCountry(entity.getShipCountry());
        dto.setPromotionIds(entity.getPromotionIds());
        dto.setB2b(entity.getB2b());
        dto.setFulfilledBy(entity.getFulfilledBy());
        return dto;
    }

    public AmazonSale mapToEntity(SalesRequestDto dto) {
        if (dto == null) return null;
        AmazonSale entity = new AmazonSale();
        entity.setOrderId(dto.getOrderId());
        entity.setDate(dto.getDate());
        entity.setStatus(dto.getStatus());
        entity.setFulfilment(dto.getFulfilment());
        entity.setSalesChannel(dto.getSalesChannel());
        entity.setShipServiceLevel(dto.getShipServiceLevel());
        entity.setStyle(dto.getStyle());
        entity.setSku(dto.getSku());
        entity.setCategory(dto.getCategory());
        entity.setSize(dto.getSize());
        entity.setAsin(dto.getAsin());
        entity.setCourierStatus(dto.getCourierStatus());
        entity.setQty(dto.getQty());
        entity.setCurrency(dto.getCurrency());
        entity.setAmount(dto.getAmount());
        entity.setShipCity(dto.getShipCity());
        entity.setShipState(dto.getShipState());
        entity.setShipPostalCode(dto.getShipPostalCode());
        entity.setShipCountry(dto.getShipCountry());
        entity.setPromotionIds(dto.getPromotionIds());
        entity.setB2b(dto.getB2b());
        entity.setFulfilledBy(dto.getFulfilledBy());
        return entity;
    }
}
