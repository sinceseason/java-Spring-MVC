package com.xty.platform.controller.basic;

import com.xty.domain.FuzzyCondition;
import com.xty.platModel.basic.User;
import com.xty.platform.controller.BasicController;
import com.xty.platform.util.Definition;
import com.xty.platform.util.Util;
import com.xty.result.Result;
import com.xty.result.ResultData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/8/9.
 */
public class SystemUserController extends BasicController {
    @RequestMapping(value = "/systemUserFuzzy", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String fuzzy(String para) {
        Result result = new Result();

        try {
            User loginedUser = getLoginedUser();
            para = Util.Base64Decode(para);
            FuzzyCondition fuzzyCondition = (FuzzyCondition) jo.parseObject(para, FuzzyCondition.class);
            ResultData resultData = userService.fuzzy(loginedUser, fuzzyCondition);
            result.setData(resultData);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            result.setResult(Definition.FAILED);
            result.setDecodeData(ex.getMessage());
        }

        return jo.generateJsonString(result);
    }
}
