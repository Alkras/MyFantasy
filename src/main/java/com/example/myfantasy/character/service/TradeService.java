package com.example.myfantasy.character.service;

import com.example.myfantasy.character.Constants;
import com.example.myfantasy.character.exceptions.TradeException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.ItemTemplate;
import com.example.myfantasy.character.model.Type;
import com.example.myfantasy.character.model.request.BuyItemFromPlayerRequest;
import com.example.myfantasy.character.model.request.BuyItemRequest;
import com.example.myfantasy.world.model.LocationType;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TradeService {

    private final ItemService itemService;
    private final CharacterService characterService;

    private final ConcurrentHashMap<Long, Lock> transactionLockMap;

    public TradeService(ItemService itemService, CharacterService characterService) {
        this.itemService = itemService;
        this.characterService = characterService;
        transactionLockMap = new ConcurrentHashMap<>();
    }

    public List<Item> getShopKeeperItems() {
        Character shopkeeper = characterService.getCharacterById(Constants.SHOPKEEPER_ID);
        return shopkeeper.getInventory();
    }

    @Transactional
    public List<Item> refreshShopKeeperItems() {
        clearShopKeeperInventory();
        Character shopkeeper = characterService.getCharacterById(Constants.SHOPKEEPER_ID);
        List<Item> shopKeeperItems = generateNewItemList(itemService.getAllItemTemplates(), shopkeeper);
        itemService.saveAll(shopKeeperItems);
        return shopKeeperItems;
    }

    @Transactional
    public Item buyItemFromShop(BuyItemRequest buyItemRequest) {
        return buyItem(characterService.getCharacterById(buyItemRequest.getBuyerId()),
                characterService.getCharacterById(Constants.SHOPKEEPER_ID),
                itemService.getItemById(buyItemRequest.getItemId()));
    }

    @Transactional
    public Item buyItemFromPlayer(BuyItemFromPlayerRequest buyItemFromPlayerRequest) {
        return buyItem(characterService.getCharacterById(buyItemFromPlayerRequest.getBuyerId()),
                characterService.getCharacterById(buyItemFromPlayerRequest.getSellerId()),
                itemService.getItemById(buyItemFromPlayerRequest.getItemId()));
    }

    private void clearShopKeeperInventory() {
        List<Item> shopkeeperItemList = itemService.findAllByCharacterId(Constants.SHOPKEEPER_ID);
        shopkeeperItemList.forEach(item -> item.setCharacter(null));
        itemService.saveAll(shopkeeperItemList);
    }

    private List<Item> generateNewItemList(List<ItemTemplate> allItemTemplates, Character shopkeeper) {
        return IntStream.range(0, new Random().nextInt(5, 11))
                .mapToObj(_ -> allItemTemplates.get(new Random().nextInt(allItemTemplates.size())))
                .map(itemTemplate -> buildItem(shopkeeper, itemTemplate))
                .collect(Collectors.toList());
    }

    private Item buildItem(Character shopkeeper, ItemTemplate itemTemplate) {
        return Item.builder()
                .itemType(itemTemplate.getItemType())
                .name(itemTemplate.getName())
                .price(itemTemplate.getPrice())
                .strength(itemTemplate.getStrength())
                .character(shopkeeper)
                .build();
    }

    @SneakyThrows(InterruptedException.class)
    private Item buyItem(Character buyer, Character seller, Item item) {
        if (buyer.getId().equals(seller.getId())) {
            throw new TradeException("Cannot trade with yourself");
        }

        Long firstId = buyer.getId() < seller.getId() ? buyer.getId() : seller.getId();
        Long secondId = buyer.getId() < seller.getId() ? seller.getId() : buyer.getId();
        Lock firstLock = transactionLockMap.computeIfAbsent(firstId, _ -> new ReentrantLock());
        Lock secondLock = transactionLockMap.computeIfAbsent(secondId, _ -> new ReentrantLock());

        if (firstLock.tryLock(3, TimeUnit.SECONDS)) {
            try {
                if (secondLock.tryLock(3, TimeUnit.SECONDS)) {
                    try {
                        tradeValidation(buyer, seller, item);
                        return performTrade(buyer, seller, item);
                    } finally {
                        secondLock.unlock();
                        transactionLockMap.remove(secondId);
                    }
                } else {
                    transactionLockMap.remove(secondId);
                    throw new TradeException("Couldn't finish trade");
                }
            } finally {
                firstLock.unlock();
                transactionLockMap.remove(firstId);
            }
        } else {
            transactionLockMap.remove(firstId);
            throw new TradeException("Couldn't finish trade");
        }
    }

    private void tradeValidation(Character buyer, Character seller, Item item) {
        if (Type.SHOPKEEPER.equals(seller.getType())) {
            if (!LocationType.SHOP.equals(buyer.getCurrentLocation().getLocationType())) {
                throw new TradeException("Cannot buy from ShopKeeper outside of shop");
            }
        } else {
            if (!buyer.getCurrentLocation().equals(seller.getCurrentLocation())) {
                throw new TradeException("Players needs to be in the same location to trade");
            }
        }

        seller.getInventory()
                .stream()
                .filter(sellerItem -> sellerItem.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new TradeException(STR."\{seller.getName()} does not have requested item"));

        if (buyer.getMoney().compareTo(item.getPrice()) < 0) {
            throw new TradeException(STR."\{buyer.getName()} does not have enough money");
        }
    }

    private Item performTrade(Character buyer, Character seller, Item item) {
        item.setCharacter(buyer);
        seller.setMoney(seller.getMoney().add(item.getPrice()));
        buyer.setMoney(buyer.getMoney().subtract(item.getPrice()));

        itemService.save(item);
        characterService.saveHero(seller);
        characterService.saveHero(buyer);
        return item;
    }
}
