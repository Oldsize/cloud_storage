package org.example.cloud_storage.services;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioService {
    @Value("${minio.bucketname}")
    private String bucketName;
    private final MinioClient minioClient;
    private final FolderService folderService;

    @Autowired
    public MinioService(MinioClient minioClient, FolderService folderService) {
        this.minioClient = minioClient;
        this.folderService = folderService;
    }

    // todo method private String concatenation

    private String concatenation(String objectName, String folderName, Long userid) {
        return "user-" + userid + "-files/" + folderName + "/" + objectName;
    }

    public void upload(String objectName, InputStream fileStream, Long size,
                       String contentType, Long userid, String folderName) throws Exception {
        objectName = concatenation(objectName, folderName, userid);
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(fileStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    public InputStream download(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void removeFile(String objectName,
                           Long userid, String folder) throws Exception {
        objectName = concatenation(objectName, folder, userid);
        minioClient.removeObject(
                RemoveObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void renameFile(String objectName,
                           Long userid, String finalName) throws Exception {
        String cleanObjectName = objectName.replaceFirst("user-" + userid + "-files/", "");
        String fileName = cleanObjectName.substring(cleanObjectName.lastIndexOf("/") + 1);
        String folder = cleanObjectName.contains("/") ? cleanObjectName.substring(0, cleanObjectName.lastIndexOf("/")) : "";
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
        long size = stat.size();
        String contentType = stat.contentType();
        InputStream file = download(objectName);
        removeFile(fileName, userid, folder);
        upload(finalName, file, size, contentType, userid, folder);
    }

    public List<String> getAllNamesInFolder(String folderName, Long userid) throws Exception {
        String folder = "user-" + userid + "-files/" + folderName;
        List<String> fileNames = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(folder + "/")
                        .build()
        );

        for (Result<Item> result : results) {
            Item item = result.get();
            fileNames.add(item.objectName());
        }
        return fileNames;
    }

    public void removeAllByFolder(String folderName, Long userid) throws Exception {
        List<String> fileNames = getAllNamesInFolder(folderName, userid);
        for (String fileName : fileNames) {
            String cleanFileName = fileName.replace("user-" + userid + "-files/" + folderName + "/", "");
            removeFile(cleanFileName, userid, folderName);
        }
    }

    public List<String> searchFilesByName(Long userId, String fileName) throws FileNotFoundException {
        String userFolderPrefix = "user-" + userId + "-files/";
        Iterable<Result<Item>> files = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(userFolderPrefix)
                        .recursive(true)
                        .build()
        );

        List<String> matchingFiles = new ArrayList<>();
        for (Result<Item> result : files) {
            try {
                Item item = result.get();
                if (item.objectName().endsWith(fileName)) {
                    matchingFiles.add(item.objectName());
                }
            } catch (Exception e) {
                throw new FileNotFoundException("Ошибка при поиске файлов: " + e.getMessage());
            }
        }
        return matchingFiles;
    }

    public void renameFolder(String oldName, Long userid, String newName) throws Exception {
        folderService.rename(userid, newName, oldName);
        List<String> fileNames = getAllNamesInFolder(oldName, userid);
        if (fileNames != null && !fileNames.isEmpty()) {
            for (String oldFileName : fileNames) {
                String cleanFileName = oldFileName.replace("user-" + userid + "-files/" + oldName + "/", "");
                InputStream inputStream = download(oldFileName);
                StatObjectResponse stat = minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(oldFileName)
                                .build()
                );
                upload(cleanFileName, inputStream, stat.size(), stat.contentType(), userid, newName);
                removeFile(cleanFileName, userid, oldName);
            }
            removeAllByFolder(oldName, userid);
        }
    }
}
