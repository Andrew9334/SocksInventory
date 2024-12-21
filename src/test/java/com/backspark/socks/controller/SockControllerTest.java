package com.backspark.socks.controller;

import com.backspark.socks.dto.SockDto;
import com.backspark.socks.model.Sock;
import com.backspark.socks.service.SockService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SockController.class)
@Import(SockControllerTest.TestConfig.class)
class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SockService sockService() {
            return Mockito.mock(SockService.class);
        }
    }

    @Autowired
    private SockService sockService;

    @Test
    void registerIncomeShouldReturnOk() throws Exception {
        doNothing().when(sockService).registerIncome(any(SockDto.class));

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "red",
                                    "cottonPart": 50,
                                    "quantity": 10
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void getSocksShouldReturnFilteredList() throws Exception {
        Sock sock = new Sock();
        sock.setColor("red");
        sock.setCottonPart(50);
        sock.setQuantity(10);

        when(sockService.getSocks(any(), anyInt(), anyInt(), any())).thenReturn(List.of(sock));

        mockMvc.perform(get("/api/socks")
                        .param("color", "red")
                        .param("cottonPartMin", "30")
                        .param("cottonPartMax", "70"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[0].cottonPart").value(50))
                .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    void uploadBatchShouldReturnOk() throws Exception {
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "socks.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                out.toByteArray()
        );

        doNothing().when(sockService).uploadBatch(any());

        mockMvc.perform(multipart("/api/socks/batch")
                        .file(file))
                .andExpect(status().isOk());

        verify(sockService, times(1)).uploadBatch(any());
    }
}
