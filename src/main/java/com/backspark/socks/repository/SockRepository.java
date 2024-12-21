package com.backspark.socks.repository;

import com.backspark.socks.model.Sock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SockRepository extends JpaRepository<Sock, Long> {
    Optional<Sock> findByColorAndCottonPart(String color, Integer cottonPart);

    List<Sock> findByColorAndCottonPartGreaterThan(String color, Integer cottonPart);

    List<Sock> findByColorAndCottonPartLessThan(String color, Integer cottonPart);

    List<Sock> findByColorAndCottonPartBetween(String color, Integer minCottonPart, Integer maxCottonPart);

    List<Sock> findByCottonPartBetween(Integer minCottonPart, Integer maxCottonPart);

    List<Sock> findByColor(String color, Sort sort);

    @Query("SELECT s FROM Sock s WHERE s.color = :color AND s.cottonPart BETWEEN :minCottonPart AND :maxCottonPart")
    List<Sock> findSocksByColorAndCottonRange(
            @Param("color") String color,
            @Param("minCottonPart") Integer minCottonPart,
            @Param("maxCottonPart") Integer maxCottonPart
    );

    Page<Sock> findByColor(String color, Pageable pageable);
}
