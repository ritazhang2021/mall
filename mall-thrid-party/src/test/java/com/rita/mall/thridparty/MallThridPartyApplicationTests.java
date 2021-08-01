package com.rita.mall.thridparty;


import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class MallThridPartyApplicationTests {

    //报错是因为风险提示设置的等级太高了，不影响使用，Could not autowire. No beans of 'OSSClient' type found.
    @Autowired
    OSSClient ossClient;

    @Test
    void contextLoads() {
    }

    @Test
    public void testUpload2() throws FileNotFoundException {
        //不开启服务器也能上传。。。
        InputStream inputStream = new FileInputStream("E:\\照片\\华为-2021-6-26\\IMG_20190418_185132.jpg");
        //用spring-cloud-alicloud在yml中配置，自动注入
        ossClient.putObject("ritamall-hello", "IMG_20190418_185132.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成");
    }

}
