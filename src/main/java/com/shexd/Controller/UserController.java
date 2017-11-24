package com.shexd.Controller;

import com.google.common.collect.Lists;
import com.shexd.Entity.User;
import com.shexd.util.DateUtils;
import com.shexd.util.ExportExcel;
import com.shexd.util.ImportExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final Logger logger= LoggerFactory.getLogger(UserController.class);

    /**
     * 下载输入数据的模板
     *
     * @param response
     */
    @RequestMapping("import/template")
    public void importFileTemplate(HttpServletResponse response){
        try {
            //定义文件名称
            String fileName = "User_Data_import_template.xlsx";
            List<User> list = Lists.newArrayList();
            new ExportExcel("User Data", User.class, 1).setDataList(list).write(response, fileName).dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * 导入已经填好数据的Excel
     * @param multipartFile
     */
    @RequestMapping(value = "import",method = RequestMethod.POST)
    public void importFile(MultipartFile multipartFile){
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(multipartFile, 1, 0);
            List<User> list = ei.getDataList(User.class);
            for (User user : list){
                try{
                    //to do: 保存处理数据
                    //userService.save(user);
                    logger.info(user.toString());
                    successNum++;
                }catch(ConstraintViolationException ex){
                    failureNum++;
                }catch (Exception ex) {
                    failureNum++;
                }
            }

            if (failureNum>0){
                failureMsg.insert(0, ", Failures: "+failureNum);
            }
            logger.info("Had Operation "+successNum+" Data;"+" "+"Failure "+failureNum);
        } catch (Exception e) {
            logger.error("导入失败",e);
        }
    }

    /**
     *
     * 导出Excel文件
     * @param response
     */
    @RequestMapping("export")
    public void export(HttpServletResponse response){
        try {
            String fileName = "User Data"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<User> users=new ArrayList<>();
            User user1=new User();
            user1.setUserName("小明");
            user1.setNickName("猪小明");
            user1.setAge(20);
            user1.setBirth(DateUtils.parseDate("1992-10-10"));
            users.add(user1);
            User user2=new User();
            user2.setUserName("小红");
            user2.setNickName("小小红");
            user2.setAge(18);
            user2.setBirth(DateUtils.parseDate("1998-11-09"));
            users.add(user2);
            new ExportExcel("User Data", User.class,2).setDataList(users).write(response, fileName).dispose();
        } catch (Exception e) {
        }
    }
}
