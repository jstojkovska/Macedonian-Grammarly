package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;

import java.util.List;

public interface VersionHistoryService {
    void saveVersion(Document oldDocument);
    List<VersionHistory> getAll(Document document);
    VersionHistory getById(Long id);
}
