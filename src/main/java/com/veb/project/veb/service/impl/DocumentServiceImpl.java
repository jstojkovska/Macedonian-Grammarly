package com.veb.project.veb.service.impl;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.User;
import com.veb.project.veb.repository.DocumentRepository;
import com.veb.project.veb.repository.UserRepository;
import com.veb.project.veb.service.DocumentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    private final VersionHistoryServiceImpl versionHistoryServiceImpl;

    private final UserRepository userRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, VersionHistoryServiceImpl versionHistoryServiceImpl, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.versionHistoryServiceImpl = versionHistoryServiceImpl;
        this.userRepository = userRepository;
    }

    public Document create(String title, String content, String username) {
        Document doc = new Document();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setCreatedAt(LocalDateTime.now());

        User user = userRepository.findByUsername(username);
        doc.setUser(user);
        Document savedDoc = documentRepository.save(doc);
        versionHistoryServiceImpl.saveVersion(savedDoc);

        return savedDoc;
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
    @Transactional
    public void update(Document doc, String newContent) {
        versionHistoryServiceImpl.saveVersion(doc);
        doc.setContent(newContent);
        doc.setCreatedAt(LocalDateTime.now());
        documentRepository.save(doc);
    }

    @Override
    public List<Document> getAllForUser(String username) {
        return documentRepository.findAllByUser_Username(username);
    }

    @Override
    public List<Document> searchByUsername(String username, String query) {
        return documentRepository.findByUser_UsernameAndTitleContainingIgnoreCase(username, query);
    }

    @Override
    public Document createWithoutUser(String title, String content) {
        Document doc = new Document();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setCreatedAt(LocalDateTime.now());
        Document savedDoc = documentRepository.save(doc);

        versionHistoryServiceImpl.saveVersion(savedDoc);

        return savedDoc;
    }

    @Override
    public List<Document> getAllWithoutUser() {
        return documentRepository.findAllByUserIsNull();
    }
}
