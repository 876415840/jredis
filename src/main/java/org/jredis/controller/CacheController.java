package org.jredis.controller;

import lombok.extern.slf4j.Slf4j;
import org.jredis.constant.JredisDbEnum;
import org.jredis.constant.code.OperateCacheErrorEnum;
import org.jredis.modules.JredisDb;
import org.jredis.modules.Jsds;
import org.jredis.vo.AppendRequestVO;
import org.jredis.vo.ResponseVO;
import org.jredis.vo.SetRequestVO;
import org.springframework.web.bind.annotation.*;

import java.net.ServerSocket;

/**
 * @Description: 缓存处理
 * @Author MengQingHao
 * @Date 2020/5/9 2:04 下午
 */
@Slf4j
@RestController
@RequestMapping("/cache")
public class CacheController {

    @PostMapping("/setExpires/{key}/{expires}")
    public ResponseVO<Void> setExpires(@PathVariable("key") String key, @PathVariable("expires") long expires) {
        if (expires <= 0) {
            return ResponseVO.ofSuccess();
        }
        JredisDb.Node<String, Object> node = JredisDbEnum.ENTITY.getJredisDb().getNode(key);
        if (node == null) {
            return ResponseVO.ofError(OperateCacheErrorEnum.KEY_NOT_FIND);
        }
        node.setExpires(expires);
        return ResponseVO.ofSuccess();
    }

    @GetMapping("/getString/{key}")
    public ResponseVO<String> getString(@PathVariable("key") String key) {
        log.info("--------------- get key--------------------1");
        if (Integer.parseInt(key) % 2 == 1) {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("--------------- get key--------------------2");
        Jsds jsds = (Jsds) JredisDbEnum.ENTITY.getJredisDb().getValue(key);
        if (jsds == null) {
            return ResponseVO.ofError(OperateCacheErrorEnum.KEY_NOT_FIND);
        }
        return ResponseVO.ofSuccess(jsds.toString());
    }

    @PostMapping("/setString")
    public ResponseVO<String> setString(@RequestBody SetRequestVO<String> setRequest) {
        long expires;
        if (!setRequest.getTimeout() || (expires = setRequest.getExpires()) < 0) {
            expires = -1;
        }
        Jsds jsds = (Jsds) JredisDbEnum.ENTITY.getJredisDb().put(setRequest.getKey(), new Jsds(setRequest.getValue()), expires);
        return ResponseVO.ofSuccess(jsds.toString());
    }

    @PostMapping("/appendString")
    public ResponseVO<String> appendString(@RequestBody AppendRequestVO<String> appendRequest) {
        Jsds jsds = (Jsds) JredisDbEnum.ENTITY.getJredisDb().getValue(appendRequest.getKey());
        if (jsds == null) {
            return ResponseVO.ofError(OperateCacheErrorEnum.KEY_NOT_FIND);
        }
        jsds.append(appendRequest.getValue());
        return ResponseVO.ofSuccess(jsds.toString());
    }
}
