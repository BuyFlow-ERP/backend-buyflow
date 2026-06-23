package com.buyflow.erp.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buyflow.erp.Entity.Attachment;
import com.buyflow.erp.Repository.AttachmentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 데이터 변경이 없는 조회 메서드 위주이므로 readOnly 적용
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    public Attachment getAttachmentInfo(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException("파일 정보를 찾을 수 없습니다. ID: " + attachmentId));
    }
}

