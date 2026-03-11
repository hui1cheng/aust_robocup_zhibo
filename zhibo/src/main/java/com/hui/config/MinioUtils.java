package com.hui.config;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MinioUtils {
    @Autowired
    private MinioClient minioClient;

    /**
     * 上传并获取一个临时访问链接（有效期 2 小时）
     */
    public String uploadAndGetUrl(String bucket, String objectName, InputStream stream, String contentType) throws Exception {
        // 1. 检查并创建桶
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        
        // 2. 上传文件
        minioClient.putObject(
            PutObjectArgs.builder().bucket(bucket).object(objectName)
                .stream(stream, stream.available(), -1)
                .contentType(contentType).build()
        );

        // 3. 生成预览链接
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucket)
                .object(objectName)
                .expiry(2, TimeUnit.HOURS)
                .build()
        );
    }
}