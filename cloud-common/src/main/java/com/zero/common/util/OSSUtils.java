package com.zero.common.util;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.zero.common.response.CommonReturnType;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Zero
 * @Date 2021/5/11 21:08
 * @Since 1.8
 **/
@Component
public class OSSUtils {

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.endpoint}")
    private String ALIYUN_OSS_ENDPOINT;

    @Value("${aliyun.oss.accessKeyId}")
    private String ALIYUN_OSS_ACCESSKEYID;

    @Value("${aliyun.oss.accessKeySecret}")
    private String ALIYUN_OSS_ACCESSKEYSECRET;

    /**
     * @description: 需要从配置文件中注入 {aliyun.oss.accessKeyId、aliyun.oss.endpoint、aliyun.oss.bucketName、aliyun.oss.accessKeySecret}
     * @return
     */
    public OSS getOssClient() {
        return new OSSClientBuilder().build(ALIYUN_OSS_ENDPOINT,ALIYUN_OSS_ACCESSKEYID,ALIYUN_OSS_ACCESSKEYSECRET);
    }

    /**
     * @description: 需要从配置文件中注入 {aliyun.oss.accessKeyId、aliyun.oss.endpoint、aliyun.oss.bucketName、aliyun.oss.accessKeySecret}
     * @param img
     * @return
     */
    public CommonReturnType uploadImg(MultipartFile img){
        OSS oss = getOssClient();
        String objectName;
        try {
            Date date = new Date();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format2 = new SimpleDateFormat("hhmmss");
            String oriName = img.getOriginalFilename();
            String suffix = oriName.substring(oriName.indexOf('.'));
            String randString = RandomStringUtils.randomAlphanumeric(5);
            objectName = "images/"+format1.format(date)+"/"+format2.format(date)+randString+suffix;
            oss.putObject(bucketName,objectName,img.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return CommonReturnType.fail("上传图片时出错", "fail");
        }finally {
            oss.shutdown();
        }
        return CommonReturnType.success("https://"+bucketName+"."+endpoint+"/"+objectName);
    }

    /**
     * 上传多张图片
     * @param img
     * @return
     */
    public CommonReturnType uploadImgs(MultipartFile[] img){
        boolean flag = true;
        Set<String> urlSet = new HashSet<>();
        for (MultipartFile multipartFile : img) {
            CommonReturnType result = uploadImg(multipartFile);
            if (result.getStatus().equals("success")){
                urlSet.add((String) result.getData());
            } else {
                flag = false;
                if(!flag) {
                    return CommonReturnType.fail(null,"fail");
                }
            }
        }
        return CommonReturnType.success(urlSet);
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    public CommonReturnType uploadFile(MultipartFile file){
        OSS oss = getOssClient();
        String objectName;
        try {
            Date date = new Date();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format2 = new SimpleDateFormat("hhmmss");
            String oriName = file.getOriginalFilename();
            String suffix = oriName.substring(oriName.indexOf('.'));
            String randString = RandomStringUtils.randomAlphanumeric(5);
            objectName = "files/"+format1.format(date)+"/"+format2.format(date)+randString+suffix;
            oss.putObject(bucketName,objectName,file.getInputStream());
        } catch (Exception e){
            e.printStackTrace();
            return CommonReturnType.fail("上传文件时出错","fail");
        }finally {
            oss.shutdown();
        }
        return CommonReturnType.success("https://"+bucketName+"."+endpoint+"/"+objectName);
    }

    public void downloadFile(String url,String fileAddress) {
        OSS ossClient = getOssClient();
        String objectName;

        String[] split = url.split("\\.");
        objectName = split[3].substring(4) + "." + split[4];
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(fileAddress));
        ossClient.shutdown();
    }
}
