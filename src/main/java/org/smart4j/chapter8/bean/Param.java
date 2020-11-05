package org.smart4j.chapter8.bean;

import org.apache.commons.lang3.StringUtils;
import org.smart4j.chapter8.util.CastUtil;
import org.smart4j.chapter8.util.CollectionUtil;
import org.smart4j.chapter8.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数对象
 */
public class Param {

    private List<FormParam> formParamList;

    private List<FileParam> fileParamList;

    public Param(List<FormParam> formParamList) {
        this.formParamList = formParamList;
    }

    public Param(List<FormParam> formParamList, List<FileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    /**
     * 获取请求参数映射
     */
    public Map<String,Object> getFieldMap(){
        Map<String,Object> fieldMap = new HashMap<String, Object>();
        if(CollectionUtil.isNotEmpty(formParamList)){
            for(FormParam formParam:formParamList){
                String fieldName = formParam.getFieldName();
                String fieldValue = formParam.getFieldValue();
                if(fieldMap.containsKey(fieldName)){
                    fieldValue = fieldMap.get(fieldName)+ StringUtil.SEPARATOR+fieldValue;
                }
                fieldMap.put(fieldName,fieldValue);
            }
        }
        return fieldMap;
    }

    /**
     * 获取文件上传映射
     */
    public Map<String,List<FileParam>> getFileMap(){
        Map<String,List<FileParam>> fileMap = new HashMap<String, List<FileParam>>();
        if(CollectionUtil.isNotEmpty(fileParamList)){
            for(FileParam fileParam:fileParamList){
                String fieldName = fileParam.getFieldName();
                List<FileParam> fileParams;
                if(fileMap.containsKey(fieldName)){
                    fileParams = fileMap.get(fieldName);
                }else{
                    fileParams = new ArrayList<FileParam>();
                }
                fileParams.add(fileParam);
                fileMap.put(fieldName,fileParams);
            }
        }
        return fileMap;
    }

    /**
     * 获取所有文件
     */
    public List<FileParam> getFileList(String fieldName){
        return getFileMap().get(fieldName);
    }

    /**
     * 获取单个文件
     */
    public FileParam getFile(String fieldName){
        List<FileParam> fileParams =getFileMap().get(fieldName);
        if(CollectionUtil.isNotEmpty(fileParams)&&fileParams.size()==1){
            return fileParams.get(0);
        }
        return null;
    }

    /**
     * 验证参数是否为空
     */
    public boolean isEmpty(){
        return CollectionUtil.isEmpty(formParamList)&&CollectionUtil.isEmpty(fileParamList);
    }

    /**
     * 根据参数名获取String型参数值
     */
    public String getString(String name){
        return CastUtil.castString(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取double型参数值
     */
    public Double getDouble(String name){
        return CastUtil.castDouble(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取long型参数值
     */
    public Long getLong(String name){
        return CastUtil.castLong(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取int型参数值
     */
    public Integer getInt(String name){
        return CastUtil.castInt(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取boolean型参数值
     */
    public Boolean getBoolean(String name){
        return CastUtil.castBoolean(getFieldMap().get(name));
    }

}
