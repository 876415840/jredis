package org.jredis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Description: 启动类
 * @Author MengQingHao
 * @Date 2020/5/9 11:54 上午
 */
@Slf4j
@SpringBootApplication
public class JredisApplication implements DisposableBean {

    private static ConfigurableApplicationContext ctx;

    public static void main(String[] args) {

        ctx = SpringApplication.run(JredisApplication.class, args);
        for (String str : ctx.getEnvironment().getActiveProfiles()) {
            log.info(str);
        }
        log.info("Boot Server started.");

    }

    @Override
    public void destroy() throws Exception {
        if (ctx != null && ctx.isRunning()) {
            ctx.close();
        }
    }

}
