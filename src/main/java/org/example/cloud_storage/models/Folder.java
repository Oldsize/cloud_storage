package org.example.cloud_storage.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cache.annotation.Cacheable;

@Entity
@Cacheable("folders")
@Table(schema = "public", name = "folders")
@Data
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "folder", nullable = false)
    private String folderName;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Folder(String folderName, User user) {
        this.folderName = folderName;
        this.user = user;
    }

    public Folder() {
    }
}
