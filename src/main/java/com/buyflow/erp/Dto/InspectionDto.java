public class InspectionDto {

    @Getter
    public static class ListResponse {
        private Long inspectionId;
        private LocalDate inspectionDate;
        private String inspectionType;
        private String inspectionResult;
        private Long receiptItemId;
        private Long userId;
        private Long quantity;
        private Long defectQuantity;
    }
    
}
