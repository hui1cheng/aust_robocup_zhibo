package com.hui.controller;
import com.hui.service.MinioService;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            String fileName = minioService.uploadFile(file);
            response.put("code", 200);
            response.put("msg", "上传成功");
            response.put("fileName", fileName);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "上传失败：" + e.getMessage());
        }
        return response;
    }
}