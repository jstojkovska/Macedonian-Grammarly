package com.veb.project.veb.web;

import com.veb.project.veb.model.Document;
import com.veb.project.veb.model.VersionHistory;
import com.veb.project.veb.service.DocumentService;
import com.veb.project.veb.service.VersionHistoryService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/history")
public class VersionHistoryController {

    private final VersionHistoryService versionHistoryService;

    public VersionHistoryController(VersionHistoryService versionHistoryService) {
        this.versionHistoryService = versionHistoryService;
    }

    @GetMapping("/versions")
    public String getAll(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Map<Document, List<VersionHistory>> allVersions = versionHistoryService.getAllVersionsGroupedByDocument(username);
            model.addAttribute("versions", allVersions);
        }
        return "all-version-history";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadVersion(@PathVariable Long id) {
        VersionHistory vh = versionHistoryService.getById(id);
        String fileName = "version_" + id + ".txt";

        ByteArrayResource resource = new ByteArrayResource(vh.getOldContent().getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
