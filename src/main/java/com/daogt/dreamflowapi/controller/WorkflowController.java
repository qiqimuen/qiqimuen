package com.daogt.dreamflowapi.controller;



import com.daogt.dreamflowapi.model.WorkflowRequest;
import com.daogt.dreamflowapi.service.WorkflowService;
import jakarta.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController     //返回作为响应体的JSON数据，不返回页面
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Resource
    private  WorkflowService workflowService;

    // 接收前端传入的JSON数据，生成流程图并返回图片URL
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody WorkflowRequest request) {
        String imageUrl = workflowService.runWorkflow(request);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}