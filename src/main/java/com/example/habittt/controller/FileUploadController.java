package com.example.habittt.controller;

import com.example.habittt.common.Result;
import com.example.habittt.service.FileUploadService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    /**
     * 上传装扮图片
     */
    @PostMapping("/costume")
    public Result<Map<String, String>> uploadCostumeImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

