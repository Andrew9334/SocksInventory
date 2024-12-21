package com.backspark.socks.service;

import com.backspark.socks.dto.SockDto;
import com.backspark.socks.exception.FileProcessingException;
import com.backspark.socks.exception.InsufficientSocksException;
import com.backspark.socks.model.Sock;
import com.backspark.socks.repository.SockRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SockService {

    private static final Logger logger = LoggerFactory.getLogger(SockService.class);

    private final SockRepository sockRepository;

    public SockService(SockRepository sockRepository) {
        this.sockRepository = sockRepository;
    }

    @Transactional
    public void registerIncome(SockDto sockDto) {
        logger.info("Registering income: {}", sockDto);
        Sock sock = findOrCreateSock(sockDto.getColor(), sockDto.getCottonPart());
        sock.setQuantity(sock.getQuantity() + sockDto.getQuantity());
        sockRepository.save(sock);
    }

    @Transactional
    public void registerOutcome(SockDto sockDto) {
        logger.info("Registering outcome: {}", sockDto);
        Sock sock = sockRepository.findByColorAndCottonPart(sockDto.getColor(), sockDto.getCottonPart())
                .stream()
                .findFirst()
                .orElseThrow(() -> new InsufficientSocksException("Socks not found."));

        if (sock.getQuantity() < sockDto.getQuantity()) {
            throw new InsufficientSocksException("Not enough socks in stock.");
        }

        sock.setQuantity(sock.getQuantity() - sockDto.getQuantity());
        sockRepository.save(sock);
    }

    public List<Sock> getSocks(String color, Integer cottonPartMin, Integer cottonPartMax, Pageable pageable) {
        if (color == null && cottonPartMin == null && cottonPartMax == null) {
            return sockRepository.findAll();
        }
        return sockRepository.findByColorAndCottonPartBetween(
                color,
                cottonPartMin != null ? cottonPartMin : 0,
                cottonPartMax != null ? cottonPartMax : 100
        );
    }

    @Transactional
    public void uploadBatch(MultipartFile file) {
        logger.info("Uploading batch from file: {}", file.getOriginalFilename());
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String color = row.getCell(0).getStringCellValue();
                int cottonPart = (int) row.getCell(1).getNumericCellValue();
                int quantity = (int) row.getCell(2).getNumericCellValue();

                Sock sock = findOrCreateSock(color, cottonPart);
                sock.setQuantity(sock.getQuantity() + quantity);
                sockRepository.save(sock);
            }
        } catch (IOException e) {
            logger.error("Error processing file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Failed to process the Excel file", e);
        }
    }

    private Sock findOrCreateSock(String color, Integer cottonPart) {
        return sockRepository.findByColorAndCottonPart(color, cottonPart)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Sock newSock = new Sock();
                    newSock.setColor(color);
                    newSock.setCottonPart(cottonPart);
                    newSock.setQuantity(0);
                    return newSock;
                });
    }

}
