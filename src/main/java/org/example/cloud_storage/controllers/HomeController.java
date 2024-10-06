package org.example.cloud_storage.controllers;

import io.minio.Result;
import io.minio.messages.Item;
import org.example.cloud_storage.models.Folder;
import org.example.cloud_storage.security.CustomUserDetails;
import org.example.cloud_storage.services.FolderService;
import org.example.cloud_storage.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    FolderService folderService;
    MinioService minioService;

    @Autowired
    public HomeController(FolderService folderService, MinioService minioService) {
        this.folderService = folderService;
        this.minioService = minioService;
    }

    @GetMapping("/home")
    public String home(Principal principal, Model model,
                       @RequestParam(required = false) String path) {
        Long userid;
        if (principal instanceof CustomUserDetails) {
            userid = ((CustomUserDetails) principal).getId();
            if (path == null) {
                List<Folder> folders = folderService.getAll(userid);
                if (folders != null) {
                    model.addAttribute("folders", folders);
                    // todo esli null to pokazivaem chto nichego netu
                }
                // todo если path == null мы показываем папки,
                // todo если path != null мы ищем по path папку и показываем ее
                return "foldersHome";
            } else {
                try {
                    Iterable<Result<Item>> allFiles =
                            minioService.getAllNamesInFolder("user-files", path, userid);
                    model.addAttribute("files", allFiles);
                    // todo можно сделать две страницы, одна под файлы, другая под папки.
                    return "filesHome";
                } catch (Exception e) {
                    // todo redirect на страницу с ошибкой
                }
            }
        }
        return "foldersHome";
    }
}