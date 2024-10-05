package org.example.cloud_storage.repositories;

import jakarta.transaction.Transactional;
import org.example.cloud_storage.models.Folder;
import org.example.cloud_storage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<List<Folder>> findAllByUser(User user);
    Folder findByFolderNameAndUser(String folder, User user);
    @Transactional
    default void renameFolder(Long folderId, String newFolderName) {
        Folder folder = findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found: " + folderId));
        folder.setFolderName(newFolderName);
        save(folder);
    }
}
