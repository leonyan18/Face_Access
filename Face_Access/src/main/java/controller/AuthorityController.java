package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.HouseEntity;
import entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.UserMangeService;
import util.DateParse;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.List;

/**
 * @author ACM-PC
 * Controller 层，调用多个 Service 完成 URL 请求 一个 Controller 控制一到多个视图
 * 不能复用，提供 Service 所需参数
 */

@Controller
public class AuthorityController {

    private UserMangeService userService;

    @Autowired

    public void setUserService(UserMangeService userService) {
        this.userService = userService;
    }

    /**
     * 控制跳转 添加人员权限界面
     *
     * @return adduser.jsp
     */
    @RequestMapping("/adduser")
    public String saveUser() {
        return "adduser";
    }

    /**
     * 返回所有人员 ID，用于模糊查找
     *
     * @return JSON
     */
    @RequestMapping("/getAllUserId")
    @ResponseBody
    public String searchUserProfile() {
        List<UserEntity> list = userService.getUserList();
        JSONArray array = new JSONArray();
        for (UserEntity entity : list) {
            JSONObject object = new JSONObject();
            object.put("id", entity.getUserId());
            array.add(object);
        }
        return JSON.toJSONString(array);
    }

    /**
     * 添加权限时，根据 ID 返回人员详情
     *
     * @param id
     * @return JSON
     */
    @RequestMapping("/user.json")
    @ResponseBody
    public String showUserProfile(@RequestParam(value = "id", defaultValue = "0") int id) {
        UserEntity entity = userService.getUserEntity(id);
        JSONObject object = new JSONObject();
        object.put("username", entity.getUserName());
        object.put("usertel", entity.getUserTelephone());
        return JSON.toJSONString(object);
    }


    /**
     * 返回该业主的所有房间 ID
     *
     * @param id
     * @return JSON
     */
    @RequestMapping("/gethouse")
    @ResponseBody
    public String getHouse(@RequestParam(value = "userId", defaultValue = "0") int id) {
        List<HouseEntity> houseEntities = userService.getHousesOfOwner(id);
        JSONArray array = new JSONArray();
        for (HouseEntity entity : houseEntities) {
            JSONObject object = new JSONObject();
            object.put("houseId", entity.getHouseId());
            array.add(object);
        }
        return JSON.toJSONString(array);
    }

    /**
     * 处理用户添加人员权限表单提交
     *
     * @param request
     * @return
     */
    @RequestMapping("/processAddUser")
    @ResponseBody
    public String processUser(HttpServletRequest request) {

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        Date startSqlDate = DateParse.stringToSql(startDate);
        Date endSqlDate = DateParse.stringToSql(endDate);
        int userId = Integer.parseInt(request.getParameter("userid"));
        int houseId = Integer.parseInt(request.getParameter("houseId"));

        userService.addAuthorityOfHouse(houseId, userId, startSqlDate, endSqlDate);

        JSONObject object = new JSONObject();
        object.put("result", "success");
        return JSON.toJSONString(object);
    }

    /**
     * 根据 authorityId 更新用户权限
     *
     * @param end
     * @param id
     * @return JSON
     */
    @RequestMapping("/updateAuthority")
    @ResponseBody
    public String updateAuthority(@RequestParam(value = "end", defaultValue = "0 ") String end,
                                  @RequestParam(value = "id", defaultValue = "0") String id) {
        Date endDate = DateParse.stringToSql(end);
        int authorityId = Integer.parseInt(id);
        userService.updateAuthorityOfHouse(authorityId, endDate);
        JSONObject object = new JSONObject();
        object.put("result", "success");
        return JSON.toJSONString(object);
    }

    /**
     * 更新权限时，返回原授权日期与失效日期给选择界面
     *
     * @param start
     * @param end
     * @param model
     * @return chooseDate.jsp
     */
    @RequestMapping("/choosedate")
    public String saveDate(@RequestParam(value = "start", defaultValue = "0") String start,
                           @RequestParam(value = "end", defaultValue = "0") String end,
                           Model model) {
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "chooseDate";
    }

    //删除用户权限
    @RequestMapping("/deleteAuthority")
    @ResponseBody
    public String deleteAuthority(HttpServletRequest request) {
        Integer authorityId;
        JSONObject object = new JSONObject();
        object.put("result", "fail");
        try {
            authorityId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return JSON.toJSONString(object);
        }

        userService.deleteAuthority(authorityId);

        object.put("result", "success");
        return JSON.toJSONString(object);
    }


}
