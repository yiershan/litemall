package org.linlinjava.litemall.admin.web;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.service.LitemallAdService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/ad")
@Validated
public class AdminAdController {
    private final Log logger = LogFactory.getLog(AdminAdController.class);

    @Autowired
    private LitemallAdService adService;
    @ApiOperation(value = "获取广告列表", notes = "获取广告列表分页数据")
    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallAd> adList = adService.querySelective(name, content, page, limit, sort, order);
        int total = adService.countSelective(name, content, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adList);

        return ResponseUtil.ok(data);
    }

    private Object validate(LitemallAd ad) {
        String name = ad.getName();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.badArgument();
        }
        String content = ad.getName();
        if(StringUtils.isEmpty(content)){
            return ResponseUtil.badArgument();
        }
        return null;
    }
    @ApiOperation(value = "创建一则广告",notes = "通过调用此方法可以创建一则广告")
    @ApiImplicitParam(name = "adminId", value = "登陆用户id", required = true, dataType = "Integer")
    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallAd ad){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        Object error = validate(ad);
        if(error != null){
            return error;
        }
        ad.setAddTime(LocalDateTime.now());
        adService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, @NotNull Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        LitemallAd brand = adService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallAd ad){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        Object error = validate(ad);
        if(error != null){
            return error;
        }
        if(adService.updateById(ad) == 0){
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(ad);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallAd ad){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        Integer id = ad.getId();
        if(id == null){
            return ResponseUtil.badArgument();
        }
        adService.deleteById(id);
        return ResponseUtil.ok();
    }

}
