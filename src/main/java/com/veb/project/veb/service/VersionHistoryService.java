package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;
import com.veb.project.veb.repository.VersionHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VersionHistoryService {
    private final VersionHistoryRepository versionHistoryRepository;

    public VersionHistoryService(VersionHistoryRepository versionHistoryRepository) {
        this.versionHistoryRepository = versionHistoryRepository;
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
}
