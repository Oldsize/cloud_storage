package org.example.cloud_storage.controllers;

import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
public class FileController {
    MinioService minioService;

    @Autowired
    public FileController(MinioService minioService) {
        this.minioService = minioService;
    }

    // todo сюда будет перенаправлять при загрузке файлов или при запросе на загрузку файла

    @PostMapping("upload")
    public String upload(@RequestParam MultipartFile file,
                         @RequestParam String folderName,
                         Principal principal) {
        try {
            String bucketName = "user-files";
            String fileName = file.getOriginalFilename();
            if (principal instanceof CustomUserDetails) {
                Long userId = ((CustomUserDetails) principal).getId();
                minioService.upload(bucketName, fileName,
                        file.getInputStream(), file.getSize(),
                        file.getContentType(), userId, folderName);
            }
        } catch (Exception e) {
            // todo forward на страницу с ошибкой
        }
        return "index";
        // todo редирект на страницу с загруженным файлом в папку
    }

    @PostMapping("rename")
    public String rename(@RequestParam String oldName,
                         @RequestParam String newName,
                         @RequestParam String folder,
                         Principal principal) {
        if (principal instanceof CustomUserDetails) {
            Long userid = ((CustomUserDetails) principal).getId();
            try {
                minioService.rename("user-files", oldName,
                        userid, folder, newName);
            } catch (Exception e) {
                // todo redirect на страницу с ошибкой
            }
        }
        return "redirect:home?path=" + folder;
    }

    @PostMapping("remove")
    public String remove() {
        // todo что нам надо знать:
        return "";
    }

    // todo метод @PostMapping("download")
    // todo       public String download() {}
}