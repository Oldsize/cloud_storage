package org.example.cloud_storage.services;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final FolderService folderService;

    @Autowired
    public MinioService(MinioClient minioClient, FolderService folderService) {
        this.minioClient = minioClient;
        this.folderService = folderService;
    }

    public void upload(String bucketName, String objectName,
                       InputStream fileStream, Long size,
                       String contentType, Long userid, String folderName) throws Exception {
        objectName = "user-" + userid.toString() + "-files/" + folderName + "/" + objectName;
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

    public InputStream download(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void removeFile(String bucketName, String objectName,
                           Long userid, String folder) throws Exception {
        objectName = "user-" + userid + "-files/" + folder + "/" + objectName;
        minioClient.removeObject(
                RemoveObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void renameFile(String bucketName, String objectName,
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
        InputStream file = download(bucketName, objectName);
        removeFile(bucketName, fileName, userid, folder);
        upload(bucketName, finalName, file, size, contentType, userid, folder);
    }

    public List<String> getAllNamesInFolder(String bucketName, String folderName, Long userid) throws Exception {
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

    public void removeAllByFolder(String bucketName, String folderName, Long userid) throws Exception {
        List<String> fileNames = getAllNamesInFolder(bucketName, folderName, userid);
        for (String fileName : fileNames) {
            String cleanFileName = fileName.replace("user-" + userid + "-files/" + folderName + "/", "");
            removeFile(bucketName, cleanFileName, userid, folderName);
        }
    }

    public List<String> searchFilesByName(String bucketName, Long userId, String fileName) throws FileNotFoundException {
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

    public void renameFolder(String bucketName, String oldName, Long userid, String newName) throws Exception {
        folderService.rename(userid, newName, oldName);
        List<String> fileNames = getAllNamesInFolder(bucketName, oldName, userid);
        if (fileNames != null && !fileNames.isEmpty()) {
            for (String oldFileName : fileNames) {
                String cleanFileName = oldFileName.replace("user-" + userid + "-files/" + oldName + "/", "");
                InputStream inputStream = download(bucketName, oldFileName);
                StatObjectResponse stat = minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(oldFileName)
                                .build()
                );
                upload(bucketName, cleanFileName, inputStream, stat.size(), stat.contentType(), userid, newName);
                removeFile(bucketName, cleanFileName, userid, oldName);
            }
            removeAllByFolder(bucketName, oldName, userid);
        }
    }
}