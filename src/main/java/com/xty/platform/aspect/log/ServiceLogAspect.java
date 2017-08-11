package com.xty.platform.aspect.log;

import com.xty.common.JsonOpera;
import com.xty.common.SystemServiceLog;
import com.xty.platModel.basic.OperationLog;
import com.xty.platModel.basic.User;
import com.xty.platform.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * Created by Shadow on 2017/8/3.
 */
public class ServiceLogAspect {
    @Resource
    private LogService logService;

    private JsonOpera jo = new JsonOpera();

    /**
     * 异常通知 用于拦截service层记录异常日志
     *
     * @param joinPoint
     */
    public  void doAfter(JoinPoint joinPoint) {
        if( RequestContextHolder.getRequestAttributes()==null)
            return;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //读取session中的用户
        User user = (User) session.getAttribute(request.getHeader("token"));
        //获取请求ip
        String ip = request.getRemoteAddr();
        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        if (joinPoint.getArgs() !=  null && joinPoint.getArgs().length > 0) {
            for ( int i = 0; i < joinPoint.getArgs().length; i++) {
                params += jo.generateJsonString(joinPoint.getArgs()[i]) + ";";
            }
        }
        try {
              /*========控制台输出=========*/
//            System.out.println("=====异常通知开始=====");
//            System.out.println("异常方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
//            System.out.println("方法描述:" + getServiceMthodDescription(joinPoint));
//            System.out.println("请求人:" + user.getFullname());
//            System.out.println("请求IP:" + ip);
//            System.out.println("请求参数:" + params);
               /*==========数据库日志=========*/
            OperationLog log = new OperationLog();
            SystemServiceLog systemServiceLog = getServiceMthodDescription(joinPoint);
            if(systemServiceLog==null)
                return;
            log.setS_module(systemServiceLog.s_module());
            log.setS_type(systemServiceLog.s_type());
            //保存数据库
            logService.save(log);
//            System.out.println("=====异常通知结束=====");
        }  catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public  static SystemServiceLog getServiceMthodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        SystemServiceLog description =null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemServiceLog.class);
                    break;
                }
            }
        }
        return description;
    }
}
