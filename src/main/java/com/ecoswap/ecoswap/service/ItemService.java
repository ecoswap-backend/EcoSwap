package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.ItemRepository;
import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO;
import com.ecoswap.ecoswap.dto.ItemUpdateDTO;
import com.ecoswap.ecoswap.exception.InvalidOperationException;
import com.ecoswap.ecoswap.exception.ResourceNotFoundException; 
import com.ecoswap.ecoswap.model.EstadoItem;
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository; 
    
    // Directorio para simular el almacenamiento local (NO URL)
    private final Path storageLocation = Paths.get("uploads").toAbsolutePath().normalize();


    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        
        try {
            // Asegura que el directorio 'uploads' exista
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio de almacenamiento: " + e.getMessage());
        }
    }

    // --- Métodos de Ayuda (Map & File Management) ---

    private ItemDTO mapToDTO(Item item) {
        
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setTitulo(item.getTitulo());
        dto.setDescripcion(item.getDescripcion());
        dto.setFechaCreacion(item.getFechaCreacion());
        dto.setEstado(item.getEstado().name()); 
        dto.setPuntosAGanar(item.getPuntosAGanar());
        dto.setCategoria(item.getCategoria());
        // Se almacena el nombre del archivo local, el cliente lo pedirá luego.
        dto.setImagenPrincipal(item.getImagenPrincipal()); 

        if (item.getDueno() != null) {
            dto.setDuenoNombre(item.getDueno().getNombre());
            dto.setDuenoImagenPerfil(item.getDueno().getImagenPerfil());
        }
        return dto;
    }
    
    /** Almacena el archivo físicamente en el directorio 'uploads'. */
    private String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = this.storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return fileName; 
        } catch (IOException ex) {
            throw new InvalidOperationException("No se pudo almacenar el archivo: " + ex.getMessage());
        }
    }
    
    /** Elimina el archivo físico. */
    private void deleteFile(String fileName) {
        if (fileName != null) {
            try {
                Path filePath = this.storageLocation.resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Error al eliminar archivo: " + e.getMessage());
            }
        }
    }

    private User getUserByEmail(String email) {
        User user = userRepository.findByMail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con el correo: " + email);
        }
        return user;
    }

    // --- Métodos de Negocio (CRUD y Transacciones) ---
    
    @Transactional
    public ItemDTO crearItem(ItemRegistroDTO registroDTO, MultipartFile imagenPrincipal, String userEmail) {
        
        User dueno = getUserByEmail(userEmail);
        String imagePath = storeFile(imagenPrincipal); 

        Item item = new Item();
        item.setDueno(dueno);
        item.setTitulo(registroDTO.getTitulo());
        item.setDescripcion(registroDTO.getDescripcion());
        item.setPuntosAGanar(registroDTO.getPuntosAGanar());
        item.setCategoria(registroDTO.getCategoria());
        item.setImagenPrincipal(imagePath); 
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

        // Mostrar solo artículos DISPONIBLES y aplicar filtros
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
    public ItemDTO editarItem(Long itemId, ItemUpdateDTO updateDTO, MultipartFile nuevaImagen, String duenoEmail) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));
            
        if (!item.getDueno().getMail().equals(duenoEmail)) {
            throw new InvalidOperationException("No tienes permiso para editar este artículo.");
        }
        
        // Regla de negocio: Solo puedes editar artículos disponibles
        if (item.getEstado() != EstadoItem.DISPONIBLE) {
             throw new InvalidOperationException("Solo puedes editar artículos en estado DISPONIBLE.");
        }

        if (updateDTO.getTitulo() != null) item.setTitulo(updateDTO.getTitulo());
        if (updateDTO.getDescripcion() != null) item.setDescripcion(updateDTO.getDescripcion());
        if (updateDTO.getCategoria() != null) item.setCategoria(updateDTO.getCategoria());
        if (updateDTO.getPuntosAGanar() != null) item.setPuntosAGanar(updateDTO.getPuntosAGanar());

        // Manejo de la imagen: Sustituir o Eliminar
        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            deleteFile(item.getImagenPrincipal()); 
            String newImagePath = storeFile(nuevaImagen);
            item.setImagenPrincipal(newImagePath);
        } else if ("true".equalsIgnoreCase(updateDTO.getEliminarImagenActual())) {
            deleteFile(item.getImagenPrincipal());
            item.setImagenPrincipal(null);
        }
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }

    @Transactional
    public void eliminarItem(Long itemId, String duenoEmail) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));
            
        if (!item.getDueno().getMail().equals(duenoEmail)) {
            throw new InvalidOperationException("No tienes permiso para eliminar este artículo.");
        }
        
        // Se elimina el archivo físico
        deleteFile(item.getImagenPrincipal());
        itemRepository.delete(item);
    }

    @Transactional
    public ItemDTO reservarItem(Long itemId, String reservadorEmail) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));

        User reservador = getUserByEmail(reservadorEmail);

        if (item.getEstado() != EstadoItem.DISPONIBLE) {
            // Restringe que un artículo pueda ser reservado por dos usuarios a la vez
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
    public ItemDTO eliminarReserva(Long itemId, String reservadorEmail) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + itemId));

        User reservador = getUserByEmail(reservadorEmail);

        if (item.getEstado() != EstadoItem.RESERVADO) {
            throw new InvalidOperationException("El artículo no está reservado.");
        }

        if (item.getReservadoPor() == null || !item.getReservadoPor().getId().equals(reservador.getId())) {
            throw new InvalidOperationException("Solo el usuario que hizo la reserva puede cancelarla.");
        }
        
        item.setEstado(EstadoItem.DISPONIBLE);
        item.setReservadoPor(null); 
        
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
        
        // ACUMULAR PUNTOS: Cambiar ropa por puntos (al dueño)
        User duenoActualizado = item.getDueno();
        duenoActualizado.setPuntos(duenoActualizado.getPuntos() + item.getPuntosAGanar());
        userRepository.save(duenoActualizado); 
        
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }
}