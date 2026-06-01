@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InspectionServiceImpl implements InspectionService {
    private final InspectionRepository inspectionRepository;
    
    @Override
    public List<InspectionDto.ListResponse> findAllInspections() {
        List<Inspection> inspection = InspectionRepository.findAll();

        return inspection.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
