package com.backspark.socks.controller;

import com.backspark.socks.dto.SockDto;
import com.backspark.socks.model.Sock;
import com.backspark.socks.service.SockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SockControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SockService sockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterIncome() throws Exception {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Red");
        sockDto.setCottonPart(80);
        sockDto.setQuantity(100);

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\": \"Red\", \"cottonPart\": 80, \"quantity\": 100}"))
                .andExpect(status().isOk());

        verify(sockService, times(1)).registerIncome(any(SockDto.class));
    }

    @Test
    void testRegisterOutcome() throws Exception {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPart(70);
        sockDto.setQuantity(50);

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\": \"Blue\", \"cottonPart\": 70, \"quantity\": 50}"))
                .andExpect(status().isOk());

        verify(sockService, times(1)).registerOutcome(any(SockDto.class));
    }

    @Test
    void testGetSocks() throws Exception {
        Sock sock1 = new Sock();
        sock1.setId(1L);
        sock1.setColor("Red");
        sock1.setCottonPart(80);
        sock1.setQuantity(100);

        Sock sock2 = new Sock();
        sock2.setId(2L);
        sock2.setColor("Blue");
        sock2.setCottonPart(70);
        sock2.setQuantity(50);

        List<Sock> socks = Arrays.asList(sock1, sock2);
        when(sockService.getSocks(null, null, null)).thenReturn(socks);

        mockMvc.perform(get("/api/socks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("Red"))
                .andExpect(jsonPath("$[0].cottonPart").value(80))
                .andExpect(jsonPath("$[0].quantity").value(100))
                .andExpect(jsonPath("$[1].color").value("Blue"))
                .andExpect(jsonPath("$[1].cottonPart").value(70))
                .andExpect(jsonPath("$[1].quantity").value(50));
    }

    @Test
    void testUploadBatch() throws Exception {
        mockMvc.perform(multipart("/api/socks/batch")
                        .file("file", "test content".getBytes()))
                .andExpect(status().isOk());

        verify(sockService, times(1)).uploadBatch(any());
    }
}