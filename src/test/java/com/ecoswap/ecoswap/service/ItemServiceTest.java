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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;

    private User mockOwner;
    private User mockBuyer;
    private Item mockItem;
    private ItemRegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        mockOwner = new User();
        mockOwner.setId(1L);
        mockOwner.setNombre("Owner");
        mockOwner.setMail("owner@example.com");
        mockOwner.setPuntos(100);

        mockBuyer = new User();
        mockBuyer.setId(2L);
        mockBuyer.setNombre("Buyer");
        mockBuyer.setMail("buyer@example.com");
        mockBuyer.setPuntos(50);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setTitulo("Test Item");
        mockItem.setDescripcion("Test Description");
        mockItem.setCategoria("Camisas");
        mockItem.setTalla("M");
        mockItem.setEstadoPrenda("Nuevo");
        mockItem.setUbicacion("Madrid");
        mockItem.setEstado(EstadoItem.DISPONIBLE);
        mockItem.setPuntosAGanar(10);
        mockItem.setDueno(mockOwner);
        mockItem.setFechaCreacion(LocalDateTime.now());

        registroDTO = new ItemRegistroDTO("New Item", "Pantalones");
        registroDTO.setDescripcion("New Description");
        registroDTO.setTalla("L");
        registroDTO.setEstado("Como nuevo");
        registroDTO.setUbicacion("Barcelona");
    }

    @Test
    void crearItem_Success() {
        when(userRepository.findByMail("owner@example.com")).thenReturn(mockOwner);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemDTO result = itemService.crearItem(registroDTO, null, "owner@example.com");

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void crearItem_WithCustomPoints() {
        registroDTO.setPuntosAGanar(20);
        when(userRepository.findByMail("owner@example.com")).thenReturn(mockOwner);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemDTO result = itemService.crearItem(registroDTO, null, "owner@example.com");

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void crearItem_UserNotFound() {
        when(userRepository.findByMail("notfound@example.com")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.crearItem(registroDTO, null, "notfound@example.com");
        });
    }

    @Test
    void obtenerItemsFiltrados_NoFilters() {
        List<Item> items = new ArrayList<>();
        items.add(mockItem);
        Page<Item> page = new PageImpl<>(items);

        when(itemRepository.findByEstado(eq(EstadoItem.DISPONIBLE), any(Pageable.class))).thenReturn(page);

        Page<ItemDTO> result = itemService.obtenerItemsFiltrados(0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void obtenerItemsFiltrados_WithCategoria() {
        List<Item> items = new ArrayList<>();
        items.add(mockItem);
        Page<Item> page = new PageImpl<>(items);

        when(itemRepository.findByEstadoAndCategoria(eq(EstadoItem.DISPONIBLE), eq("Camisas"), any(Pageable.class)))
                .thenReturn(page);

        Page<ItemDTO> result = itemService.obtenerItemsFiltrados(0, 10, "Camisas", null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void obtenerItemsFiltrados_WithFecha() {
        List<Item> items = new ArrayList<>();
        items.add(mockItem);
        Page<Item> page = new PageImpl<>(items);
        LocalDate fecha = LocalDate.now();

        when(itemRepository.findByEstadoAndFechaCreacionAfter(eq(EstadoItem.DISPONIBLE), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        Page<ItemDTO> result = itemService.obtenerItemsFiltrados(0, 10, null, fecha);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void obtenerItemsFiltrados_WithCategoriaAndFecha() {
        List<Item> items = new ArrayList<>();
        items.add(mockItem);
        Page<Item> page = new PageImpl<>(items);
        LocalDate fecha = LocalDate.now();

        when(itemRepository.findByEstadoAndCategoriaAndFechaCreacionAfter(
                eq(EstadoItem.DISPONIBLE), eq("Camisas"), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        Page<ItemDTO> result = itemService.obtenerItemsFiltrados(0, 10, "Camisas", fecha);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void obtenerItemPorId_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));

        ItemDTO result = itemService.obtenerItemPorId(1L);

        assertNotNull(result);
        assertEquals("Test Item", result.getTitulo());
    }

    @Test
    void obtenerItemPorId_NotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.obtenerItemPorId(999L);
        });
    }

    @Test
    void editarItem_Success() {
        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setTitulo("Updated Title");
        updateDTO.setDescripcion("Updated Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemDTO result = itemService.editarItem(1L, updateDTO, null, "owner@example.com");

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void editarItem_NotOwner() {
        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setTitulo("Updated Title");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));

        assertThrows(InvalidOperationException.class, () -> {
            itemService.editarItem(1L, updateDTO, null, "other@example.com");
        });
    }

    @Test
    void editarItem_NotDisponible() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setTitulo("Updated Title");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));

        assertThrows(InvalidOperationException.class, () -> {
            itemService.editarItem(1L, updateDTO, null, "owner@example.com");
        });
    }

    @Test
    void eliminarItem_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        doNothing().when(itemRepository).delete(mockItem);

        assertDoesNotThrow(() -> {
            itemService.eliminarItem(1L, "owner@example.com");
        });

        verify(itemRepository).delete(mockItem);
    }

    @Test
    void eliminarItem_NotOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));

        assertThrows(InvalidOperationException.class, () -> {
            itemService.eliminarItem(1L, "other@example.com");
        });
    }

    @Test
    void reservarItem_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("buyer@example.com")).thenReturn(mockBuyer);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemDTO result = itemService.reservarItem(1L, "buyer@example.com");

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void reservarItem_AlreadyReserved() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("buyer@example.com")).thenReturn(mockBuyer);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.reservarItem(1L, "buyer@example.com");
        });
    }

    @Test
    void reservarItem_OwnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("owner@example.com")).thenReturn(mockOwner);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.reservarItem(1L, "owner@example.com");
        });
    }

    @Test
    void eliminarReserva_Success() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        mockItem.setReservadoPor(mockBuyer);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("buyer@example.com")).thenReturn(mockBuyer);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemDTO result = itemService.eliminarReserva(1L, "buyer@example.com");

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void eliminarReserva_NotReserved() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("buyer@example.com")).thenReturn(mockBuyer);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.eliminarReserva(1L, "buyer@example.com");
        });
    }

    @Test
    void eliminarReserva_NotReservedByUser() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        mockItem.setReservadoPor(mockOwner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("buyer@example.com")).thenReturn(mockBuyer);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.eliminarReserva(1L, "buyer@example.com");
        });
    }

    @Test
    void completarIntercambio_Success() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        mockItem.setReservadoPor(mockBuyer);
        mockBuyer.setPuntos(100);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("owner@example.com")).thenReturn(mockOwner);
        when(userRepository.save(any(User.class))).thenReturn(mockBuyer, mockOwner);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemDTO result = itemService.completarIntercambio(1L, "owner@example.com");

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void completarIntercambio_NotOwner() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        mockItem.setReservadoPor(mockBuyer);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("buyer@example.com")).thenReturn(mockBuyer);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.completarIntercambio(1L, "buyer@example.com");
        });
    }

    @Test
    void completarIntercambio_NotReserved() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("owner@example.com")).thenReturn(mockOwner);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.completarIntercambio(1L, "owner@example.com");
        });
    }

    @Test
    void completarIntercambio_InsufficientPoints() {
        mockItem.setEstado(EstadoItem.RESERVADO);
        mockItem.setReservadoPor(mockBuyer);
        mockItem.setPuntosAGanar(100);
        mockBuyer.setPuntos(50);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(userRepository.findByMail("owner@example.com")).thenReturn(mockOwner);

        assertThrows(InvalidOperationException.class, () -> {
            itemService.completarIntercambio(1L, "owner@example.com");
        });
    }
}
