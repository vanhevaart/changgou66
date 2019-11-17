package org.changgou.controller;

import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.util.FastDFSClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/10  22:28
 */
@RestController
@CrossOrigin
public class FileController {

    @RequestMapping("/upload")
    public Result upload(@RequestParam(name = "file") MultipartFile file) {
        //1. 上传文件,获得返回信息
        String[] upload = FastDFSClient.upload(file);
        System.out.println(Arrays.toString(upload));
        //2. 拼接文件存储完整名
        String filePath = FastDFSClient.getTrackerUrl()+upload[0]+"/"+upload[1];
        return new Result(true, StatusCode.OK,"上传文件成功",filePath);
    }
}
