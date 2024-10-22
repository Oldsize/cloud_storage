package org.example.cloud_storage.controllers;

import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;

@Controller
public class SearchController {

    private final MinioService minioService;

    @Autowired
    public SearchController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String fileName,
                         Model model) throws FileNotFoundException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            if (fileName != null) {
                model.addAttribute("finded", minioService.searchFilesByName("user-files", userid, fileName));
            }
        }
        return "search";
    }
}
