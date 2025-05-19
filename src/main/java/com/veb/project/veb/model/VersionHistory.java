package com.veb.project.veb.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VersionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oldTitle;
    private String oldContent;
    private LocalDateTime changedAt;
    @ManyToOne
    private Document document;

    public VersionHistory(String oldTitle, String oldContent, Document document) {
        this.oldTitle = oldTitle;
        this.oldContent = oldContent;
        this.changedAt = LocalDateTime.now();
        this.document = document;
    }

    public VersionHistory() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOldTitle() {
        return oldTitle;
    }

    public void setOldTitle(String oldTitle) {
        this.oldTitle = oldTitle;
    }

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
