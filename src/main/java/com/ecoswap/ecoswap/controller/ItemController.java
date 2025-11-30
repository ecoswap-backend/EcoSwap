package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO; 
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    
    @PostMapping
    public ResponseEntity<?> crearItem(
            @Valid @RequestBody ItemRegistroDTO itemRegistroDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
           
            String userEmail = userDetails.getUsername(); 

            Item nuevoItem = itemService.crearItem(itemRegistroDTO, userEmail);

            return new ResponseEntity<>(nuevoItem, HttpStatus.CREATED);

        } catch (RuntimeException e) {
           
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

  
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> obtenerItemPorId(@PathVariable Long itemId) {
        ItemDTO item = itemService.obtenerItemPorId(itemId);
        
        if (item != null) {
         return ResponseEntity.ok(item);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}