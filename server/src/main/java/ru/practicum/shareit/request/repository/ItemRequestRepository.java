package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    Collection<ItemRequest> getByCreatorIdIsNot(Integer userId);

    Collection<ItemRequest> getByCreatorIdOrderByCreatedDesc(Integer userId);
}