package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.request.BuyItemFromPlayerRequest;
import com.example.myfantasy.character.model.request.BuyItemRequest;
import com.example.myfantasy.character.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
class TradeControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TradeService tradeService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void refreshShopKeeperItems() throws Exception {
        doReturn(List.of(new Item(), new Item()))
                .when(tradeService)
                .refreshShopKeeperItems();

        mockMvc.perform(post("/trade/shop/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tradeService).refreshShopKeeperItems();
    }

    @Test
    void getShopKeeperItems() throws Exception {
        doReturn(List.of(new Item(), new Item()))
                .when(tradeService)
                .getShopKeeperItems();

        mockMvc.perform(get("/trade/shop/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tradeService).getShopKeeperItems();
    }

    @Test
    void buyItemFromShop() throws Exception {
        BuyItemRequest buyItemRequest = new BuyItemRequest(0L, 0L);
        Item toBeReturned = new Item();
        toBeReturned.setId(1L);
        doReturn(toBeReturned)
                .when(tradeService)
                .buyItemFromShop(buyItemRequest);

        mockMvc.perform(post("/trade/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(tradeService).buyItemFromShop(buyItemRequest);
    }

    @Test
    void buyItemFromPlayer() throws Exception {
        BuyItemFromPlayerRequest buyItemFromPlayerRequest = new BuyItemFromPlayerRequest();
        buyItemFromPlayerRequest.setSellerId(0L);
        buyItemFromPlayerRequest.setBuyerId(0L);
        buyItemFromPlayerRequest.setItemId(1L);
        Item toBeReturned = new Item();
        toBeReturned.setId(1L);
        doReturn(toBeReturned)
                .when(tradeService)
                .buyItemFromPlayer(buyItemFromPlayerRequest);

        mockMvc.perform(post("/trade/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyItemFromPlayerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(tradeService).buyItemFromPlayer(buyItemFromPlayerRequest);
    }
}