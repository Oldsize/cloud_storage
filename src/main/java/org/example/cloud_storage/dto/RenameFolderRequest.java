package org.example.cloud_storage.dto;

import lombok.Data;

@Data
public class RenameFolderRequest {
    String oldName;
    String newName;
}
