package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.ServiceType;

public class ServiceSearchDTO {
    public ServiceType serviceType;
    public String discipline;
    public String city;
    public Double minPrice;
    public Double maxPrice;
    public boolean onlineOnly;
    public int page = 0;
    public int size = 20;
}