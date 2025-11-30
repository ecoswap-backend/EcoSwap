package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.ItemRepository;
import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO;
import com.ecoswap.ecoswap.exception.InvalidOperationException;
import com.ecoswap.ecoswap.model.EstadoItem;
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.User;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository; 

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

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

    private User getUserByEmail(String email) {
        User user = userRepository.findByMail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con el correo: " + email);
        }
        return user;
    }

    public ItemDTO crearItem(ItemRegistroDTO registroDTO, String userEmail) {
       
        User dueno = getUserByEmail(userEmail);

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

    public Page<ItemDTO> obtenerItemsFiltrados(
            int page, 
            int size, 
            String categoria, 
            LocalDate fechaDesde) {
        
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime fechaHoraDesde = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
        Page<Item> itemPage;

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
    
    public ItemDTO obtenerItemPorId(Long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));
        return mapToDTO(item);
    }
    
    @Transactional
    public ItemDTO reservarItem(Long itemId, String reservadorEmail) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));

        User reservador = getUserByEmail(reservadorEmail);

        if (item.getEstado() != EstadoItem.DISPONIBLE) {
            throw new InvalidOperationException("El artículo ya está reservado o intercambiado.");
        }

        if (item.getDueno().getId().equals(reservador.getId())) {
            throw new InvalidOperationException("No puedes reservar tu propio artículo.");
        }
       
        item.setEstado(EstadoItem.RESERVADO);
        item.setReservadoPor(reservador);
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }

    @Transactional
    public ItemDTO completarIntercambio(Long itemId, String duenoEmail) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));
        
        User dueno = getUserByEmail(duenoEmail);

        if (!item.getDueno().getId().equals(dueno.getId())) {
            throw new InvalidOperationException("Solo el dueño puede completar el intercambio.");
        }
  
        if (item.getEstado() != EstadoItem.RESERVADO || item.getReservadoPor() == null) {
            throw new InvalidOperationException("El artículo no está en estado RESERVADO.");
        }
    
        item.setEstado(EstadoItem.INTERCAMBIADO);
        
        dueno.setPuntos(dueno.getPuntos() + item.getPuntosAGanar());
        userRepository.save(dueno); 
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }
}