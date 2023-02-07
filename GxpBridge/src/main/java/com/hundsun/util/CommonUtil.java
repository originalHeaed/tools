package com.hundsun.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/*********************************************************
 * 文件名称：CommonUtil.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.Util
 * 功能说明：通用的工具 util
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/20 13:57
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class CommonUtil {

    /**
     * gson 对象
     */
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    /**
     * 判断是否为空
     * @param object
     * @return
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof CharSequence) {
            return ((CharSequence)object).length() == 0;
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else if (object instanceof Collection) {
            return ((Collection)object).isEmpty();
        } else {
            return object instanceof Map ? ((Map)object).isEmpty() : false;
        }
    }

    /**
     * 将 Exception 的堆栈错误信息构造成字符串
     * @param e
     * @return
     */
    public static String getMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }

    /**
     * 当 s 为 true（不分大小写）时，返回 true，否则返回 false
     * @param s
     * @return true/ false
     */
    public static boolean strConvertBoolean(String s) {
        if (CommonUtil.isEmpty(s)) return false;
        return "true".equals(s.toLowerCase());
    }

    /**
     * 将对象转为 json
     * @param o
     * @return
     */
    public static String objectToJson(Object o) {
        return gson.toJson(o);
    }
}
