package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                .name("userName1")
                .email("test@mail.fg")
                .build();
        userRepository.save(user);

        itemRepository.save(Item.builder()
                .name("item1")
                .description("item 1 Oh")
                .available(true)
                .itemRequest(null)
                .ownerId(user.getId())
                .build());

        itemRepository.save(Item.builder()
                .name("Boook")
                .description("Soha")
                .available(true)
                .itemRequest(null)
                .ownerId(user.getId())
                .build());
    }

    @Test
    void testDeleteInBatch() {
    }

    @Test
    void testFindAllByOwnerOrderById() {
        Collection<Item> itemList = itemRepository.getAllByOwnerId(user.getId());
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testSearchItemsByText() {
        Collection<Item> itemList =
                itemRepository.searchAvailableItems("oh");
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }
}
