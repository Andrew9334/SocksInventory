package com.backspark.socks.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) для работы с информацией о носках.
 */
@Data // Генерирует геттеры, сеттеры, equals, hashCode, toString
@NoArgsConstructor // Генерирует конструктор без аргументов
@AllArgsConstructor // Генерирует конструктор с аргументами для всех полей
public class SockDto {

    @NotBlank(message = "Color cannot be blank")
    private String color;

    @NotNull(message = "Cotton part cannot be null")
    @Min(value = 0, message = "Cotton part must be at least 0")
    private Integer cottonPart;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
