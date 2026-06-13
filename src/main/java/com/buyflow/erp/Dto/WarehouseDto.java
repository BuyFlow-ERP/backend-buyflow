package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class WarehouseDto {

    @Getter
    @Setter
    public static class HouseList {
        private String warehouseCode;
        private String warehouseName;
        private String zipcode;
        private String address;
        private String detailAddress;
        private String contact;
        private String useYn;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String managerName; 
        private String type;
    }
    
    @Getter
    @Setter
    public static class Detail {
        private String warehouseCode;
        private String warehouseName;
        private String zipcode;
        private String address;
        private String detailAddress;
        private String contact;
        private String useYn;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String managerName;
        private String type;
    }
    
    @Getter
    @Setter
    public static class Create {
        private String warehouseCode;
        private String warehouseName;
        private String zipcode;
        private String address;
        private String detailAddress;
        private String contact;
        private String useYn;
        private String managerName;
        private String type;
    }
    
    @Getter
    @Setter
    public static class Update {
    	private String warehouseName;
    	private String zipcode;
    	private String address;
    	private String detailAddress;
    	private String contact;
    	private String useYn;
    	private Long userId;
    	private String type;
    }
    
}
