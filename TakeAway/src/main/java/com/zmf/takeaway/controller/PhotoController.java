package com.zmf.takeaway.controller;

import com.zmf.takeaway.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author 翟某人~
 * @version 1.0
 */
@RestController
@RequestMapping("/photo")
@Slf4j
public class PhotoController {

    @Value("${TakeAway.path}")
    private String PhotoPath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("file=: {}",file);
        String originalFilename = file.getOriginalFilename();

//        根据最后一个.的索引来截取后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString()+substring;
        File dir = new File(PhotoPath);
        if (!dir.exists()){
            dir.mkdir();
        }
        try {
            file.transferTo(new File(PhotoPath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //获取图片文件
            FileInputStream inputStream = new FileInputStream(new File(PhotoPath+name));
            //输出流
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
