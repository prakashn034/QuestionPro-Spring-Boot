package com.questionpro.grocery.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyGrocery {

    @Id
    @NonNull
    private String name;

    @NonNull
    private Integer count;
}
