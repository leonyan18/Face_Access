package controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.DoorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.DoorMangeServiceInterface;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class DoorController {

    private DoorMangeServiceInterface doorService;

    @Autowired
    public void setDoorService(DoorMangeServiceInterface doorService) {
        this.doorService = doorService;
    }

    @RequestMapping(value="doorsjson", produces = "text/html; charset=utf-8")
    @ResponseBody
    public String showDoorJSon(@RequestParam(value = "page",defaultValue = "1")String page,@RequestParam(value = "limit",defaultValue = "10")String limit) throws UnsupportedEncodingException {
        List<DoorEntity> list = doorService.getDoorList(Integer.parseInt(page),Integer.parseInt(limit));
        JSONArray array = new JSONArray();
        for (DoorEntity entity : list) {
            JSONObject object = new JSONObject();
            object.put("Id", entity.getDoorId());
            object.put("Location", entity.getDoorLocation());
            object.put("Status", entity.getDoorStasue());
            object.put("Ip", entity.getDoorIp());
            array.add(object);
        }
        String ans="{\"code\":0,\"msg\":\"\",\"count\":1000,\"data\":" + array + "}";
        return ans;
    }
    @RequestMapping("/doors")
    public String showdoors(){ return "doors"; }
    @RequestMapping("/updatedoorview")
    public String showupdatedoorview(@RequestParam (value = "id")String id, @RequestParam (value = "location")String location, @RequestParam (value = "ip")String ip, @RequestParam (value = "status")String status, Model model){
        model.addAttribute("id",id);
        model.addAttribute("ip",ip);
        model.addAttribute("location",location);
        model.addAttribute("status",status);
        return "updatedoors";
    }
    @RequestMapping("/updatedoor")
    @ResponseBody
    public String updatedoor(@RequestParam (value = "id")String id, @RequestParam (value = "location")String location, @RequestParam (value = "ip")String ip, @RequestParam (value = "status")String status){
        DoorEntity doorEntity=new DoorEntity();
        doorEntity.setDoorId(Integer.parseInt(id));
        doorEntity.setDoorStasue(status);
        doorEntity.setDoorIp(ip);
        doorEntity.setDoorLocation(location);
        return  doorService.update(doorEntity);
    }
    @RequestMapping("/deldoor")
    public String deldoor(@RequestParam(value = "id")String id){
        return doorService.delete(Integer.parseInt(id));
    }

    @RequestMapping("/adddoorview")
    public String adddoorview(){ return "adddoor";}

    @RequestMapping("/adddoor")
    @ResponseBody
    public String adddoor(@RequestParam (value = "location")String location, @RequestParam (value = "ip")String ip, @RequestParam (value = "status")String status){
        DoorEntity doorEntity=new DoorEntity();
        doorEntity.setDoorLocation(location);
        doorEntity.setDoorIp(ip);
        doorEntity.setDoorStasue(status);
        doorService.adddoor(doorEntity);
        return "success";
    }
}
