package com.buyflow.erp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buyflow.erp.Entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

}
