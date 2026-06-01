public class WarehouseDto {

    @Getter
    public static class HouseList {
        private String warehouseCode;
        private String warehouseName;
        private String location;
        private String address;
        private String manager;
        private String contact;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
}
