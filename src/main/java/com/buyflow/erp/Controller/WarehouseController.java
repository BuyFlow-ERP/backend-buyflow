@RestController
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseDto.HouseList>> getWarehouseList() {
        List<WarehouseDto.HouseList> list = warehouseService.findAllWarehouses();
        return ResponseEntity.ok(list);
    }
    
}
