package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;

import java.util.List;
import java.util.Map;

public interface VersionHistoryService {
    void saveVersion(Document oldDocument);
    List<VersionHistory> getAll(Document document);
    VersionHistory getById(Long id);
    Map<Document, List<VersionHistory>> getAllVersionsGroupedByDocument(String username);
    List<VersionHistory> getVersionsForDocument(Long documentId);
    Map<Long, List<VersionHistory>> getVersionsForDocuments(List<Document> documents);

}
