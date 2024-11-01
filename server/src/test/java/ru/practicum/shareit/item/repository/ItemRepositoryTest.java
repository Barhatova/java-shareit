package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("name1")
                .email("name1@yandex.ru")
                .build();
        userRepository.save(user);

        itemRepository.save(Item.builder()
                .name("name2")
                .description("desc wow")
                .available(true)
                .itemRequest(null)
                .ownerId(user.getId())
                .build());

        itemRepository.save(Item.builder()
                .name("name3")
                .description("Wowa")
                .available(true)
                .itemRequest(null)
                .ownerId(user.getId())
                .build());
    }

    @Test
    void test_deleteInBatch() {
    }

    @Test
    void test_getAllByOwnerOrderById() {
        Collection<Item> itemList = itemRepository.getAllByOwnerId(user.getId());

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void test_searchItemsByText() {
        Collection<Item> itemList =
                itemRepository.searchAvailableItems("ow");
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }
}