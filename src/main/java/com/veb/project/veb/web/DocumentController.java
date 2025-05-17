package com.veb.project.veb.web;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.service.DocumentService;
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
    public String homePage(Model model) {
        List<Document> allDocs = documentService.getAll();

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
    public String handleFileUpload(@RequestParam("title") String title,
                                   @RequestParam("file") MultipartFile file) {
        try {
            String content = new String(file.getBytes());
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
}
