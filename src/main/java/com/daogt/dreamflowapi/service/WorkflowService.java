package com.daogt.dreamflowapi.service;

import com.daogt.dreamflowapi.model.WorkflowRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 示例工作流服务
 * 工作流模版文件位于 /src/main/resources/workflows/text2img_workflow.json
 */
@Service
public class WorkflowService {

    @Value("${comfyui.base-url}")
    private String comfyuiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();     //jackson提供的工具类，用于json序列化和反序列化

    @SuppressWarnings("unchecked")
    public String runWorkflow(WorkflowRequest request) {
        try {
            // 读取工作流模板
            File template = new ClassPathResource("workflows/text2img_workflow.json").getFile();
            Map<String, Object> workflow = mapper.readValue(template, new TypeReference<>() {});

            // 更新节点3（KSampler）的参数
            if (request.getNode3() != null) {
                Map<String, Object> node3 = (Map<String, Object>) workflow.get("3");
                Map<String, Object> originalInputs = (Map<String, Object>) node3.get("inputs");
                // 创建新的inputs Map，包含原有参数
                Map<String, Object> newInputs = new HashMap<>(originalInputs);
                // 更新用户提供的参数
                newInputs.putAll(request.getNode3().getInputs());
                // 更新节点的inputs
                node3.put("inputs", newInputs);
            }

            // 更新节点5（EmptyLatentImage）的参数
            if (request.getNode5() != null) {
                Map<String, Object> node5 = (Map<String, Object>) workflow.get("5");
                Map<String, Object> originalInputs = (Map<String, Object>) node5.get("inputs");
                // 创建新的inputs Map，包含原有参数
                Map<String, Object> newInputs = new HashMap<>(originalInputs);
                // 更新用户提供的参数
                newInputs.putAll(request.getNode5().getInputs());
                // 更新节点的inputs
                node5.put("inputs", newInputs);
            }

            // 更新节点6（正向提示词）的参数
            if (request.getNode6() != null) {
                Map<String, Object> node6 = (Map<String, Object>) workflow.get("6");
                Map<String, Object> originalInputs = (Map<String, Object>) node6.get("inputs");
                // 创建新的inputs Map，包含原有参数
                Map<String, Object> newInputs = new HashMap<>(originalInputs);
                // 更新用户提供的参数
                newInputs.putAll(request.getNode6().getInputs());
                // 更新节点的inputs
                node6.put("inputs", newInputs);
            }

            // 更新节点7（负向提示词）的参数
            if (request.getNode7() != null) {
                Map<String, Object> node7 = (Map<String, Object>) workflow.get("7");
                Map<String, Object> originalInputs = (Map<String, Object>) node7.get("inputs");
                // 创建新的inputs Map，包含原有参数
                Map<String, Object> newInputs = new HashMap<>(originalInputs);
                // 更新用户提供的参数
                newInputs.putAll(request.getNode7().getInputs());
                // 更新节点的inputs
                node7.put("inputs", newInputs);
            }

            // 创建包含 prompt 字段的请求体
            Map<String, Object> requestBody = Map.of("prompt", workflow);

            // 提交工作流任务
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);

            Map<String, String> response = restTemplate.postForObject(comfyuiBaseUrl + "/prompt", entity, Map.class);
            String promptId = response.get("prompt_id");

            // 轮询获取结果
            String resultUrl = waitForResult(promptId);
            return resultUrl;

        } catch (Exception e) {
            throw new RuntimeException("工作流处理失败", e);
        }
    }

    private String waitForResult(String promptId) throws InterruptedException {
        String historyUrl = comfyuiBaseUrl + "/history/" + promptId;
        for (int i = 0; i < 30; i++) {
            ResponseEntity<Map> response = restTemplate.getForEntity(historyUrl, Map.class);
            System.out.println(response.getBody());
            if (response.getBody() != null && !response.getBody().isEmpty()) {
                String imageName = extractImageFilename(response.getBody());
                return comfyuiBaseUrl + "/view?filename=" + imageName + "&type=output";
            }
            Thread.sleep(1000);
        }
        throw new RuntimeException("生成超时");
    }

    @SuppressWarnings("unchecked")
    private String extractImageFilename(Map body) {
        System.out.println("开始解析响应体: " + body);
        // 遍历每个 prompt 执行结果
        for (Object value : body.values()) {
            Map<String, Object> promptResult = (Map<String, Object>) value;
            
            // 获取 outputs 部分
            Map<String, Object> outputs = (Map<String, Object>) promptResult.get("outputs");
            if (outputs == null) {
                System.out.println("未找到 outputs 部分");
                continue;
            }
            System.out.println("找到 outputs: " + outputs);

            // 获取节点9（SaveImage）的输出
            Map<String, Object> node9Output = (Map<String, Object>) outputs.get("9");
            if (node9Output == null) {
                System.out.println("未找到节点9的输出");
                continue;
            }
            System.out.println("找到节点9输出: " + node9Output);

            // 获取 images 数组
            List<Map<String, Object>> images = (List<Map<String, Object>>) node9Output.get("images");
            if (images != null && !images.isEmpty()) {
                // 获取第一张图片的文件名
                Map<String, Object> firstImage = images.get(0);
                String filename = (String) firstImage.get("filename");
                System.out.println("找到图片文件名: " + filename);
                return filename;
            }
            System.out.println("未找到图片信息");
        }
        throw new RuntimeException("未找到生成的图片");
    }

}
