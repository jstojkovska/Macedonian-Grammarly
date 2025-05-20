package com.veb.project.veb.web;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;
import com.veb.project.veb.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/home")
    public String homePage(Model model, @RequestParam(value = "query", required = false) String query) {
        List<Document> allDocs;

        if (query != null && !query.isEmpty()) {
            List<Document> results = documentService.search(query);
            model.addAttribute("searchQuery", query);
            model.addAttribute("searchResults", results);
        } else {
            allDocs = documentService.getAll();

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

            model.addAttribute("documentsToday", todayDocs);
            model.addAttribute("documentsEarlier", earlierDocs);
        }

        return "home";
    }


    @GetMapping("/new")
    public String showNewForm() {
        return "new-document";
    }

    @PostMapping("/new-document")
    public String createNewDocument(@RequestParam String title,
                                    @RequestParam String content) {
        documentService.create(title, content);
        return "redirect:/documents/home";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "/upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String content = new String(file.getBytes());


            String originalFilename = file.getOriginalFilename();
            String title = originalFilename != null ? originalFilename.replaceFirst("[.][^.]+$", "") : "Untitled";

            documentService.create(title, content);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/documents/upload?error";
        }

        return "redirect:/documents/home";
    }



    @GetMapping("/delete/{id}")
    public String deleteDoc(@PathVariable Long id) {
        documentService.deleteById(id);
        return "redirect:/documents/home";
    }

    @PostMapping("/update/{id}")
    public String updateDocument(@PathVariable Long id, @RequestParam String content) {
        Document doc = documentService.getById(id);
        documentService.update(doc, content);
        return "redirect:/documents/home";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadVersion(@PathVariable Long id) {
        Document document = documentService.getById(id);
        String fileName = "version_" + id + ".txt";

        ByteArrayResource resource = new ByteArrayResource(document.getContent().getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Document doc = documentService.getById(id);
        model.addAttribute("document", doc);
        return "edit-document";
    }


}
