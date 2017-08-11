package com.xty.platform.controller.interceptor;import com.xty.common.JsonOpera;import com.xty.platform.util.Definition;import com.xty.platform.util.Util;import com.xty.result.Result;import org.springframework.web.servlet.HandlerInterceptor;import org.springframework.web.servlet.ModelAndView;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;/** * @author Shadow */public class SqlInjectInterceptor implements HandlerInterceptor {	private JsonOpera jo = new JsonOpera();	public void afterCompletion(HttpServletRequest request,                                HttpServletResponse response, Object handler, Exception arg3)			throws Exception {	}	public void postHandle(HttpServletRequest request,                           HttpServletResponse response, Object arg2, ModelAndView arg3)			throws Exception {	}	public boolean preHandle(HttpServletRequest request,                             HttpServletResponse response, Object handler) throws Exception {		Result rs = new Result();		try {			String[] values = request.getParameterValues("para");			if (values == null || values.length == 0)				return true;			else {				String paraStr = values[0];				if(Util.IsBase64Code(paraStr))					paraStr = Util.Base64Decode(paraStr);				if (paraStr.toUpperCase().indexOf("_METHOD") >= 0						|| paraStr.toUpperCase().indexOf("DROP ") >= 0						|| paraStr.toUpperCase().indexOf("CREATE ") >= 0) {					rs.setResult(Definition.FAILED);					rs.setData("非法的sql注入！");					rs.setErrorCode(Definition.SQL_INJECT);					response.getWriter().print(jo.generateJsonString(rs));					return false;				}				return true;			}		} catch (Exception ex) {			rs.setResult(Definition.FAILED);			rs.setData(ex.toString());			rs.setErrorCode(Definition.UNKNOW);			response.getWriter().print(jo.generateJsonString(rs));			return false;		}	}}