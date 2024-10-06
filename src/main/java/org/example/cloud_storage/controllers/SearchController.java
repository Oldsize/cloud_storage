package org.example.cloud_storage.controllers;

import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileNotFoundException;
import java.security.Principal;

@Controller
public class SearchController {

    MinioService minioService;

    @Autowired
    public SearchController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/search")
    public String search(String folderName, String fileName,
                         Model model, Principal principal) {
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            try {
                model.addAttribute("finded", minioService.searchFilesByName("user-files", folderName, userid, fileName));
            } catch (FileNotFoundException e) {
                // todo redirect на страницу с ошибкой
            }
        }
        return "search";
    }
}
