package com.ecoswap.ecoswap.model;

public enum EstadoItem {
    DISPONIBLE,     // El artículo está listo para ser intercambiado.
    RESERVADO,      // Un usuario ha iniciado el proceso de reserva/intercambio.
    INTERCAMBIADO   // La transacción se ha completado.
}