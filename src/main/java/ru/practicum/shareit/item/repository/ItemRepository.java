package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Collection<Item> getAllByOwnerId(int userId);

    @Modifying
    @Query("SELECT i FROM Item i "
            + "WHERE upper(i.name) like upper(concat('%', ?1, '%')) "
            + "OR upper(i.description) like upper(concat('%', ?1, '%')) "
            + "AND i.available = true")
    Collection<Item> searchAvailableItems(String text);
}
