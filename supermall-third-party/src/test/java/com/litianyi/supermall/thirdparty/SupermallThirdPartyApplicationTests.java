package com.litianyi.supermall.thirdparty;

import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
@SpringBootTest
class SupermallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient;

    @Test
    void contextLoads() {
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        /*// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5tCwuAHC9rrxjjcv89j7";
        String accessKeySecret = "fR0cO2zFJgar8zLHI5LILF7LHWqCVR";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);*/

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("/Users/litianyi/Movies/谷粒商城/资料/基础/pics/2b1837c6c50add30.jpg");
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("supermall-stringli", "c6c50add303.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        log.info("上传成功");
    }
}
