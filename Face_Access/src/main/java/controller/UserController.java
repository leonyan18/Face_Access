package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.AuthorityEntity;
import entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import service.UserMangeService;
import util.EncryptInfo;
import util.VerifyCodeProducer;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class UserController {

    private UserMangeService userService;

    @Autowired
    public void setUserService(UserMangeService userService) {
        this.userService = userService;
    }

    Logger logger = Logger.getLogger("UserController");

    /**
     * 控制跳转，跳转到人员管理界面
     *
     * @return users.jsp
     */
    @RequestMapping("/users")
    public String showUsers() {
        return "users";
    }

    /**
     * 查看个人资料
     *
     * @param id
     * @param model
     * @return userProfile.jsp
     */
    @RequestMapping("/user")
    public String showUserProfile(@RequestParam(value = "id", defaultValue = "-1") int id,
                                  Model model) {
        UserEntity entity = userService.getUserEntity(id);
        entity.setUserTelephone(EncryptInfo.encryptTelephone(entity.getUserTelephone()));
        model.addAttribute(entity);
        return "userProfile";
    }

    /**
     * 返回包含请求 userID 下的人员信息以及相关授权信息，
     *
     * @return JSON
     */
    @RequestMapping(value = "/users.json", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String showUserJson(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userid");
        List<AuthorityEntity> list = userService.getAuthoritiesByOwner(userId);
        JSONArray array = new JSONArray();
        for (AuthorityEntity entity : list) {
            JSONObject object = new JSONObject();
            object.put("userId", entity.getUser().getUserId());
            object.put("userName", entity.getUser().getUserName());
            object.put("userTelephone", entity.getUser().getUserTelephone());
            object.put("startDate", entity.getStartDate().toString());
            object.put("endDate", entity.getEndDate().toString());
            object.put("houseId", entity.getHouse().getHouseId());
            object.put("authorityId", entity.getAuthorityId());
            object.put("remark", entity.getRemark());
            array.add(object);
        }
        return "{\"code\":0,\"msg\":\"\",\"count\":1000,\"data\":" + array + "}";

    }

    @RequestMapping("/updatePassword")
    @ResponseBody
    public String updatePassword(@RequestParam int userId,
                                 @RequestParam String oldPassword,
                                 @RequestParam String password) {
        JSONObject object = new JSONObject();
        object.put("result", "fail");
        int result = userService.updatePassword(userId, password, oldPassword);
        if (result == 1) {
            object.put("result", "success");
        }
        return JSON.toJSONString(object);
    }

    @RequestMapping("/updateTelephone")
    @ResponseBody
    public String updateTelephone(@RequestParam int userId,
                                  @RequestParam String verifyCode,
                                  @RequestParam String telephone,
                                  HttpSession session) {
        JSONObject object = new JSONObject();
        object.put("result", "fail");
        String digitVerifyCode = (String) session.getAttribute("digitVerifyCode");
        if (verifyCode.equals(digitVerifyCode)) {
            userService.updateTelephone(userId, telephone);
            object.put("result", "success");
            object.put("telephone", EncryptInfo.encryptTelephone(telephone));
        }
        return JSON.toJSONString(object);
    }

    @RequestMapping("/getDigitVerifyCode")
    @ResponseBody
    public String getUpdateCode(HttpSession session) {
        String digitVerifyCode = VerifyCodeProducer.getDigitVerifyCode();
        session.setAttribute("digitVerifyCode", digitVerifyCode);
        JSONObject object = new JSONObject();
        object.put("digitVerifyCode", digitVerifyCode);
        return JSON.toJSONString(object);
    }

    /**
     * 照片上传
     *
     * @param image
     * @return JSON
     */
    @RequestMapping("/upload")
    @ResponseBody
    public String savePicture(@RequestPart("image") MultipartFile image) {
        logger.info(String.valueOf(image.getSize()));
//        try {
//            image.transferTo(new File(image.getOriginalFilename()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "{\"code\":0,\"msg\":\"success\"}";
    }

}

