package com.hui.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/srs")
@CrossOrigin // 允许 Vue 前端跨域访问
public class SrsController {

    // 用来记录当前在线的直播间（Key: 流名称, Value: 客户端IP）
    private static final Map<String, String> onlineStreams = new ConcurrentHashMap<>();

    /**
     * OBS 开始推流时，SRS 会访问这个接口
     */
    @PostMapping("/on_publish")
    public int onPublish(@RequestBody Map<String, Object> params) {
        String ip = (String) params.get("ip");

        // 打印全部参数，你会发现新大陆（比如 client_id, vhost 等）
        System.out.println(">>> 完整参数: " + params);

        String stream = (String) params.get("stream");
        System.out.println(">>> 状态：[" + stream + "] 正在尝试开播...");

        // 业务逻辑：记录到内存里，表示直播间已开播
        onlineStreams.put(stream, ip);

        // 返回 0 代表准许推流；返回 -1 代表拒绝
        return 0;
    }

    /**
     * OBS 停止推流或网络断开时
     */
    @PostMapping("/on_unpublish")
    public int onUnpublish(@RequestBody Map<String, Object> params) {
        String stream = (String) params.get("stream");
        System.out.println("<<<<<< 直播结束，流名称: " + stream);

        // 从在线列表中移除
        onlineStreams.remove(stream);
        return 0;
    }

    /**
     * 给 Vue 前端调用的接口：获取当前有哪些人在直播
     */
    @GetMapping("/streams")
    public Map<String, String> getStreams() {
        return onlineStreams;
    }
}