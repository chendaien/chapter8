package org.smart4j.chapter8.helper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter8.bean.FileParam;
import org.smart4j.chapter8.bean.FormParam;
import org.smart4j.chapter8.bean.Param;
import org.smart4j.chapter8.util.CollectionUtil;
import org.smart4j.chapter8.util.FileUtil;
import org.smart4j.chapter8.util.StreamUtil;
import org.smart4j.chapter8.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件上传助手
 */
public final class UploadHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);

    /**
     *Apache Commons FileUpload 提供的 Servlet 文件上传对象
     */
    private static ServletFileUpload servletFileUpload ;

    /**
     * 初始化
     */
    public static void init(ServletContext servletContext){
        File repository = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD,repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if(uploadLimit != 0){
            servletFileUpload.setFileSizeMax(uploadLimit*1024*1024);
        }
    }

    /**
     * 判断请求是否为multipart类型
     */
    public static boolean isMultipart(HttpServletRequest httpServletRequest){
        return ServletFileUpload.isMultipartContent(httpServletRequest);
    }

    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request)throws IOException {
        List<FormParam> formParamList = new ArrayList<FormParam>();
        List<FileParam> fileParamList = new ArrayList<FileParam>();
        try{
           Map<String,List<FileItem>> fileItemListMap = servletFileUpload.parseParameterMap(request);
           if(CollectionUtil.isNotEmpty(fileItemListMap)){
               for(Map.Entry<String,List<FileItem>> fileItemEntry:fileItemListMap.entrySet()){
                    String fieldName = fileItemEntry.getKey();
                    List<FileItem> fileItemList = fileItemEntry.getValue();
                    if(CollectionUtil.isNotEmpty(fileItemList)){
                        for(FileItem fileItem:fileItemList){
                            if(fileItem.isFormField()){
                                String fieldValue = fileItem.getString("UTF-8");
                                formParamList.add(new FormParam(fieldName,fieldValue));
                            }else{
                                String fileName = FileUtil.getRealFileName(new String(fileItem.getName().getBytes(),"UTF-8"));
                                if(StringUtil.isNotEmpty(fileName)){
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParamList.add(new FileParam(fieldName,fileName,fileSize,contentType,inputStream));
                                }
                            }
                        }
                    }
               }
           }
        }catch (FileUploadException e){
            LOGGER.error("file upload failure",e);
            throw new RuntimeException(e);
        }
        return new Param(formParamList,fileParamList);
    }

    /**
     * 上传文件
     */
    public static void uploadFile(String basePath,FileParam fileParam){
        try {
            if(fileParam != null){
                String filePath = basePath+fileParam.getFileName();
                FileUtil.createFile(filePath);
                InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                StreamUtil.copyStream(inputStream,outputStream);
            }
        }catch (Exception e){
            LOGGER.error("upload file failure ",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     */
    public static void uploadFile(String basePath,List<FileParam> fileParams){
        try{
            if(CollectionUtil.isNotEmpty(fileParams)){
                for(FileParam fileParam:fileParams){
                    uploadFile(basePath,fileParam);
                }
            }
        }catch (Exception e){
            LOGGER.error("batch upload file failure",e);
            throw new RuntimeException(e);
        }
    }

}
