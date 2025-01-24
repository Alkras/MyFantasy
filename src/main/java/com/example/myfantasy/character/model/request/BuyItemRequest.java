package com.example.myfantasy.character.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BuyItemRequest {
    private long buyerId;
    private long itemId;
}
