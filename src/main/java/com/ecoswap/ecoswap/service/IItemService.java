package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.dto.ItemDTO;
import com.ecoswap.ecoswap.dto.ItemRegistroDTO;
import com.ecoswap.ecoswap.dto.ItemUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface IItemService {
    ItemDTO crearItem(ItemRegistroDTO registroDTO, MultipartFile imagenPrincipal, String userEmail);
    Page<ItemDTO> obtenerItemsFiltrados(int page, int size, String categoria, LocalDate fechaDesde);
    ItemDTO obtenerItemPorId(Long itemId);
    ItemDTO editarItem(Long itemId, ItemUpdateDTO updateDTO, MultipartFile nuevaImagen, String duenoEmail);
    void eliminarItem(Long itemId, String duenoEmail);
    ItemDTO reservarItem(Long itemId, String reservadorEmail);
    ItemDTO eliminarReserva(Long itemId, String reservadorEmail);
    ItemDTO completarIntercambio(Long itemId, String duenoEmail);
}
