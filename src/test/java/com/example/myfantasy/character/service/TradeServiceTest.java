package com.example.myfantasy.character.service;

import com.example.myfantasy.character.exceptions.TradeException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.*;
import com.example.myfantasy.character.model.request.BuyItemFromPlayerRequest;
import com.example.myfantasy.character.model.request.BuyItemRequest;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @InjectMocks
    private TradeService tradeService;
    @Mock
    private ItemService itemService;
    @Mock
    private CharacterService characterService;

    @Test
    void getShopKeeperItems() {
        Character character = new Character();
        ArrayList<Item> inventory = new ArrayList<>();
        character.setInventory(inventory);

        doReturn(character)
                .when(characterService)
                .getCharacterById(0L);

        List<Item> shopKeeperItems = tradeService.getShopKeeperItems();
        assertThat(shopKeeperItems)
                .isNotNull()
                .isEqualTo(inventory);

        verify(characterService).getCharacterById(0L);
        verifyNoMoreInteractions(characterService, itemService);
    }

    @Test
    void refreshShopKeeperItems() {
        Character character = new Character();
        List<Item> shopkeeperItems = List.of(Item.builder().character(character).build(), Item.builder().character(character).build());
        ItemTemplate itemTemplate = ItemTemplate.builder()
                .itemType(ItemType.WEAPON)
                .name("name")
                .price(BigDecimal.ONE)
                .strength(1)
                .build();
        doReturn(shopkeeperItems)
                .when(itemService)
                .findAllByCharacterId(0L);
        doReturn(character)
                .when(characterService)
                .getCharacterById(0L);
        doReturn(List.of(itemTemplate))
                .when(itemService)
                .getAllItemTemplates();
        doNothing()
                .when(itemService)
                .saveAll(anyList());

        List<Item> items = tradeService.refreshShopKeeperItems();

        assertThat(items)
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(5)
                .hasSizeLessThanOrEqualTo(10)
                .allSatisfy(item -> assertThat(item)
                        .extracting(Item::getStrength, Item::getPrice, Item::getItemType, Item::getCharacter, Item::getName)
                        .containsExactly(1, BigDecimal.ONE, ItemType.WEAPON, character, "name"));

        verify(itemService).findAllByCharacterId(0L);
        verify(characterService).getCharacterById(0L);
        verify(itemService).getAllItemTemplates();
        verify(itemService, times(2)).saveAll(anyList());
    }

    @Test
    void buyItemFromShop() {
        BuyItemRequest buyItemRequest = getBuyItemRequest(1L, 2L);
        buyItem(buyItemRequest);
    }

    @Test
    void buyItemFromPlayer() {
        BuyItemFromPlayerRequest buyItemFromPlayerRequest = getBuyItemFromPlayerRequest(0L, 1L, 2L);
        buyItem(buyItemFromPlayerRequest);
    }

    @Test
    @Timeout(value = 2, unit = SECONDS)
    void buyItemAntiDeadlockTest() {
        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Location location = Location.builder().x(5L).y(5L).build();
        Character buyer = getCharacter(2L, location, Arrays.asList(tradeItem), BigDecimal.valueOf(1000), "buyer");
        Character seller = getCharacter(3L, location, Arrays.asList(tradeItem), BigDecimal.valueOf(1000), "seller");

        doReturn(buyer)
                .when(characterService)
                .getCharacterById(2L);
        doReturn(seller)
                .when(characterService)
                .getCharacterById(3L);
        doReturn(tradeItem)
                .when(itemService)
                .getItemById(1L);
        doNothing()
                .when(itemService)
                .save(tradeItem);
        doReturn(buyer)
                .when(characterService)
                .saveHero(buyer);
        doReturn(seller)
                .when(characterService)
                .saveHero(seller);


        List<Item> collect = IntStream.range(0, 100).parallel().mapToObj(_ -> CompletableFuture.supplyAsync(() -> {
                    long sellerId = new Random().nextLong(2, 4);
                    long buyerId = sellerId == 2L ? 3L : 2L;
                    return tradeService.buyItemFromPlayer(getBuyItemFromPlayerRequest(sellerId, 1L, buyerId));
                }))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        assertThat(collect)
                .isNotNull()
                .hasSize(100);

        verify(characterService, times(200)).getCharacterById(any());
        verify(itemService, times(100)).getItemById(any());
        verify(itemService, times(100)).save(tradeItem);
        verify(characterService, times(100)).saveHero(buyer);
        verify(characterService, times(100)).saveHero(seller);
        verifyNoMoreInteractions(characterService, itemService);
    }

    @Test
    void buyItemShouldThrowErrorWithNoMoney() {
        BuyItemRequest buyItemRequest = getBuyItemRequest(1L, 2L);

        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Location location = Location.builder().x(5L).y(5L).build();
        Character buyer = getCharacter(2L, location, new ArrayList<>(), BigDecimal.ZERO, "buyer");
        Character seller = getCharacter(0L, location, Arrays.asList(tradeItem), BigDecimal.ZERO, "seller");

        commonTradeErrorTest(buyItemRequest, buyer, seller, tradeItem, "buyer does not have enough money");
    }

    @Test
    void buyItemShouldThrowErrorWithNoItem() {
        BuyItemRequest buyItemRequest = getBuyItemRequest(1L, 2L);

        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Location location = Location.builder().x(5L).y(5L).build();
        Character buyer = getCharacter(2L, location, new ArrayList<>(), BigDecimal.ZERO, "buyer");
        Character seller = getCharacter(0L, location, new ArrayList<>(), BigDecimal.ZERO, "seller");

        commonTradeErrorTest(buyItemRequest, buyer, seller, tradeItem, "seller does not have requested item");
    }

    @Test
    void buyItemShouldThrowErrorWithPlayersAtDifferentLocations() {
        BuyItemRequest buyItemRequest = getBuyItemRequest(1L, 2L);

        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Location location = Location.builder().x(5L).y(5L).build();
        Location location2 = Location.builder().x(3L).y(3L).build();
        Character buyer = getCharacter(2L, location, new ArrayList<>(), BigDecimal.ZERO, "buyer");
        Character seller = getCharacter(0L, location2, new ArrayList<>(), BigDecimal.ZERO, "seller");

        commonTradeErrorTest(buyItemRequest, buyer, seller, tradeItem, "Players needs to be in the same location to trade");
    }

    @Test
    void buyItemShouldThrowErrorWithNoShopZone() {
        BuyItemRequest buyItemRequest = getBuyItemRequest(1L, 2L);
        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Location location = Location.builder().x(5L).y(5L).locationType(LocationType.EMPTY).build();
        Character buyer = getCharacter(2L, location, new ArrayList<>(), BigDecimal.ZERO, "buyer");
        Character seller = new Character();
        seller.setId(0L);
        seller.setType(Type.SHOPKEEPER);

        commonTradeErrorTest(buyItemRequest, buyer, seller, tradeItem, "Cannot buy from ShopKeeper outside of shop");
    }

    @Test
    void buyItemShouldThrowErrorWithSelfTrade() {
        BuyItemRequest buyItemRequest = getBuyItemRequest(1L, 0L);

        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Character buyer = new Character();
        buyer.setId(0L);
        Character seller = new Character();
        seller.setId(0L);

        doReturn(buyer)
                .doReturn(seller)
                .when(characterService)
                .getCharacterById(0L);
        doReturn(tradeItem)
                .when(itemService)
                .getItemById(1L);

        assertThatThrownBy(() -> tradeService.buyItemFromShop(buyItemRequest))
                .isInstanceOf(TradeException.class)
                .hasMessage("Cannot trade with yourself");

        verify(characterService, times(2)).getCharacterById(0L);
        verify(itemService).getItemById(1L);
        verifyNoMoreInteractions(characterService, itemService);
    }

    private BuyItemRequest getBuyItemRequest(long itemId, long buyerId) {
        BuyItemRequest buyItemRequest = new BuyItemRequest();
        buyItemRequest.setBuyerId(buyerId);
        buyItemRequest.setItemId(itemId);
        return buyItemRequest;
    }

    private BuyItemFromPlayerRequest getBuyItemFromPlayerRequest(long sellerId, long itemId, long buyerId) {
        BuyItemFromPlayerRequest buyItemFromPlayerRequest = new BuyItemFromPlayerRequest();
        buyItemFromPlayerRequest.setBuyerId(buyerId);
        buyItemFromPlayerRequest.setItemId(itemId);
        buyItemFromPlayerRequest.setSellerId(sellerId);
        return buyItemFromPlayerRequest;
    }

    <E> void buyItem(E request) {
        Item tradeItem = Item.builder().id(1L).price(BigDecimal.TEN).build();
        Location location = Location.builder().x(5L).y(5L).build();
        Character buyer = getCharacter(2L, location, new ArrayList<>(), BigDecimal.TEN, "buyer");
        Character seller = getCharacter(0L, location, Arrays.asList(tradeItem), BigDecimal.ZERO, "seller");

        doReturn(buyer)
                .when(characterService)
                .getCharacterById(2L);
        doReturn(seller)
                .when(characterService)
                .getCharacterById(0L);
        doReturn(tradeItem)
                .when(itemService)
                .getItemById(1L);
        doNothing()
                .when(itemService)
                .save(tradeItem);
        doReturn(buyer)
                .when(characterService)
                .saveHero(buyer);
        doReturn(seller)
                .when(characterService)
                .saveHero(seller);

        Item item = switch (request) {
            case BuyItemFromPlayerRequest buyItemFromPlayerRequest ->
                    tradeService.buyItemFromPlayer(buyItemFromPlayerRequest);
            case BuyItemRequest buyItemRequest -> tradeService.buyItemFromShop(buyItemRequest);
            default -> throw new IllegalStateException(STR."Unexpected value: \{request}");
        };

        assertThat(item)
                .isNotNull()
                .extracting(Item::getCharacter, Item::getId)
                .containsExactly(buyer, 1L);

        assertThat(buyer.getMoney()).isEqualTo(BigDecimal.ZERO);
        assertThat(seller.getMoney()).isEqualTo(BigDecimal.TEN);

        verify(characterService).getCharacterById(2L);
        verify(characterService).getCharacterById(0L);
        verify(itemService).getItemById(1L);
        verify(itemService).save(tradeItem);
        verify(characterService).saveHero(buyer);
        verify(characterService).saveHero(seller);
        verifyNoMoreInteractions(characterService, itemService);
    }

    private void commonTradeErrorTest(BuyItemRequest buyItemRequest, Character buyer, Character seller, Item tradeItem, String errorMessage) {
        doReturn(buyer)
                .when(characterService)
                .getCharacterById(2L);
        doReturn(seller)
                .when(characterService)
                .getCharacterById(0L);
        doReturn(tradeItem)
                .when(itemService)
                .getItemById(1L);

        assertThatThrownBy(() -> tradeService.buyItemFromShop(buyItemRequest))
                .isInstanceOf(TradeException.class)
                .hasMessage(errorMessage);

        verify(characterService).getCharacterById(2L);
        verify(characterService).getCharacterById(0L);
        verify(itemService).getItemById(1L);
        verifyNoMoreInteractions(characterService, itemService);
    }


    private Character getCharacter(long id, Location location, List<Item> inventory, BigDecimal zero, String name) {
        Character character = new Character();
        character.setId(id);
        character.setCurrentLocation(location);
        character.setInventory(inventory);
        character.setMoney(zero);
        character.setName(name);
        return character;
    }
}