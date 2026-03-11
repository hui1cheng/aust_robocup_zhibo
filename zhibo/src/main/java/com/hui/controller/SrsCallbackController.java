package com.hui.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/srs")
public class SrsCallbackController {

    @PostMapping("/on_publish")
    public int onPublish(@RequestBody Map<String, Object> srsData) {
        // 1. 打印日志，看有没有收到信息
        System.out.println("收到推流请求: " + srsData);

        // 2. 业务逻辑：比如在数据库记录 "livestream" 正在直播
        String stream = (String) srsData.get("stream");
        
        // 3. 必须返回 0，SRS 才会允许推流继续
        return 0; 
    }

    @PostMapping("/on_unpublish")
    public int onUnpublish(@RequestBody Map<String, Object> srsData) {
        System.out.println("直播停止: " + srsData);
        return 0;
    }
}