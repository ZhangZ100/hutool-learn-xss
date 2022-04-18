package com.learn.wrapper;

/**
 * @author Z100
 * @create 2022-04-16 21:42
 * @desc 对HttpServletRequest 请求的数据进行转义，防止xss攻击
 **/

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;


public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 重写getParameter方法，用HtmlUtil转义后再返回
     */
    @Override
    public String getParameter(String name) {
        String value= super.getParameter(name);
        if(!StrUtil.hasEmpty(value)){
            value= HtmlUtil.filter(value);
        }
        return value;
    }

    /**
     * 重写getParameterValues方法，
     * 遍历每一个值，用HtmlUtil转义后再返回
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values= super.getParameterValues(name);
        if(values!=null){
            for (int i=0;i<values.length;i++){
                String value=values[i];
                if(!StrUtil.hasEmpty(value)){
                    value=HtmlUtil.filter(value);
                }
                values[i]=value;
            }
        }
        return values;
    }

    /**
     * 重写getParameterMap方法，
     * 拿到所有的k-v键值对，用LinkedHashMap接收，
     * key不变，value用HtmlUtil转义后再返回
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();
        LinkedHashMap<String, String[]> map=new LinkedHashMap();
        if(parameters!=null){
            for (String key:parameters.keySet()){
                String[] values=parameters.get(key);
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    if (!StrUtil.hasEmpty(value)) {
                        value = HtmlUtil.filter(value);
                    }
                    values[i] = value;
                }
                map.put(key,values);
            }
        }
        return map;
    }

    /**
     * 重写getHeader方法，用HtmlUtil转义后再返回
     */
    @Override
    public String getHeader(String name) {
        String value= super.getHeader(name);
        if (!StrUtil.hasEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        /**
         * 拿到数据流，通过StringBuffer拼接，
         * 读取到line上，用StringBuffer是因为会有多个线程同时请求，要保证线程的安全
         */
        InputStream in= super.getInputStream();
        InputStreamReader reader=new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader buffer=new BufferedReader(reader);
        StringBuffer body=new StringBuffer();
        String line=buffer.readLine();
        while(line!=null){
            body.append(line);
            line=buffer.readLine();
        }
        buffer.close();
        reader.close();
        in.close();

        /**
         * 将拿到的map，转移后存到另一个map中
         */
        Map<String,Object> map= JSONUtil.parseObj(body.toString());
        Map<String,Object> result=new LinkedHashMap<>();
        for(String key:map.keySet()){
            Object val=map.get(key);
            if(val instanceof String){
                if(!StrUtil.hasEmpty(val.toString())){
                    result.put(key,HtmlUtil.filter(val.toString()));
                }
            }else {
                result.put(key,val);
            }
        }
        String json=JSONUtil.toJsonStr(result);
        ByteArrayInputStream bain=new ByteArrayInputStream(json.getBytes());
        //匿名内部类，只需要重写read方法，把转义后的值，创建成ServletInputStream对象
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bain.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
