package com.veb.project.veb.repository;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionHistoryRepository extends JpaRepository<VersionHistory,Long> {
    List<VersionHistory>findByDocument(Document document);
}
