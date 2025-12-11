package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO; 
import com.ecoswap.ecoswap.dto.ItemUpdateDTO;
import com.ecoswap.ecoswap.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // 1. PUBLICAR ARTÍCULO (POST - MULTIPART_FORM_DATA)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> crearItem(
            @Valid @ModelAttribute ItemRegistroDTO itemRegistroDTO, 
            @RequestPart(value = "imagenPrincipal", required = false) MultipartFile imagenPrincipal, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userEmail = userDetails.getUsername(); 
        ItemDTO nuevoItem = itemService.crearItem(itemRegistroDTO, imagenPrincipal, userEmail);

        return new ResponseEntity<>(nuevoItem, HttpStatus.CREATED);
    }

    // 2. EXPLORACIÓN Y FILTROS
    @GetMapping
    public ResponseEntity<Page<ItemDTO>> obtenerItemsFiltrados(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "30") int size, 
        @RequestParam(required = false) String categoria, 
        @RequestParam(required = false) LocalDate fechaDesde) {
        
        Page<ItemDTO> items = itemService.obtenerItemsFiltrados(page, size, categoria, fechaDesde);
        return ResponseEntity.ok(items);
    }

    // 3. OBTENER DETALLE POR ID
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> obtenerItemPorId(@PathVariable Long itemId) {
        ItemDTO item = itemService.obtenerItemPorId(itemId);
        return ResponseEntity.ok(item);
    }
    
    // 4A. EDITAR ARTÍCULO (CON IMAGEN)
    @PutMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> editarItemConImagen(
            @PathVariable Long itemId,
            @Valid @ModelAttribute ItemUpdateDTO itemUpdateDTO, // Datos de texto/número
            @RequestPart(value = "imagen", required = false) MultipartFile nuevaImagen, // Archivo de imagen
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        ItemDTO itemActualizado = itemService.editarItem(itemId, itemUpdateDTO, nuevaImagen, userEmail);
        return ResponseEntity.ok(itemActualizado);
    }

    // 4B. EDITAR ARTÍCULO (SOLO DATOS DE TEXTO/JSON) [NUEVO MÉTODO/SOLUCIÓN AL ERROR]
    @PatchMapping(value = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> editarItemSoloDatos(
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDTO itemUpdateDTO, // Acepta JSON directamente
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        // Llamamos al servicio con null en la imagen
        ItemDTO itemActualizado = itemService.editarItem(itemId, itemUpdateDTO, null, userEmail);
        return ResponseEntity.ok(itemActualizado);
    }

    // 5. ELIMINAR ARTÍCULO
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        itemService.eliminarItem(itemId, userEmail);
        return ResponseEntity.noContent().build();
    }
    
    // 6. RESERVAR ARTÍCULO
    @PostMapping("/{itemId}/reserve")
    public ResponseEntity<ItemDTO> reservarItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String reservadorEmail = userDetails.getUsername();
        ItemDTO itemReservado = itemService.reservarItem(itemId, reservadorEmail);

        return ResponseEntity.ok(itemReservado);
    }

    // 7. CANCELAR RESERVA
    @DeleteMapping("/{itemId}/reserve")
    public ResponseEntity<ItemDTO> eliminarReserva(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String reservadorEmail = userDetails.getUsername();
        ItemDTO itemSinReserva = itemService.eliminarReserva(itemId, reservadorEmail);
        
        return ResponseEntity.ok(itemSinReserva);
    }

    // 8. COMPLETAR INTERCAMBIO Y TRANSFERENCIA DE PUNTOS
    @PostMapping("/{itemId}/complete")
    public ResponseEntity<ItemDTO> completarIntercambio(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String duenoEmail = userDetails.getUsername();
        ItemDTO itemCompletado = itemService.completarIntercambio(itemId, duenoEmail);

        return ResponseEntity.ok(itemCompletado);
    }
}