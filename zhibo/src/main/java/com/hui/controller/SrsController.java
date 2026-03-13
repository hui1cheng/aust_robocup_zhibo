package com.hui.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/srs")
@CrossOrigin // 允许前端跨域
public class SrsController {

    // Key: 流名称（livestream_2d/3d/rescue）, Value: 推流PC的IP
    private static final Map<String, String> onlineStreams = new ConcurrentHashMap<>();

    /**
     * OBS推流到你的SRS时，SRS会调用这个接口
     */
    @PostMapping("/on_publish")
    public int onPublish(@RequestBody Map<String, Object> params) {
        try {
            String stream = (String) params.get("stream"); // 推流码（如 livestream_2d）
            String clientIp = (String) params.get("ip"); // 推流PC的IP（3台PC的局域网IP）

            System.out.printf(">>> [%s] 推流成功 | 推流PC: %s%n", stream, clientIp);
            onlineStreams.put(stream, clientIp); // 记录流
            return 0; // 准许推流
        } catch (Exception e) {
            System.err.println("推流回调异常: " + e.getMessage());
            return -1;
        }
    }

    /**
     * OBS停止推流时，SRS调用这个接口
     */
    @PostMapping("/on_unpublish")
    public int onUnpublish(@RequestBody Map<String, Object> params) {
        String stream = (String) params.get("stream");
        onlineStreams.remove(stream);
        System.out.println("<<<<<< [" + stream + "] 推流结束");
        return 0;
    }

    /**
     * 前端获取所有在线流（供页面展示）
     */
    @GetMapping("/streams")
    public Map<String, String> getStreams() {
        return onlineStreams;
    }
}