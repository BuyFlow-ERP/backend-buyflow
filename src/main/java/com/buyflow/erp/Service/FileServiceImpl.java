package com.buyflow.erp.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Value("${app.upload.dir:/app/uploads}")
    private String uploadDir;

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

        Path dir = Paths.get(uploadDir);

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String savedName = UUID.randomUUID() + extension;
        Path filePath = dir.resolve(savedName);

        file.transferTo(filePath.toFile());

        Users user = null;

        if (userId != null) {
            user = usersRepository.getReferenceById(userId);
        }

        Attachment attachment = Attachment.builder()
                .originalName(originalName)
                .savedName(savedName)
                .filePath(filePath.toString())
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