package com.buyflow.erp.Entity;

@Entity
@Getter
@Setter
// @Table(name = "WAREHOUSE")
// @NoArgsConstructor
public class Warehouse {
    
    @Id
    @Column(name = "WAREHOUSE_CODE", length = 50)
    private String warehouseCode;

    @Column(name = "WAREHOUSE_NAME", length = 100)
    private String warehouseName;
 
    @Column(name = "LOCATION", length = 200)
    private String location;

    @Column(name = "ADDRESS", length = 300)
    private String address;

    @Column(name = "MANAGER", length = 100)
    private String manager;
 
    @Column(name = "CONTACT", length = 50)
    private String contact;
 
    @Column(name = "USE_YN", length = 1)
    private String useYn = "Y";
 
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
 
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
