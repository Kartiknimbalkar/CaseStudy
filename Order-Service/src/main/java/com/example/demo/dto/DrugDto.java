package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrugDto {
	
	private String batch_id;
    private String name;
    private String manufacturer;
    private int quantity;
    private double price;
	
}
