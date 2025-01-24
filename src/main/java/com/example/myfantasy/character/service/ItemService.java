package com.example.myfantasy.character.service;

import com.example.myfantasy.character.exceptions.ItemNotFoundException;
import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.ItemTemplate;
import com.example.myfantasy.character.repository.ItemTemplatesRepository;
import com.example.myfantasy.character.repository.ItemsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemsRepository itemsRepository;

    private final ItemTemplatesRepository itemTemplatesRepository;

    public List<ItemTemplate> getAllItemTemplates() {
        return itemTemplatesRepository.findAll();
    }

    public void saveAll(List<Item> items) {
        itemsRepository.saveAll(items);
    }

    public void save(Item item) {
        itemsRepository.save(item);
    }

    public List<Item> findAllByCharacterId(Long shopkeeperId) {
        return itemsRepository.findAllByCharacterId(shopkeeperId);
    }

    public List<Item> getAllItems() {
        return itemsRepository.findAll();
    }

    public Item getItemById(Long itemId) {
        return itemsRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(STR."Item with id:\{itemId} does not exists"));
    }
}
