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
import com.buyflow.erp.Dto.StockHistoryResponseDto;

import com.buyflow.erp.Dto.ReceiptDto;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

	private final PurchaseOrderService purchaseOrderService;
	private final ReceiptService receiptService;
	private final StockService stockService;
	private final StockHistoryService stockHistoryService;
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

				} else if ("stock-history".equalsIgnoreCase(target)) {

					rowCount = drawStockHistorySheet(workbook);

				} else if ("receipts".equalsIgnoreCase(target)) {

					rowCount = drawReceiptSheet(workbook);

				}
				String fileName;

				if ("orders".equalsIgnoreCase(target)) {

					fileName = "발주목록.xlsx";

				} else if ("inventories".equalsIgnoreCase(target)) {

					fileName = "재고현황.xlsx";

				} else if ("stock-history".equalsIgnoreCase(target)) {

					fileName = "재고이력.xlsx";

				} else if ("receipts".equalsIgnoreCase(target)) {

					fileName = "입고관리.xlsx";

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

	private long drawStockHistorySheet(Workbook workbook) {

		Sheet sheet = workbook.createSheet("재고 이력");

		List<StockHistoryResponseDto> histories = stockHistoryService.getStockHistory();

		Row headerRow = sheet.createRow(0);

		headerRow.createCell(0).setCellValue("발생일시");
		headerRow.createCell(1).setCellValue("변경유형");
		headerRow.createCell(2).setCellValue("품목코드");
		headerRow.createCell(3).setCellValue("품목명");
		headerRow.createCell(4).setCellValue("창고");
		headerRow.createCell(5).setCellValue("변경수량");
		headerRow.createCell(6).setCellValue("이전재고");
		headerRow.createCell(7).setCellValue("현재재고");
		headerRow.createCell(8).setCellValue("사유");
		headerRow.createCell(9).setCellValue("처리자");

		int rowNum = 1;

		for (StockHistoryResponseDto history : histories) {

			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(
					history.getOccurredAt() != null ? history.getOccurredAt() : "");

			row.createCell(1).setCellValue(
					history.getMovementType() != null ? history.getMovementType() : "");

			row.createCell(2).setCellValue(
					history.getItemCode() != null ? history.getItemCode() : "");

			row.createCell(3).setCellValue(
					history.getItemName() != null ? history.getItemName() : "");

			row.createCell(4).setCellValue(
					history.getWarehouseName() != null ? history.getWarehouseName() : "");

			row.createCell(5).setCellValue(
					history.getQuantity() != null ? history.getQuantity() : 0);

			row.createCell(6).setCellValue(
					history.getBeforeStock() != null ? history.getBeforeStock() : 0);

			row.createCell(7).setCellValue(
					history.getAfterStock() != null ? history.getAfterStock() : 0);

			row.createCell(8).setCellValue(
					history.getReason() != null ? history.getReason() : "");

			row.createCell(9).setCellValue(
					history.getProcessedBy() != null ? history.getProcessedBy() : "");
		}

		return histories.size();
	}

	private long drawReceiptSheet(Workbook workbook) {

		Sheet sheet = workbook.createSheet("입고 관리");

		ReceiptDto.PageResponse<ReceiptDto.ListResponse> response = receiptService.searchReceipts(
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				1,
				99999);

		List<ReceiptDto.ListResponse> receipts = response.getItems();

		Row headerRow = sheet.createRow(0);

		headerRow.createCell(0).setCellValue("발주번호");
		headerRow.createCell(1).setCellValue("공급업체");
		headerRow.createCell(2).setCellValue("발주일");
		headerRow.createCell(3).setCellValue("입고예정일");
		headerRow.createCell(4).setCellValue("창고");
		headerRow.createCell(5).setCellValue("품목수");
		headerRow.createCell(6).setCellValue("발주수량");
		headerRow.createCell(7).setCellValue("입고수량");
		headerRow.createCell(8).setCellValue("미입고수량");
		headerRow.createCell(9).setCellValue("상태");

		int rowNum = 1;

		for (ReceiptDto.ListResponse receipt : receipts) {

			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(receipt.getOrderNumber());
			row.createCell(1).setCellValue(receipt.getSupplierName());
			row.createCell(2).setCellValue(receipt.getOrderedAt());
			row.createCell(3).setCellValue(receipt.getExpectedReceiptAt());
			row.createCell(4).setCellValue(receipt.getWarehouseName());
			row.createCell(5).setCellValue(receipt.getItemCount());
			row.createCell(6).setCellValue(receipt.getOrderQuantity());
			row.createCell(7).setCellValue(receipt.getReceivedQuantity());
			row.createCell(8).setCellValue(receipt.getRemainingQuantity());
			row.createCell(9).setCellValue(receipt.getStatus());
		}

		return receipts.size();
	}
}
