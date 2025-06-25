package com.veb.project.veb.repository;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionHistoryRepository extends JpaRepository<VersionHistory,Long> {
    List<VersionHistory>findByDocument(Document document);
    List<VersionHistory> findByDocumentIdOrderByChangedAtDesc(Long documentId);
}
