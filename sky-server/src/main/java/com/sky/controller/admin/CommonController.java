package com.sky.controller.admin;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
/**
 * 功能描述
 *
 * @Description TODO 通用接口
 * @ClassName CommonController
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf  ’.png‘
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称  把原始名的后缀加上UUID新生成的文件名名进行拼接成一个新的字符串
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            //file.getBytes():文件对象转成的数组   objectName阿里云的文件名字，防止重名利用UUID看来生成名字
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            //成功返回文件访问路径
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        //动态输出  MessageConstant.UPLOAD_FAILED 文件上传失败
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}