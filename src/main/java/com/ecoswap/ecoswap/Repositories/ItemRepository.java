package com.ecoswap.ecoswap.Repositories;

import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.EstadoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Page<Item> findByEstado(EstadoItem estado, Pageable pageable);
    Page<Item> findByEstadoAndCategoria(EstadoItem estado, String categoria, Pageable pageable);
    Page<Item> findByEstadoAndFechaCreacionAfter(EstadoItem estado, LocalDateTime fecha, Pageable pageable);
    Page<Item> findByEstadoAndCategoriaAndFechaCreacionAfter(EstadoItem estado, String categoria, LocalDateTime fecha, Pageable pageable);
}