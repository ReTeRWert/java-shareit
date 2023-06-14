package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdIsOrderById(Long userId, Pageable pageable);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            " OR LOWER(i.description) LIKE LOWER(concat('%', ?1, '%'))) " +
            "AND i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    List<Item> findAllByRequestIdIs(Long requestId);
}
