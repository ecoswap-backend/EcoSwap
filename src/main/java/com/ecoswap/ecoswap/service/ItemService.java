package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.ItemRepository;
import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO;
import com.ecoswap.ecoswap.model.EstadoItem;
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository; // Necesario para buscar al dueño/usuario

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // --- Mapeador (Mapper) ---
    private ItemDTO mapToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setTitulo(item.getTitulo());
        dto.setDescripcion(item.getDescripcion());
        dto.setFechaCreacion(item.getFechaCreacion());
        dto.setEstado(item.getEstado().name()); 
        dto.setPuntosAGanar(item.getPuntosAGanar());
        dto.setCategoria(item.getCategoria());
        dto.setImagenPrincipal(item.getImagenPrincipal());

        if (item.getDueno() != null) {
            dto.setDuenoNombre(item.getDueno().getNombre());
            dto.setDuenoImagenPerfil(item.getDueno().getImagenPerfil());
        }
        return dto;
    }

    // 🚩 1. CREACIÓN DE ARTÍCULO (Requiere Autenticación)
    public ItemDTO crearItem(ItemRegistroDTO registroDTO, Long duenoId) {
        User dueno = userRepository.findById(duenoId)
            .orElseThrow(() -> new RuntimeException("Dueño no encontrado"));

        Item item = new Item();
        item.setDueno(dueno);
        item.setTitulo(registroDTO.getTitulo());
        item.setDescripcion(registroDTO.getDescripcion());
        item.setPuntosAGanar(registroDTO.getPuntosAGanar());
        item.setCategoria(registroDTO.getCategoria());
        item.setImagenPrincipal(registroDTO.getImagenPrincipal());
        item.setEstado(EstadoItem.DISPONIBLE);
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }

    // 🚩 2. FILTRADO Y PAGINACIÓN (Página principal - Sin autenticación)
    public Page<ItemDTO> obtenerItemsFiltrados(
            int page, 
            int size, 
            String categoria, 
            LocalDate fechaDesde) {
        
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime fechaHoraDesde = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
        Page<Item> itemPage;

        // Lógica para filtrar los artículos disponibles
        if (categoria != null && fechaDesde != null) {
            itemPage = itemRepository.findByEstadoAndCategoriaAndFechaCreacionAfter(
                EstadoItem.DISPONIBLE, categoria, fechaHoraDesde, pageable);
        } else if (categoria != null) {
            itemPage = itemRepository.findByEstadoAndCategoria(
                EstadoItem.DISPONIBLE, categoria, pageable);
        } else if (fechaDesde != null) {
            itemPage = itemRepository.findByEstadoAndFechaCreacionAfter(
                EstadoItem.DISPONIBLE, fechaHoraDesde, pageable);
        } else {
            itemPage = itemRepository.findByEstado(EstadoItem.DISPONIBLE, pageable);
        }
        
        return itemPage.map(this::mapToDTO);
    }
    
    // 🚩 3. DETALLE DE ARTÍCULO (Sin autenticación)
    public ItemDTO obtenerItemPorId(Long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
            
        return mapToDTO(item);
    }
    
    // 🚩 4. HACER UNA RESERVA (Requiere Autenticación)
    public ItemDTO reservarItem(Long itemId, Long reservadorId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        if (item.getEstado() != EstadoItem.DISPONIBLE) {
            throw new RuntimeException("El artículo ya está reservado o intercambiado.");
        }
        if (item.getDueno().getId().equals(reservadorId)) {
            throw new RuntimeException("No puedes reservar tu propio artículo.");
        }
        
        // Restringir que solo un usuario pueda reservar a la vez (por defecto lo hacemos aquí)
        User reservador = userRepository.findById(reservadorId)
            .orElseThrow(() -> new RuntimeException("Usuario reservador no encontrado"));

        item.setEstado(EstadoItem.RESERVADO);
        item.setReservadoPor(reservador);
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }
    
    // 🚩 5. ACEPTAR RESERVA Y COMPLETAR INTERCAMBIO (Requiere Autenticación del Dueño)
    // Requisito: Aceptar reserva y cambiar a entregado, ingresar puntos
    public ItemDTO completarIntercambio(Long itemId, Long duenoId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
            
        // Validar que el usuario que intenta completar la reserva es el dueño
        if (!item.getDueno().getId().equals(duenoId)) {
            throw new RuntimeException("Solo el dueño puede completar el intercambio.");
        }
        if (item.getEstado() != EstadoItem.RESERVADO || item.getReservadoPor() == null) {
            throw new RuntimeException("El artículo no está en estado RESERVADO.");
        }
        
        // 1. Cambiar estado del artículo
        item.setEstado(EstadoItem.INTERCAMBIADO);
        
        // 2. Ingresar puntos al dueño (duenoId)
        User dueno = item.getDueno();
        dueno.setPuntos(dueno.getPuntos() + item.getPuntosAGanar());
        userRepository.save(dueno); // Guardar los puntos actualizados
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }
}