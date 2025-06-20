package com.veb.project.veb.service.impl;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.User;
import com.veb.project.veb.model.VersionHistory;
import com.veb.project.veb.repository.DocumentRepository;
import com.veb.project.veb.repository.UserRepository;
import com.veb.project.veb.service.DocumentService;
import com.veb.project.veb.service.VersionHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    private final VersionHistoryService versionHistoryService;

    private final UserRepository userRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, VersionHistoryService versionHistoryService, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.versionHistoryService = versionHistoryService;
        this.userRepository = userRepository;
    }

    public Document create(String title, String content, String username) {
        Document doc = new Document();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setCreatedAt(LocalDateTime.now());

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        doc.setUser(user);

        // dodadov lista od docs vo user i sea gi dodavam za da mozat posle da se brisat avtomatski ako se izbrise korisnik i negovite dokumenti da se izbrisat
        user.getDocuments().add(doc);

        Document savedDoc = documentRepository.save(doc);
        versionHistoryService.saveVersion(savedDoc);

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
        versionHistoryService.saveVersion(doc);
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
    public Map<String, List<Document>> getSplitDocuments(String username) {
        List<Document> allDocs = documentRepository.findAllByUser_Username(username);

        LocalDate today = LocalDate.now();
        List<Document> todayDocs = new ArrayList<>();
        List<Document> earlierDocs = new ArrayList<>();

        for (Document doc : allDocs) {
            if (doc.getCreatedAt().toLocalDate().isEqual(today)) {
                todayDocs.add(doc);
            } else {
                earlierDocs.add(doc);
            }
        }

        Map<String, List<Document>> result = new HashMap<>();
        result.put("today", todayDocs);
        result.put("earlier", earlierDocs);
        return result;
    }

    @Override
    public Document handleFileUpload(MultipartFile file, String username) {
        try {
            String content = new String(file.getBytes());
            String originalFilename = file.getOriginalFilename();
            String title = (originalFilename != null) ? originalFilename.replaceFirst("[.][^.]+$", "") : "Untitled";
            return create(title, content, username);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    @Override
    public void updateById(Long id, String newContent) {
        Document doc = getById(id);
        update(doc, newContent);
    }
}
