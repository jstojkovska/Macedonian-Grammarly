package com.veb.project.veb.repository;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByUser_Username(String username);
    List<Document> findByUser_UsernameAndTitleContainingIgnoreCase(String username, String query);
}
