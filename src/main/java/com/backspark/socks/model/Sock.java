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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getCottonPart() {
        return cottonPart;
    }

    public void setCottonPart(Integer cottonPart) {
        this.cottonPart = cottonPart;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Sock{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", cottonPart=" + cottonPart +
                ", quantity=" + quantity +
                '}';
    }
}