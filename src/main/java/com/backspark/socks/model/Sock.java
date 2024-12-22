package com.backspark.socks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a sock item in the inventory.
 */
@Entity
@Table(name = "socks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sock {

    /**
     * Unique identifier of the sock.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Color of the sock.
     */
    @Column(nullable = false)
    @NotBlank(message = "Color cannot be blank")
    private String color;

    /**
     * Percentage of cotton in the sock.
     */
    @Column(nullable = false)
    @Min(value = 0, message = "Cotton part must be at least 0")
    @Max(value = 100, message = "Cotton part cannot exceed 100")
    private Integer cottonPart;

    /**
     * Quantity of socks in inventory.
     */
    @Column(nullable = false)
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}