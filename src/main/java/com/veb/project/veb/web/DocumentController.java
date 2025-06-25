package com.veb.project.veb.web;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.service.DocumentService;
import com.veb.project.veb.service.TextCheckService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final TextCheckService textCheckService;

    public DocumentController(DocumentService documentService, TextCheckService textCheckService) {
        this.documentService = documentService;
        this.textCheckService = textCheckService;
    }

    @GetMapping("/home")
    public String homePage(Model model, @RequestParam(value = "query", required = false) String query, Principal principal) {

        if (principal != null) {
            String username = principal.getName();

            if (query != null && !query.isEmpty()) {
                List<Document> results = documentService.searchByUsername(username, query);
                model.addAttribute("searchQuery", query);
                model.addAttribute("searchResults", results);
                return "home";
            }

            Map<String, List<Document>> splitDocs = documentService.getSplitDocuments(username);
            model.addAttribute("documentsToday", splitDocs.get("today"));
            model.addAttribute("documentsEarlier", splitDocs.get("earlier"));
        }

        return "home";
    }

    @GetMapping("/new")
    public String showNewForm() {
        return "new-document";
    }

    @PostMapping("/new-document")
    public String createNewDocument(@RequestParam String title,
                                    @RequestParam String content,Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            documentService.create(title,content,username);
        }
        return "redirect:/documents/home";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam MultipartFile file, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            try {
                Document savedDoc = documentService.handleFileUpload(file, username);
                return "redirect:/documents/edit/" + savedDoc.getId();
            } catch (RuntimeException e) {
                e.printStackTrace();
                return "redirect:/documents/upload?error=processing";
            }
        } else {
            return "redirect:/documents/home?error=unauthorized";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDoc(@PathVariable Long id,
                            @RequestParam(value = "redirectBackTo", required = false) String redirectBackTo) {
        documentService.deleteById(id);

        if (redirectBackTo != null && !redirectBackTo.isBlank()) {
            return "redirect:" + redirectBackTo;
        }
        return "redirect:/documents/home";
    }


    @PostMapping("/update/{id}")
    public String updateDocument(@PathVariable Long id,
                                 @RequestParam String content,
                                 @RequestParam(required = false) String redirectBackTo) {
        documentService.updateById(id, content);

        if (redirectBackTo != null && !redirectBackTo.isBlank()) {
            return "redirect:" + redirectBackTo;
        }
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
    public String showEditForm(@PathVariable Long id,
                               @RequestParam(value = "redirectBackTo", required = false) String redirectBackTo,
                               Model model) {
        Document doc = documentService.getById(id);
        model.addAttribute("document", doc);
        model.addAttribute("redirectBackTo", redirectBackTo);
        return "edit-document";
    }


    @PostMapping("/check-text")
    @ResponseBody
    public List<Map<String, String>> checkText(@RequestBody Map<String, String> payload) {
        String originalText = payload.get("original");
        return textCheckService.checkText(originalText);
    }

}
