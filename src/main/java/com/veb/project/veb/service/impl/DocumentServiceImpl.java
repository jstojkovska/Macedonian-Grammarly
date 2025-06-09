package com.veb.project.veb.service.impl;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.repository.DocumentRepository;
import com.veb.project.veb.service.DocumentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    private final VersionHistoryServiceImpl versionHistoryServiceImpl;

    public DocumentServiceImpl(DocumentRepository documentRepository, VersionHistoryServiceImpl versionHistoryServiceImpl) {
        this.documentRepository = documentRepository;
        this.versionHistoryServiceImpl = versionHistoryServiceImpl;
    }

    public Document create(String title, String content) {
        Document doc = new Document();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setCreatedAt(LocalDateTime.now());

        return documentRepository.save(doc);
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
        versionHistoryServiceImpl.saveVersion(doc);
        doc.setContent(newContent);
        doc.setCreatedAt(LocalDateTime.now());
        documentRepository.save(doc);
    }
}
