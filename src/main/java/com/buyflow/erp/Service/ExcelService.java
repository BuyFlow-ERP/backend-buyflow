package com.buyflow.erp.Service;

import java.io.IOException;

import com.buyflow.erp.Entity.Users;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelService {
	void exportExcel(String target, Users user, HttpServletResponse response) throws IOException;
}
