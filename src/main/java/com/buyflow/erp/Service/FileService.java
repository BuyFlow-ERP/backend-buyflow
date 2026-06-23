package com.buyflow.erp.Service;

import org.springframework.web.multipart.MultipartFile;

import com.buyflow.erp.Entity.Attachment;

public interface FileService {
	Attachment uploadFile(MultipartFile file, Long userId, String userName) throws Exception;
}
