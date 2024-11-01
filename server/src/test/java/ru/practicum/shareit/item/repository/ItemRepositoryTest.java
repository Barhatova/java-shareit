package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
                .description("oh")
                .available(true)
                .itemRequest(null)
                .ownerId(user.getId())
                .build());

        itemRepository.save(Item.builder()
                .name("Book")
                .description("oh")
                .available(true)
                .itemRequest(null)
                .ownerId(user.getId())
                .build());
    }

    @Test
    void testDeleteInBatch() {
    }

    @Test
    void testGetAllByOwnerOrderById() {
        Collection<Item> itemList = itemRepository.getAllByOwnerId(user.getId());
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

   /* @Test
    void testSearchItemsByText() {
        Collection<Item> itemList = itemRepository.searchAvailableItems("oh");
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }*/
}