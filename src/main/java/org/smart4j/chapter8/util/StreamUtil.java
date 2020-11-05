package org.smart4j.chapter8.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 流操作工具类
 */
public final class StreamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

    /**
     * 从输入流中获取字符串
     */
    public static String getString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedInputStream.readLine()) != null){
                stringBuilder.append(line);
            }
        }catch (IOException e){
            LOGGER.error("read InputStream failure",e);
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

    /**
     * 将输入流复制到输出流
     */
    public static void copyStream(InputStream inputStream,OutputStream outputStream){
        try {
            int length;
            byte[] buffer = new byte[4*1024];
            while ((length=inputStream.read(buffer,0,buffer.length))!=-1){
                outputStream.write(buffer,0,length);
            }
            outputStream.flush();
        }catch (Exception e){
            LOGGER.error("copy inputStream to outputStream failure",e);
            throw new RuntimeException(e);
        }
    }

}
