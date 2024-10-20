package org.example.cloud_storage.controllers;

import org.example.cloud_storage.dto.RenameFolderRequest;
import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.FolderService;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/folder")
public class FolderController {

    private final FolderService folderService;
    private final MinioService minioService;

    @Autowired
    public FolderController(FolderService folderService, MinioService minioService) {
        this.folderService = folderService;
        this.minioService = minioService;
    }

    @PostMapping("/create")
    public String create(@RequestParam String name) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            folderService.add(name, userid);
        }
        return "redirect:home";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam String name) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            minioService.removeAllByFolder("user-files", name, userid);
            folderService.remove(name, userid);
        }
        return "redirect:home";
    }

    @PostMapping("/rename")
    public ResponseEntity<String> rename(@RequestBody RenameFolderRequest request) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String oldName = request.getOldName();
        String newName = request.getNewName();
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            minioService.renameFolder("user-files", oldName, userid, newName);
            return ResponseEntity.ok("Папка успешно переименована");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не авторизован");
    }
}