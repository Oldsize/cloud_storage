package org.example.cloud_storage.services;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
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

    // todo метод upload надо будет доработать, как именно:
    // todo если юзер захочет загрузить файл не имея папок, то он будет загружаться в user-{id}-files/file
    // todo если юзер захочет загрузить файл в какую то папку или подпапку, аналогично, user-{id}-files/directory/subdirectory/file

    public InputStream download(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void remove(String bucketName, String objectName,
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


    public void rename(String bucketName, String objectName,
                           Long userid, String folder,
                           String finalName) throws Exception {
        String afterRemovedName;
        afterRemovedName = "user-" + userid + "-files/" + folder + "/" + finalName;
        objectName = "user-" + userid + "-files/" + folder + "/" + objectName;

        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );

        long size = stat.size();
        String contentType = stat.contentType();

        InputStream file = download(bucketName, objectName);

        remove(bucketName, objectName, userid, folder);


        upload(bucketName, afterRemovedName, file, size, contentType, userid, folder);

    }

    public Iterable<Result<Item>> getAllNamesInFolder(String bucketName, String fold,
                                                      Long userid) {
        String folder;
        folder = "user-" + userid + "-files/" + fold;
        return minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(folder)
                        .build()
        );
    }

    public void removeAllByFolder(String bucketName, String folderName, Long userid) throws Exception {
        Iterable<Result<Item>> files = getAllNamesInFolder(bucketName, folderName, userid);
        for (Result<Item> result : files) {
            Item item = result.get();
            String fileName = item.objectName().replace("user-" + userid + "-files/" + folderName + "/", "");
            remove(bucketName, fileName, userid, folderName);
        }
    }
}