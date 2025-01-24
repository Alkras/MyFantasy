package com.example.myfantasy.character.service;

import com.example.myfantasy.character.exceptions.ItemNotFoundException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.ItemTemplate;
import com.example.myfantasy.character.model.ItemType;
import com.example.myfantasy.character.repository.ItemTemplatesRepository;
import com.example.myfantasy.character.repository.ItemsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemsRepository itemsRepository;

    @Mock
    private ItemTemplatesRepository itemTemplatesRepository;


    @Test
    void getAllItemTemplates() {
        ItemTemplate itemTemplate = createItemTemplate();
        doReturn(List.of(itemTemplate))
                .when(itemTemplatesRepository)
                .findAll();

        List<ItemTemplate> itemTemplates = itemService.getAllItemTemplates();

        assertThat(itemTemplates)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .extracting(ItemTemplate::getName, ItemTemplate::getItemType, ItemTemplate::getPrice, ItemTemplate::getStrength)
                .containsExactly("name", ItemType.WEAPON, BigDecimal.ONE, 10);

        verify(itemTemplatesRepository).findAll();
    }

    @Test
    public void testGetAllItems() {
        Item item = createItem();
        doReturn(List.of(item))
                .when(itemsRepository)
                .findAll();

        List<Item> allItems = itemService.getAllItems();
        assertThat(allItems)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .extracting(Item::getId, Item::getName, Item::getItemType, Item::getPrice, Item::getStrength, x -> x.getCharacter().getId())
                .containsExactly(1L, "item", ItemType.WEAPON, BigDecimal.TEN, 10, 10L);

        verify(itemsRepository).findAll();
    }

    @Test
    public void testSaveItem() {
        Item item = createItem();

        doReturn(item)
                .when(itemsRepository)
                .save(item);

        itemService.save(item);

        verify(itemsRepository).save(item);
    }

    @Test
    public void testSaveAllItems() {
        Item item = createItem();
        List<Item> itemsList = List.of(item);
        doReturn(itemsList)
                .when(itemsRepository)
                .saveAll(itemsList);

        itemService.saveAll(itemsList);

        verify(itemsRepository).saveAll(itemsList);
    }

    @Test
    public void testFindAllByCharacterId() {
        Item item = createItem();
        Long characterId = 1L;
        List<Item> itemsList = List.of(item);
        doReturn(itemsList)
                .when(itemsRepository)
                .findAllByCharacterId(characterId);


        List<Item> itemsByCharacterId = itemService.findAllByCharacterId(characterId);

        assertThat(itemsByCharacterId)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .extracting(Item::getId, Item::getName, Item::getItemType, Item::getPrice, Item::getStrength, x -> x.getCharacter().getId())
                .containsExactly(1L, "item", ItemType.WEAPON, BigDecimal.TEN, 10, 10L);

        verify(itemsRepository).findAllByCharacterId(characterId);
    }

    @Test
    public void testGetItemById() {
        Item item = createItem();
        Long itemId = 1L;

        doReturn(Optional.of(item))
                .when(itemsRepository).findById(itemId);

        Item itemById = itemService.getItemById(itemId);

        assertThat(itemById)
                .isNotNull()
                .extracting(Item::getId, Item::getName, Item::getItemType, Item::getPrice, Item::getStrength, x -> x.getCharacter().getId())
                .containsExactly(1L, "item", ItemType.WEAPON, BigDecimal.TEN, 10, 10L);

        verify(itemsRepository).findById(itemId);
    }

    @Test
    public void testGetItemByIdNotFound() {
        Long itemId = 1L;
        doReturn(Optional.empty()).when(itemsRepository).findById(itemId);

        Assertions.assertThatThrownBy(() -> itemService.getItemById(itemId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("Item with id:1 does not exists");

        verify(itemsRepository).findById(itemId);
    }

    private ItemTemplate createItemTemplate() {
        return new ItemTemplate("name", ItemType.WEAPON, BigDecimal.ONE, 10);
    }

    private Item createItem() {
        Character character = new Character();
        character.setId(10L);
        return Item.builder()
                .id(1L)
                .itemType(ItemType.WEAPON)
                .name("item")
                .price(BigDecimal.TEN)
                .strength(10)
                .character(character)
                .build();
    }
}