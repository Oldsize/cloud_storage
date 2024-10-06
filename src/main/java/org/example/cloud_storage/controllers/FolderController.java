package org.example.cloud_storage.controllers;

import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.FolderService;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/folder")
public class FolderController {

    FolderService folderService;
    MinioService minioService;

    @Autowired
    public FolderController(FolderService folderService, MinioService minioService) {
        this.folderService = folderService;
        this.minioService = minioService;
    }

    @PostMapping("/create")
    public String create(@RequestParam String name,
                         Principal principal) {
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            folderService.add(name, userid);
        }
        return "foldersHome";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam String name,
                         Principal principal) {
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            folderService.remove(name, userid);
            try {
                minioService.removeAllByFolder("user-files", name, userid);
            } catch (Exception e) {
                // todo redirect на страницу с ошибкой
            }
        }
        return "foldersHome";
    }
}
