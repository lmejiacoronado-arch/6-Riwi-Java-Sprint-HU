package com.eventify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private int id;
    private String name;
    private String address;
    private Integer capacity;
}
