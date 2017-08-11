package com.xty.platform.controller.basic;

import com.xty.common.SHA1;
import com.xty.common.SystemControllerLog;
import com.xty.platModel.basic.OperationLog;
import com.xty.platModel.basic.User;
import com.xty.platform.controller.BasicController;
import com.xty.platform.util.Definition;
import com.xty.platform.util.Util;
import com.xty.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class SystemController extends BasicController {
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(String para) {
        Result result = new Result();
        try {
            para = Util.Base64Decode(para);
            User user = (User) jo.parseObject(para, User.class);
            if(user.getPassword().length() <= 16)
                user.setPassword(SHA1.Encode(user.getPassword()));

            user.setType(0);

            User loginedUser = getLoginedUser();
            if (loginedUser != null && loginedUser.getUsername().equals(user.getUsername())
                    && loginedUser.getPassword().equals(user.getPassword())) {
                logService.save(
                        new OperationLog(
                                Definition.LOG_SYSTEM_MODULE,
                                Definition.LOG_LOGIN_TYPE
                        )
                );
                result.setData(loginedUser);
                return jo.generateJsonString(result);
            }

            User account = userService.login(user);
            if (account != null) {
                session.setAttribute(account.getId().toString(), account);
                result.setData(account);
                logService.save(
                        new OperationLog(
                                Definition.LOG_SYSTEM_MODULE,
                                Definition.LOG_LOGIN_TYPE
                        )
                );
            } else {
                result.setResult(Definition.FAILED);
                result.setErrorCode(Definition.RECORD_WAS_NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            result.setResult(Definition.FAILED);
            result.setDecodeData(ex.getMessage());
        }
        return jo.generateJsonString(result);
    }

    @SystemControllerLog(s_module = Definition.LOG_SYSTEM_MODULE, s_type =Definition.LOG_LOGOUT_TYPE)
    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String logout() {
        Result result = new Result();

        try {
            User loginedUser = getLoginedUser();
            request.getSession().removeAttribute(getToken());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            result.setResult(Definition.FAILED);
            result.setDecodeData(ex.getMessage());
        }

        return jo.generateJsonString(result);
    }
}
