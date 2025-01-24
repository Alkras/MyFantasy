package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.model.Item;
import com.example.myfantasy.character.model.ItemTemplate;
import com.example.myfantasy.character.service.ItemService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ItemService itemService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void getAllItemTemplates() throws Exception {
        doReturn(List.of(new ItemTemplate(), new ItemTemplate()))
                .when(itemService)
                .getAllItemTemplates();

        mockMvc.perform(get("/item-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(itemService).getAllItemTemplates();
    }

    @Test
    void getAllItems() throws Exception {
        doReturn(List.of(new Item(), new Item()))
                .when(itemService)
                .getAllItems();

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(itemService).getAllItems();
    }
}