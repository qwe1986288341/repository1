package com.leyou.upload.com.leyou.controller;

import com.leyou.upload.com.leyou.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService us;

    @RequestMapping("image")
    public ResponseEntity<String> upload(MultipartFile file){
        String url = us.upLoad(file);
        return ResponseEntity.ok(url);
    }
}
