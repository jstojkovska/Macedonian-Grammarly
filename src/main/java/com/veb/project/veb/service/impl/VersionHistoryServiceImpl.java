package com.veb.project.veb.service.impl;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;
import com.veb.project.veb.repository.DocumentRepository;
import com.veb.project.veb.repository.VersionHistoryRepository;
import com.veb.project.veb.service.VersionHistoryService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VersionHistoryServiceImpl implements VersionHistoryService {

    private final VersionHistoryRepository versionHistoryRepository;
    private final DocumentRepository documentRepository;

    public VersionHistoryServiceImpl(VersionHistoryRepository versionHistoryRepository, DocumentRepository documentRepository) {
        this.versionHistoryRepository = versionHistoryRepository;

        this.documentRepository = documentRepository;
    }

    public void saveVersion(Document oldDocument){
        VersionHistory vh=new VersionHistory(oldDocument.getTitle(),oldDocument.getContent(),oldDocument);
        versionHistoryRepository.save(vh);
    }

    public List<VersionHistory> getAll(Document document){
        return versionHistoryRepository.findByDocument(document);
    }

    public VersionHistory getById(Long id){
        return versionHistoryRepository.findById(id).orElseThrow(()->new RuntimeException("Version not found"));
    }

    @Override
    public List<VersionHistory> getVersionsForDocument(Long documentId) {
        return versionHistoryRepository.findByDocumentIdOrderByChangedAtDesc(documentId);
    }

    @Override
    public Map<Long, List<VersionHistory>> getVersionsForDocuments(List<Document> documents) {
        Map<Long, List<VersionHistory>> map = new LinkedHashMap<>();
        for (Document doc : documents) {
            map.put(doc.getId(), getVersionsForDocument(doc.getId()));
        }
        return map;
    }

    @Override
    public Map<Document, List<VersionHistory>> getAllVersionsGroupedByDocument(String username) {
        Map<Document, List<VersionHistory>> allVersions = new LinkedHashMap<>();
        List<Document> documents = documentRepository.findAllByUser_Username(username);// so service pravese problem kruzna zavisnost

        for (Document doc : documents) {
            List<VersionHistory> versions = getAll(doc);
            if (!versions.isEmpty()) {
                allVersions.put(doc, versions);
            }
        }

        return allVersions;
    }

}
