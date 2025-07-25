package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DocumentService {
    Document create(String title, String content,String username);
    List<Document> getAll();
    void deleteById(Long id);
    Document getById(Long id);
    void update(Document doc, String newContent);
    List<Document> getAllForUser(String username);
    List<Document> searchByUsername(String username, String query);
    Map<String, List<Document>> getSplitDocuments(String username);
    Document handleFileUpload(MultipartFile file, String username);
    void updateById(Long id, String newContent);
}
