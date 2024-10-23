package org.example.cloud_storage.controllers;

import com.google.common.net.HttpHeaders;
import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Controller
@RequestMapping("/api/file")
public class FileController {

    private final MinioService minioService;

    @Autowired
    public FileController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file,
                         @RequestParam String folderName) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String fileName = file.getOriginalFilename();
        if (principal instanceof CustomUserDetails customUserDetails) {
            Long userid = customUserDetails.getId();
            minioService.upload(fileName,
                    file.getInputStream(), file.getSize(),
                    file.getContentType(), userid, folderName);
        }
        return "redirect:home/?path=" + folderName;
    }

    @PostMapping("/rename")
    public String rename(@RequestParam String oldName,
                         @RequestParam String newName) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            Long userid = customUserDetails.getId();
            minioService.renameFile(oldName,
                    userid, newName);
        }
        return "redirect:home";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam String file,
                         @RequestParam String path) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            Long userid = customUserDetails.getId();
            String cleanObjectName = file.replaceFirst("user-" + userid + "-files/", "");
            String fileName = cleanObjectName.substring(cleanObjectName.lastIndexOf("/") + 1);
            minioService.removeFile(fileName, userid, path);
        }
        return "redirect:home/?path=" + path;
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam String filename) {
        try {
            InputStream fileStream = minioService.download(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
