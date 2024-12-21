package com.backspark.socks.controller;

import com.backspark.socks.dto.SockDto;
import com.backspark.socks.model.Sock;
import com.backspark.socks.service.SockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Socks API", description = "Управление запасами носков")
public class SockController {

    private static final Logger logger = LoggerFactory.getLogger(SockController.class);

    private final SockService sockService;

    public SockController(SockService sockService) {
        this.sockService = sockService;
    }

    @PostMapping("/income")
    @Operation(summary = "Регистрация прихода носков", description = "Увеличивает количество носков на складе.")
    public ResponseEntity<Void> registerIncome(@Valid @RequestBody SockDto sockDto) {
        logger.info("Received request to register income: {}", sockDto);
        sockService.registerIncome(sockDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/outcome")
    @Operation(summary = "Регистрация отпуска носков", description = "Уменьшает количество носков на складе, если их хватает.")
    public ResponseEntity<Void> registerOutcome(@Valid @RequestBody SockDto sockDto) {
        logger.info("Received request to register outcome: {}", sockDto);
        sockService.registerOutcome(sockDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Получение списка носков", description = "Возвращает список носков с возможностью фильтрации, сортировки и пагинации.")
    public ResponseEntity<List<Sock>> getSocks(
            @Parameter(description = "Цвет носков") @RequestParam(required = false) String color,
            @Parameter(description = "Минимальное содержание хлопка") @RequestParam(required = false) Integer cottonPartMin,
            @Parameter(description = "Максимальное содержание хлопка") @RequestParam(required = false) Integer cottonPartMax,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Сортировка") @RequestParam(defaultValue = "color") String sortBy) {
        logger.info("Received request to get socks with filters - color: {}, cottonPartMin: {}, cottonPartMax: {}", color, cottonPartMin, cottonPartMax);
        List<Sock> socks = sockService.getSocks(color, cottonPartMin, cottonPartMax, PageRequest.of(page, size, Sort.by(sortBy)));
        return ResponseEntity.ok(socks);
    }

    @PostMapping("/batch")
    @Operation(summary = "Загрузка партий носков из файла", description = "Позволяет загружать партии носков из Excel файла.")
    public ResponseEntity<String> uploadBatch(@Parameter(description = "Excel файл с партиями носков") @RequestParam("file") MultipartFile file) {
        logger.info("Received request to upload batch file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("Upload failed: file is empty");
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            logger.warn("Upload failed: invalid file format");
            return ResponseEntity.badRequest().body("Invalid file format. Only .xlsx files are supported.");
        }

        sockService.uploadBatch(file);
        return ResponseEntity.ok("File uploaded successfully");
    }
}
