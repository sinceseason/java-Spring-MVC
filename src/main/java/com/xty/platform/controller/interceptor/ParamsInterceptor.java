package com.xty.platform.controller.interceptor;import com.xty.common.JsonOpera;import com.xty.platform.util.Definition;import com.xty.result.Result;import org.springframework.web.servlet.HandlerInterceptor;import org.springframework.web.servlet.ModelAndView;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;/** * @author Shadow */public class ParamsInterceptor implements HandlerInterceptor {    private JsonOpera jOpera = new JsonOpera();    public void afterCompletion(HttpServletRequest request,                                HttpServletResponse response, Object handler, Exception arg3)            throws Exception {    }    public void postHandle(HttpServletRequest request,                           HttpServletResponse response, Object arg2, ModelAndView arg3)            throws Exception {    }    public boolean preHandle(HttpServletRequest request,                             HttpServletResponse response, Object handler) throws Exception {        boolean status = false;        Result rs = new Result();        try{            if(request.getParameter("para") == null){                status = false;                rs.setResult(Definition.FAILED);                rs.setErrorCode(Definition.HAVE_NOT_ENOUGH_PARAMETERS);                response.getWriter().print(jOpera.generateJsonString(rs));            }else{                status = true;            }        }catch(Exception ex){            ex.printStackTrace();            rs.setResult(Definition.FAILED);            rs.setData(ex);            rs.setErrorCode(Definition.UNKNOW);            response.getWriter().print(jOpera.generateJsonString(rs));        }        return status;    }}