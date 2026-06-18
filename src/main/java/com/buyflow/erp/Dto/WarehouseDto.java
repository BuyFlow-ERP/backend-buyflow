package com.buyflow.erp.Dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WarehouseDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
    public static class SearchCondition {
        private String warehouseName;
        private String type;
        private String useYn;
        private String createdAt;
        private String managerName;

        // 페이징
        private int page = 0;
        private int size = 10;
    }

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
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
	@NoArgsConstructor
	@AllArgsConstructor
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
	@NoArgsConstructor
	@AllArgsConstructor
    public static class Create {
        private String warehouseCode;
        private String warehouseName;
        private String zipcode;
        private String address;
        private String detailAddress;
        private String contact;
        private String useYn;
        private String userId;
        private String managerName;
        private String type;
    }
    
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
    public static class Update {
    	private String warehouseName;
    	private String zipcode;
    	private String address;
    	private String detailAddress;
    	private String contact;
    	private String useYn;
    	private String userId;
    	private String type;
    }
    
}
