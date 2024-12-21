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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Sock sock = sockRepository.findByColorAndCottonPart(sockDto.getColor(), sockDto.getCottonPart())
                .stream()
                .findFirst()
                .orElse(new Sock());

        sock.setColor(sockDto.getColor());
        sock.setCottonPart(sockDto.getCottonPart());
        sock.setQuantity(sock.getQuantity() == null ? sockDto.getQuantity() : sock.getQuantity() + sockDto.getQuantity());

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

    public List<Sock> getSocks(String color, Integer cottonPartMin, Integer cottonPartMax) {
        logger.info("Fetching socks with filters - color: {}, cottonPartMin: {}, cottonPartMax: {}", color, cottonPartMin, cottonPartMax);
        return sockRepository.findAll().stream()
                .filter(sock -> (color == null || sock.getColor().equalsIgnoreCase(color)) &&
                        (cottonPartMin == null || sock.getCottonPart() >= cottonPartMin) &&
                        (cottonPartMax == null || sock.getCottonPart() <= cottonPartMax))
                .toList();
    }

    @Transactional
    public void uploadBatch(MultipartFile file) {
        logger.info("Uploading batch from file: {}", file.getOriginalFilename());
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Sock> socks = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String color = row.getCell(0).getStringCellValue();
                int cottonPart = (int) row.getCell(1).getNumericCellValue();
                int quantity = (int) row.getCell(2).getNumericCellValue();

                Sock sock = new Sock();
                sock.setColor(color);
                sock.setCottonPart(cottonPart);
                sock.setQuantity(quantity);

                socks.add(sock);
            }

            sockRepository.saveAll(socks);
        } catch (IOException e) {
            logger.error("Error processing file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Failed to process the Excel file", e);
        }
    }
}
