package com.buyflow.erp.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.buyflow.erp.Entity.ExcelExportHistory;
import com.buyflow.erp.Entity.PurchaseOrder;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Repository.ExcelExportHistoryRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.buyflow.erp.Dto.StockDto;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

	private final PurchaseOrderService purchaseOrderService;
	private final StockService stockService;
	private final ExcelExportHistoryRepository historyRepository;

	@Override
	public void exportExcel(String target, Users user, HttpServletResponse response) throws IOException {
		long rowCount = 0;
		String status = "SUCCESS";

		try {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			System.setProperty("javax.xml.parsers.SAXParserFactory",
					"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

			try (Workbook workbook = new XSSFWorkbook()) {
				if ("orders".equalsIgnoreCase(target)) {

					rowCount = drawOrderSheet(workbook);

				} else if ("inventories".equalsIgnoreCase(target)) {

					rowCount = drawInventorySheet(workbook);

				}
				String fileName;

				if ("orders".equalsIgnoreCase(target)) {

					fileName = "발주목록.xlsx";

				} else if ("inventories".equalsIgnoreCase(target)) {

					fileName = "재고현황.xlsx";

				} else {

					fileName = "ExportedData.xlsx";

				}
				String encodedFileName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
						.replaceAll("\\+", "%20");

				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

				workbook.write(response.getOutputStream());
			}
		} catch (Exception e) {
			status = "FAILED";
			throw e;
		} finally {
			ExcelExportHistory history = new ExcelExportHistory();
			history.setExportType(target.toUpperCase());
			history.setStatus(status);
			history.setCreatedAt(LocalDateTime.now());
			history.setDownloadRowCount(rowCount);
			history.setUser(user);

			historyRepository.save(history);
		}
	}

	private long drawOrderSheet(Workbook workbook) {
		Sheet sheet = workbook.createSheet("발주 내역");
		List<PurchaseOrder> orders = purchaseOrderService.getAllOrdersForExcel();

		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("발주 번호");
		headerRow.createCell(1).setCellValue("공급업체");
		headerRow.createCell(2).setCellValue("총 금액");
		headerRow.createCell(3).setCellValue("상태");

		int rowNum = 1;
		for (PurchaseOrder order : orders) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(order.getOrderNo());
			row.createCell(1).setCellValue(order.getSupplier() != null ? order.getSupplier().getSupplierName() : "-");
			row.createCell(2).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
			row.createCell(3).setCellValue(order.getOrderStatus());
		}
		return orders.size();
	}

	private long drawInventorySheet(Workbook workbook) {

		Sheet sheet = workbook.createSheet("재고 현황");

		List<StockDto> stocks = stockService.findAllStocks();

		Row headerRow = sheet.createRow(0);

		headerRow.createCell(0).setCellValue("품목코드");
		headerRow.createCell(1).setCellValue("품목명");
		headerRow.createCell(2).setCellValue("카테고리");
		headerRow.createCell(3).setCellValue("창고");
		headerRow.createCell(4).setCellValue("현재재고");
		headerRow.createCell(5).setCellValue("안전재고");
		headerRow.createCell(6).setCellValue("단위");

		int rowNum = 1;

		for (StockDto stock : stocks) {

			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(stock.getItemCode());
			row.createCell(1).setCellValue(stock.getItemName());
			row.createCell(2).setCellValue(stock.getCategory());
			row.createCell(3).setCellValue(stock.getWarehouseName());
			row.createCell(4).setCellValue(stock.getCurrentStock());
			row.createCell(5).setCellValue(stock.getSafetyStock());
			row.createCell(6).setCellValue(stock.getUnit());
		}

		return stocks.size();
	}
}
