package com.example.myfantasy.character.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BuyItemFromPlayerRequest extends BuyItemRequest {
    private Long sellerId;
}
