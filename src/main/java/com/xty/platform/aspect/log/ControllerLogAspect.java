package com.xty.platform.aspect.log;

import com.xty.common.SystemControllerLog;
import com.xty.platModel.basic.OperationLog;
import com.xty.platModel.basic.User;
import com.xty.platform.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * Created by Shadow on 2017/8/3.
 */
@Aspect
@Component
public class ControllerLogAspect {
    @Resource
    private LogService logService;

    private static final Logger logger = LoggerFactory.getLogger(ControllerLogAspect.class);

    //controller层切点
    @Pointcut("@annotation(com.xty.common.SystemControllerLog)")
    public void controllerAspect() {

    }

    //前置通知
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //读取session中的用户
        User user = (User) session.getAttribute(request.getHeader("token"));
        //请求的IP
        String ip = request.getRemoteAddr();
        try {
            //*========控制台输出=========*//
//            System.out.println("=====前置通知开始=====");
//            System.out.println("请求方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
//            System.out.println("方法描述:" + getControllerMethodDescription(joinPoint));
//            System.out.println("请求人:" + user.getFullname());
//            System.out.println("请求IP:" + ip);
            //*========数据库日志=========*//
            OperationLog log = new OperationLog();
            SystemControllerLog systemControllerLog = getControllerMethodDescription(joinPoint);
            log.setS_module(systemControllerLog.s_module());
            log.setS_type(systemControllerLog.s_type());
            //保存数据库
            logService.save(log);
        }  catch (Exception e) {
            //记录本地异常日志
            logger.error("==前置通知异常==");
            logger.error("异常信息:{}", e.getMessage());
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public  static SystemControllerLog getControllerMethodDescription(JoinPoint joinPoint)  throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        SystemControllerLog description = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemControllerLog. class);
                    break;
                }
            }
        }
        return description;
    }

}
