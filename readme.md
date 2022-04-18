## 关于本项目

本项目是使用hutool工具防止xss攻击的demo



## 测试步骤

1. **引入依赖**

~~~java
        <!--    数据转义，防止xss攻击-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.7.2</version>
        </dependency>
~~~



2. **编写包装类wrapper**

 **配置XssHttpServletRequestWrapper**, 相当于所有请求都经过包装

~~~java
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper
~~~



3. **编写filter过滤器**

使用上面编写好的wrapper来处理请求，这里没有使用springboot过滤器实现



4. **测试**

首先测试xss攻击

~~~text
localhost:8080/hello?name=<script>alert("xss攻击")</script>
~~~

浏览器输入请求，发现出现弹框



接着测试hutool防范xss攻击

- 去掉启动类上面的@ServletComponentScan的注释，相当于打开了过滤器
- 测试

~~~text
localhost:8080/hello?name=<script>alert("xss攻击")</script>
~~~

发现结果为

~~~text
hello, springboot! your name is alert("xss攻击")
~~~

- 防范xss攻击成功



## XSS攻击

待续...

