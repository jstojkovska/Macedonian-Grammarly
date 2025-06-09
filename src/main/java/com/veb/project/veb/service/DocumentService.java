package com.veb.project.veb.service;

import com.veb.project.veb.model.Document;

import java.util.List;

public interface DocumentService {
    Document create(String title, String content);
    List<Document> getAll();
    void deleteById(Long id);
    Document getById(Long id);
    List<Document> search(String query);
    void update(Document doc, String newContent);
}
