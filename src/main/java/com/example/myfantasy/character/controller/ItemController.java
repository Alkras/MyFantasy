package com.example.myfantasy.character.controller;


import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.ItemTemplate;
import com.example.myfantasy.character.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/item-templates")
    public List<ItemTemplate> getAllItemTemplates() {
        return itemService.getAllItemTemplates();
    }

    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

}
