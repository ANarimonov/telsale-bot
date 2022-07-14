package com.anarimonov.phonesalebot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {
    private Integer id;
    private String model;
    private String brand;
    private double price;
    private String batteryCapacity;
    private boolean documents;
    private double documentPenalty;
    private String color;
    private String country;
    private String damage;
    private int damage1;
    private int damage2;
    private String storage;
    private boolean forSelling;
    private String phoneNumber;
    private String swap;
    private String condition;
    private String place;
}
