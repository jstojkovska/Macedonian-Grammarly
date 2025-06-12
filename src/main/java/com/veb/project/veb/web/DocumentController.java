package com.veb.project.veb.web;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.service.DocumentService;
import com.veb.project.veb.service.impl.DocumentServiceImpl;
import com.veb.project.veb.utils.TextUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentServiceImpl;

    public DocumentController(DocumentService documentServiceImpl) {
        this.documentServiceImpl = documentServiceImpl;
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
        System.out.println("🔍 checkText() повикан со: " + payload);

        String originalText = payload.get("original");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("original", originalText);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        String url = "https://macedoniangrammarly.free.beeceptor.com/";

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            String rawText = response.getBody();

            System.out.println("RAW text from Beeceptor:\n" + rawText);

            if (rawText == null || !rawText.contains("correction")) {
                return List.of(Map.of("error", "Нема корекција."));
            }

            List<String> correctionValues = new ArrayList<>();
            for (String line : rawText.split("\n")) {
                if (line.trim().startsWith("\"correction\"")) {
                    String value = line.split(":", 2)[1].replace("\"", "").replace(",", "").trim();
                    correctionValues.add(value);
                }
            }

            String[] originalWords = originalText.split("\\s+");
            List<Map<String, String>> corrections = new ArrayList<>();

            for (String correction : correctionValues) {
                String normalizedCorrection = correction.replace(" ", "").toLowerCase();

                for (String word : originalWords) {
                    String normalizedWord = word.toLowerCase();

                    if (normalizedWord.equals(normalizedCorrection)) {
                        corrections.add(Map.of("wrong", word, "correct", correction));
                        break;
                    }

                    if (!normalizedWord.equals(correction.toLowerCase()) &&
                            normalizedWord.length() > 3 &&
                            correction.length() > 3 &&
                            TextUtils.levenshtein(normalizedWord, correction.toLowerCase()) <= 2) {
                        corrections.add(Map.of("wrong", word, "correct", correction));
                        break;
                    }
                }
            }

            if (corrections.isEmpty()) {
                return List.of(Map.of("error", "Не се најдени зборови за замена."));
            }

            return corrections;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("error", "Грешка при поврзување со сервисот."));
        }
    }

}
