package com.backspark.socks.service;

import com.backspark.socks.dto.SockDto;
import com.backspark.socks.model.Sock;
import com.backspark.socks.repository.SockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SockServiceTests {

    @InjectMocks
    private SockService sockService;

    @Mock
    private SockRepository sockRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }


    @Test
    void registerIncomeTest() {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Red");
        sockDto.setCottonPart(80);
        sockDto.setQuantity(100);

        Sock existingSock = new Sock();
        existingSock.setColor("Red");
        existingSock.setCottonPart(80);
        existingSock.setQuantity(50);

        when(sockRepository.findByColorAndCottonPart("Red", 80)).thenReturn(List.of(existingSock));

        sockService.registerIncome(sockDto);

        verify(sockRepository, times(1)).save(any(Sock.class));
        assertEquals(150, existingSock.getQuantity());

    }


    @Test
    void registerOutcomeTest() {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPart(70);
        sockDto.setQuantity(50);

        Sock existingSock = new Sock();
        existingSock.setColor("Blue");
        existingSock.setCottonPart(70);
        existingSock.setQuantity(100);

        when(sockRepository.findByColorAndCottonPart("Blue", 70)).thenReturn(List.of(existingSock));

        sockService.registerOutcome(sockDto);

        verify(sockRepository, times(1)).save(any(Sock.class));
        assertEquals(50, existingSock.getQuantity());

    }


    @Test
    void registerOutcomeNotEnoughStockTest() {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Green");
        sockDto.setCottonPart(60);
        sockDto.setQuantity(200);

        Sock existingSock = new Sock();
        existingSock.setColor("Green");
        existingSock.setCottonPart(60);
        existingSock.setQuantity(100);

        when(sockRepository.findByColorAndCottonPart("Green", 60)).thenReturn(List.of(existingSock));

        Exception exception = assertThrows(RuntimeException.class, () -> sockService.registerOutcome(sockDto));

        assertEquals("Not enough socks in stock.", exception.getMessage());

    }


    @Test
    void getSocksTest() {
        Sock sock1 = new Sock();
        sock1.setColor("Red");
        sock1.setCottonPart(80);
        sock1.setQuantity(100);

        Sock sock2 = new Sock();
        sock2.setColor("Blue");
        sock2.setCottonPart(70);
        sock2.setQuantity(50);

        List<Sock> socks = new ArrayList<>();
        socks.add(sock1);
        socks.add(sock2);

        when(sockRepository.findAll()).thenReturn(socks);

        List<Sock> result = sockService.getSocks("Red", null, null);

        assertEquals(1, result.size());
        assertEquals("Red", result.get(0).getColor());

    }
}