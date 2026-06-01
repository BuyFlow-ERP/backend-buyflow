@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    
    @Override
    public List<WarehouseDto.HouseList> findAllWarehouses() {
        List<Warehouse> warehouses = WarehouseRepository.findAll();
        return warehouses.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
