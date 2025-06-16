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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentServiceImpl;
    private final TextCheckService textCheckService;

    public DocumentController(DocumentService documentServiceImpl, TextCheckService textCheckService) {
        this.documentServiceImpl = documentServiceImpl;
        this.textCheckService = textCheckService;
    }

    @GetMapping("/home")
    public String homePage(Model model, @RequestParam(value = "query", required = false) String query, Principal principal) {

        List<Document> allDocs;

        if(principal!=null){
            String username= principal.getName();
            if (query != null && !query.isEmpty()) {
                List<Document> results = documentServiceImpl.searchByUsername(username,query);
                model.addAttribute("searchQuery", query);
                model.addAttribute("searchResults", results);
                return "home";
            }
            allDocs = documentServiceImpl.getAllForUser(username);

        } else {
            allDocs = documentServiceImpl.getAllWithoutUser();
        }
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

    //ova mislam ne ni mi treba
    @GetMapping("/new")
    public String showNewForm() {
        return "new-document";
    }

    @PostMapping("/new-document")
    public String createNewDocument(@RequestParam String title,
                                    @RequestParam String content,Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            documentServiceImpl.create(title,content,username);
        }else {
            documentServiceImpl.createWithoutUser(title, content);
        }
        return "redirect:/documents/home";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "/upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String content = new String(file.getBytes());
            String originalFilename = file.getOriginalFilename();
            String title = originalFilename != null ? originalFilename.replaceFirst("[.][^.]+$", "") : "Untitled";

            if (principal != null) {
                String username = principal.getName();
                Document savedDoc = documentServiceImpl.create(title, content, username);
                return "redirect:/documents/edit/" + savedDoc.getId();
            } else {
                return "redirect:/documents/home?error=unauthorized";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/documents/upload?error";
        }
    }



    @GetMapping("/delete/{id}")
    public String deleteDoc(@PathVariable Long id) {
        documentServiceImpl.deleteById(id);
        return "redirect:/documents/home";
    }

    @PostMapping("/update/{id}")
    public String updateDocument(@PathVariable Long id, @RequestParam String content) {
        Document doc = documentServiceImpl.getById(id);
        documentServiceImpl.update(doc, content);
        return "redirect:/documents/home";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadVersion(@PathVariable Long id) {
        Document document = documentServiceImpl.getById(id);
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
        Document doc = documentServiceImpl.getById(id);
        model.addAttribute("document", doc);
        return "edit-document";
    }

    @PostMapping("/check-text")
    @ResponseBody
    public List<Map<String, String>> checkText(@RequestBody Map<String, String> payload) {
        String originalText = payload.get("original");
        return textCheckService.checkText(originalText);
    }

}
