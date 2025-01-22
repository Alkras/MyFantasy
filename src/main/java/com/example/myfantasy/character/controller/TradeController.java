package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.request.BuyItemFromPlayerRequest;
import com.example.myfantasy.character.model.request.BuyItemRequest;
import com.example.myfantasy.character.service.TradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/trade/shop/refresh")
    public List<Item> refreshShopKeeperItems() {
        return tradeService.refreshShopKeeperItems();
    }

    @GetMapping("/trade/shop/list")
    public List<Item> getShopKeeperItems() {
        return tradeService.getShopKeeperItems();
    }

    @PostMapping("/trade/shop")
    public Item buyItemFromShop(@RequestBody BuyItemRequest buyItemRequest) {
        return tradeService.buyItemFromShop(buyItemRequest);
    }

    @PostMapping("/trade/player")
    public Item buyItemFromPlayer(@RequestBody BuyItemFromPlayerRequest buyItemFromPlayerRequest) {
        return tradeService.buyItemFromPlayer(buyItemFromPlayerRequest);
    }
}
