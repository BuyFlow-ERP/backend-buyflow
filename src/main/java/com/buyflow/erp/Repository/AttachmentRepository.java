package com.buyflow.erp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyflow.erp.Entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByRequestIdOrderByAttachmentIdAsc(Long requestId);
}