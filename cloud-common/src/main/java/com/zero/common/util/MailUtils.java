package com.zero.common.util;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @Author Zero
 * @Date 2021/5/14 0:13
 * @Since 1.8
 **/

/**
 * 需要在配置文件中配置用户名、密码等信息
 */
@Component
public class MailUtils {

    @Resource
    private JavaMailSenderImpl javaMailSender;

    /**
     * @description 参数maps中需包含subject、fromBirth、toBirth;可选text;若包含attachment，则file为空
     * @param maps
     * @param file
     * @return
     * @throws MessagingException
     */
    public Boolean sendMail(String sender,String receiver,Map<String,String> maps,MultipartFile file) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"utf-8");
        helper.setFrom(sender);
        helper.setTo(receiver);
        getParam(helper,maps,file);
        javaMailSender.send(mimeMessage);
        return true;
    }

    private void getParam(MimeMessageHelper helper, Map<String, String> maps, MultipartFile file) throws MessagingException, IOException {
        helper.setSubject(maps.get("subject"));
        if(null !=maps.get("text")){
            helper.setText(maps.get("text"), true );
        }
        if(null != maps.get("attachment") && null != maps.get("fileBirth")){
            helper.addAttachment(maps.get("attachment"), new File(maps.get("fileBirth")));
        } else {
            helper.addAttachment(file.getOriginalFilename(), file);
        }
    }
}
