package com.backspark.socks.service;

import com.backspark.socks.dto.SockDto;
import com.backspark.socks.exception.InsufficientSocksException;
import com.backspark.socks.model.Sock;
import com.backspark.socks.repository.SockRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SockServiceTest {

    private SockRepository sockRepository;
    private SockService sockService;

    @BeforeEach
    void setUp() {
        sockRepository = mock(SockRepository.class);
        sockService = new SockService(sockRepository);
    }

    @Test
    void registerIncomeShouldAddNewSocks_whenSockDoesNotExist() {
        SockDto sockDto = new SockDto("red", 50, 10);

        when(sockRepository.findByColorAndCottonPart("red", 50)).thenReturn(Optional.empty());

        sockService.registerIncome(sockDto);

        ArgumentCaptor<Sock> sockCaptor = ArgumentCaptor.forClass(Sock.class);
        verify(sockRepository, times(1)).save(sockCaptor.capture());

        Sock savedSock = sockCaptor.getValue();
        assertEquals("red", savedSock.getColor());
        assertEquals(50, savedSock.getCottonPart());
        assertEquals(10, savedSock.getQuantity());
    }

    @Test
    void registerOutcomeShouldThrowException_whenSocksNotAvailable() {
        SockDto sockDto = new SockDto("red", 50, 10);

        when(sockRepository.findByColorAndCottonPart("red", 50)).thenReturn(Optional.empty());

        assertThrows(InsufficientSocksException.class, () -> sockService.registerOutcome(sockDto));

        verify(sockRepository, times(1)).findByColorAndCottonPart("red", 50);
    }

    @Test
    void uploadBatchShouldSaveSocksFromExcelFile() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("color");
        header.createCell(1).setCellValue("cottonPart");
        header.createCell(2).setCellValue("quantity");

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("red");
        row1.createCell(1).setCellValue(50);
        row1.createCell(2).setCellValue(10);

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("blue");
        row2.createCell(1).setCellValue(70);
        row2.createCell(2).setCellValue(5);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(out.toByteArray()));

        System.out.println("Тест: Перед вызовом uploadBatch");
        sockService.uploadBatch(file);
        System.out.println("Тест: После вызова uploadBatch");

        verify(sockRepository, times(1)).saveAll(anyList());
    }

}
