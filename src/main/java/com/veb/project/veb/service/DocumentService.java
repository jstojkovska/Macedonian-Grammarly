package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void create(String title, String content) {
        Document document = new Document(title, content);
        documentRepository.save(document);
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
}
