package com.rita.modules.mall.product;



import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rita.modules.mall.product.entity.BrandEntity;
import com.rita.modules.mall.product.service.BrandService;

import com.rita.modules.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MallProductApplicationTests {


    @Autowired
    BrandService brandService;
    //报错是因为风险提示设置的等级太高了，不影响使用，Could not autowire. No beans of 'OSSClient' type found.
    @Autowired
    OSSClient ossClient;

    @Autowired
    CategoryService categoryService;

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setName("huawei");
        brandService.save(brandEntity);
        System.out.println("保存成功");
        brandService.updateById(brandEntity);
        //按id查询
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item)-> System.out.println(item));


    }
    //原生sdk上传文件
    @Test
    public void testUpload() throws FileNotFoundException {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-us-west-1.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，
        // 请登录RAM控制台创建RAM用户。并配置访问权限
        String accessKeyId = "LTAI5tSBASRHAR6uHVu8hXzV";
        String accessKeySecret = "CgHZpLW3bKoJSwcQRTQmH9cUITJIUL";

        // 创建OSSClient实例。通过以上三个变量，创建客户端实例，就可以对文件进行操作了
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("E:\\照片\\华为-2021-6-26\\IMG_20190523_215345.jpg");
        // 填写Bucket名称和Object完整名称。Object完整名称中不能包含Bucket名称。
        ossClient.putObject("ritamall-hello", "IMG_20190523_215345.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成");
    }

    /**
     * 1、引入oss-starter
     * 2、配置key，endpoint相关信息即可
     * 3、使用OSSClient 进行相关操作
     */

    @Test
    public void testUpload2() throws FileNotFoundException {
        //不开启服务器也能上传。。。
        InputStream inputStream = new FileInputStream("E:\\照片\\华为-2021-6-26\\IMG_20190425_161302.jpg");
        //用spring-cloud-alicloud在yml中配置，自动注入
        ossClient.putObject("ritamall-hello", "IMG_20190425_161302.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成");
    }

    @Test
    public void testFindPath(){
        Long[] cateLogPath = categoryService.findCateLogPath(225L);
        //引用Slf4j,自动变量log,{}是占位符
        log.info("路径:{}",Arrays.asList(cateLogPath));
        for (Long aLong : cateLogPath) {
            System.out.println(aLong);
        }
    }

}
