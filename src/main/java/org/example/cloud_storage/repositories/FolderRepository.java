package org.example.cloud_storage.repositories;

import jakarta.transaction.Transactional;
import org.example.cloud_storage.models.Folder;
import org.example.cloud_storage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<List<Folder>> findAllByUser(User user);
    Folder findByFolderNameAndUser(String folder, User user);
    @Modifying
    @Transactional
    @Query("UPDATE Folder f SET f.folderName = :newFolderName WHERE f.folderName = :oldFolderName AND f.user.id = :userId")
    void rename(Long userId, String oldFolderName, String newFolderName);
}