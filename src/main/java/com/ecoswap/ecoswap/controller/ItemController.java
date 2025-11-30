package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO; 
import com.ecoswap.ecoswap.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDTO> crearItem(
            @Valid @RequestBody ItemRegistroDTO itemRegistroDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        
        String userEmail = userDetails.getUsername(); 
        ItemDTO nuevoItem = itemService.crearItem(itemRegistroDTO, userEmail);

        return new ResponseEntity<>(nuevoItem, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<Page<ItemDTO>> obtenerItemsFiltrados(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "10") int size, 
        @RequestParam(required = false) String categoria, 
        @RequestParam(required = false) LocalDate fechaDesde) {
        
        Page<ItemDTO> items = itemService.obtenerItemsFiltrados(page, size, categoria, fechaDesde);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> obtenerItemPorId(@PathVariable Long itemId) {
     
        ItemDTO item = itemService.obtenerItemPorId(itemId);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/{itemId}/reserve")
    public ResponseEntity<ItemDTO> reservarItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String reservadorEmail = userDetails.getUsername();
        ItemDTO itemReservado = itemService.reservarItem(itemId, reservadorEmail);
 
        return ResponseEntity.ok(itemReservado);
    }

    @PostMapping("/{itemId}/complete")
    public ResponseEntity<ItemDTO> completarIntercambio(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String duenoEmail = userDetails.getUsername();
        ItemDTO itemCompletado = itemService.completarIntercambio(itemId, duenoEmail);
 
        return ResponseEntity.ok(itemCompletado);
    }
}