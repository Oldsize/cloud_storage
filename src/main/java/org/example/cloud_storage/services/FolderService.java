package org.example.cloud_storage.services;

import org.example.cloud_storage.models.Folder;
import org.example.cloud_storage.models.User;
import org.example.cloud_storage.repositories.FolderRepository;
import org.example.cloud_storage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    public void add(String folderName, Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        folderRepository.save(new Folder(folderName, user));
    }

    public List<Folder> getAll(Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        return folderRepository.findAllByUser(user).orElse(Collections.emptyList());
    }

    public void remove(String folderName, Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        folderRepository.delete(folderRepository.findByFolderNameAndUser(folderName, user));
    }

    public void rename(Long userid, String newName, String oldName) {
        folderRepository.rename(userid, oldName, newName);
    }

    public boolean isExist(String folderName, Long userid) {
        User user = userRepository.findById(userid).orElse(null);
        Folder folder = folderRepository.findByFolderNameAndUser(folderName, user);
        return folder != null;
    }
}