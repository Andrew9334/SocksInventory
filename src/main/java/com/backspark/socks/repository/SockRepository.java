package com.backspark.socks.repository;

import com.backspark.socks.model.Sock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SockRepository extends JpaRepository<Sock, Long> {
    List<Sock> findByColorAndCottonPart(String color, Integer cottonPart);
}
