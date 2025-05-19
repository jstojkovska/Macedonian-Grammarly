package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.repository.DocumentRepository;
import com.veb.project.veb.repository.VersionHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    private final VersionHistoryService versionHistoryService;

    public DocumentService(DocumentRepository documentRepository, VersionHistoryService versionHistoryService) {
        this.documentRepository = documentRepository;
        this.versionHistoryService = versionHistoryService;
    }

    public void create(String title, String content) {
        Document document = new Document(title, content);
        documentRepository.save(document);
        versionHistoryService.saveVersion(document);
    }

    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    public Document getById(Long id) {
        return documentRepository.findById(id).orElseThrow();
    }

    public List<Document> search(String query) {
        return documentRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
    }
    public void update(Document doc, String newContent) {
        versionHistoryService.saveVersion(doc);
        doc.setContent(newContent);
        doc.setCreatedAt(LocalDateTime.now());
        documentRepository.save(doc);
    }
}
