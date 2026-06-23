package com.buyflow.erp.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.buyflow.erp.Entity.Attachment;
import com.buyflow.erp.Entity.Users;
import com.buyflow.erp.Repository.AttachmentRepository;
import com.buyflow.erp.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository usersRepository;

    // 기존 프로젝트 저장 경로 유지
    private final String UPLOAD_DIR = "C:/erp/uploads/";

    @Override
    public Attachment uploadFile(
            MultipartFile file,
            Long userId,
            String userName
    ) throws IOException {
        return uploadFile(file, userId, userName, null);
    }

    @Override
    public Attachment uploadFile(
            MultipartFile file,
            Long userId,
            String userName,
            Long requestId
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        File dir = new File(UPLOAD_DIR);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalName = file.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String savedName = UUID.randomUUID() + extension;
        String filePath = UPLOAD_DIR + savedName;

        file.transferTo(new File(filePath));

        Users user = null;

        if (userId != null) {
            user = usersRepository.getReferenceById(userId);
        }

        Attachment attachment = Attachment.builder()
                .originalName(originalName)
                .savedName(savedName)
                .filePath(filePath)
                .fileSize(file.getSize())
                .extension(extension)
                .uploadedBy(userName)
                .uploadedAt(LocalDateTime.now())
                .requestId(requestId)
                .user(user)
                .build();

        return attachmentRepository.save(attachment);
    }
}