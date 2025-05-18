package com.example.demo;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EntityDto {
    private int userId;
    private int id;
    private String title;
    private String body;
}
