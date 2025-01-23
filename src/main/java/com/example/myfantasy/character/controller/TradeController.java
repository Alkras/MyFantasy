package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.request.BuyItemFromPlayerRequest;
import com.example.myfantasy.character.model.request.BuyItemRequest;
import com.example.myfantasy.character.service.TradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trade")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/shop/refresh")
    public List<Item> refreshShopKeeperItems() {
        return tradeService.refreshShopKeeperItems();
    }

    @GetMapping("/shop/list")
    public List<Item> getShopKeeperItems() {
        return tradeService.getShopKeeperItems();
    }

    @PostMapping("/shop")
    public Item buyItemFromShop(@RequestBody BuyItemRequest buyItemRequest) {
        return tradeService.buyItemFromShop(buyItemRequest);
    }

    @PostMapping("/player")
    public Item buyItemFromPlayer(@RequestBody BuyItemFromPlayerRequest buyItemFromPlayerRequest) {
        return tradeService.buyItemFromPlayer(buyItemFromPlayerRequest);
    }
}
