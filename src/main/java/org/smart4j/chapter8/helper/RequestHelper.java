package org.smart4j.chapter8.helper;

import org.apache.commons.lang3.StringUtils;
import org.smart4j.chapter8.bean.FormParam;
import org.smart4j.chapter8.bean.Param;
import org.smart4j.chapter8.util.ArrayUtil;
import org.smart4j.chapter8.util.CodecUtil;
import org.smart4j.chapter8.util.StreamUtil;
import org.smart4j.chapter8.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 请求助手类
 */
public final class RequestHelper {

    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request)throws IOException {
        List<FormParam> formParamList = new ArrayList<FormParam>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));
        return new Param(formParamList);
    }

    /**
     *解析请求URL上的参数值
     */
    private static List<FormParam> parseParameterNames(HttpServletRequest request){
        List<FormParam> formParamList = new ArrayList<FormParam>();
        Enumeration<String> requestParams = request.getParameterNames();
        while (requestParams.hasMoreElements()){
            String paramName = requestParams.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if(ArrayUtil.isNotEmpty(paramValues)){
                String paramValue;
                if(paramValues.length==1){
                    paramValue = paramValues[0];
                }else{
                    StringBuilder strBuilder = new StringBuilder("");
                    for(int index=0;index<paramValues.length;index++){
                          String paramVal = paramValues[index];
                          if(index==(paramValues.length-1)){
                              strBuilder.append(paramVal);
                              break;
                          }
                        strBuilder.append(paramVal+ StringUtil.SEPARATOR);
                    }
                    paramValue = strBuilder.toString();
                }
                formParamList.add(new FormParam(paramName,paramValue));
            }
        }
        return formParamList;
    }

    /**
     * 解析请求body里的参数值
     */
    private static List<FormParam> parseInputStream(HttpServletRequest request)throws IOException{
        List<FormParam> formParamList = new ArrayList<FormParam>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            String[] paramStrs = StringUtils.split(body,"&");
            if(ArrayUtil.isNotEmpty(paramStrs)){
                for (String paramStr:paramStrs) {
                    String[] array = StringUtils.split(paramStr,"=");
                    if(ArrayUtil.isNotEmpty(array)&&array.length==2){
                        String paramName = array[0];
                        String paramValue = array[1];
                        formParamList.add(new FormParam(paramName,paramValue));
                    }
                }
            }
        }
        return formParamList;
    }


}
