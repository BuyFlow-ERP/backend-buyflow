@RestController
public class InspectionController {

    private final InspectionService inspectionService;
    
    @GetMapping
    public ResponseEntity<List<InspectionDto.ListResponse>> getInspectionList() {
        List<InspectionDto.ListResponse> list = inspectionService.findAllInspects();
        return ResponseEntity.ok(list);
    }
    
}
