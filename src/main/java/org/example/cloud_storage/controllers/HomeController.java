package org.example.cloud_storage.controllers;

import org.example.cloud_storage.models.Folder;
import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.FolderService;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final FolderService folderService;
    private final MinioService minioService;

    @Autowired
    public HomeController(FolderService folderService, MinioService minioService) {
        this.folderService = folderService;
        this.minioService = minioService;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(required = false) String path) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userid;
        if (principal instanceof CustomUserDetails) {
            userid = ((CustomUserDetails) principal).getId();
            if (path == null) {
                List<Folder> folders = folderService.getAll(userid);
                model.addAttribute("folders", folders);
                return "foldersHome";
            } else {
                if (folderService.isExist(path, userid)) {
                    List<String> allFiles =
                            minioService.getAllNamesInFolder("user-files", path, userid);
                    model.addAttribute("files", allFiles);
                    model.addAttribute("path", path);
                    return "filesHome";
                } else {
                    return "redirect:home";
                }
            }
        }
        return "foldersHome";
    }
}