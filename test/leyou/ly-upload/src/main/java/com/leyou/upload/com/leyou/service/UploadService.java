package com.leyou.upload.com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");
    @Autowired
    private FastFileStorageClient storageClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
    public String upLoad(MultipartFile file) {
        //获取文件的名称
        String originalFilename = file.getOriginalFilename();
        //判断该文件是否是文件 这个时候我们可以创建一个集合  这个集合存储着http的两种文件类型
        //如果是文件则向下走 如果不是则返回一个错误 判断它的Tepto值
        //获取该文件的类型
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            // 文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}",originalFilename);
            return null;
        }
        //走到这里则说明该文件是一个图片的类型  然后判断它的内容是否符合条件
        BufferedImage bufferedImage = null;
        try {

            bufferedImage = ImageIO.read(file.getInputStream());
            System.out.println(bufferedImage);
            //判断这个图片的类型是否合法
            if(bufferedImage==null){
                //走到这里则说明他是不合法的则抛去一个异常 并返回null
                // 文件类型不合法，直接返回null
                LOGGER.info("文件类型不合法：{}",originalFilename);
                return null;
            }
            String s = StringUtils.substringAfterLast(originalFilename, ".");
            System.out.println(s);
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), s, null);
            String fullPath = storePath.getFullPath();
//            //走到这里则说明符合全部的条件 就上传图片到本地中
//            file.transferTo(new File("F:/05b/"+originalFilename));
//
//            //上传后返回一个自定义的虚拟路径
//            //返回之后肯定会抱一个image.leyou.com的错误
//            //可以在本地的hosts文件中配置该域名 来代替localhost
           return "http://image.leyou.com/" + fullPath;

        } catch (IOException e) {
            LOGGER.info("系统发生了错误：{}",originalFilename);
            e.printStackTrace();
        }

        return null;
    }
}
