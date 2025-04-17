# DreamFlow API

这是一个基于 Spring Boot 的 RESTful API 服务，用于与 ComfyUI 进行交互，实现 AI 图像生成功能。

## 项目说明

本项目是一个 Java 后端服务，主要功能是：
- 接收用户的图像生成请求
- 处理并转换为 ComfyUI 可识别的工作流格式
- 与本地运行的 ComfyUI 服务交互
- 返回生成的图片 URL

## 技术栈

- Java 17
- Spring Boot 3.4.4
- Spring Web
- Jackson (JSON 处理)
- Lombok
- Maven

## 系统要求

- JDK 17 或更高版本
- Maven 3.6.0 或更高版本
- 本地运行的 ComfyUI 服务

## 快速开始

1. 确保本地已安装并运行 ComfyUI

2. 克隆项目
```bash
git clone [项目地址]
cd dreamFlowAPI
```

3. 配置 ComfyUI 地址
在 `application.properties` 中配置：
```properties
comfyui.base-url=http://localhost:8188
```

4. 构建项目
```bash
mvn clean install
```

5. 运行项目
```bash
mvn spring-boot:run
```

## API 使用说明

### 生成图片 API

- 端点：`POST /api/workflow/generate`
- 请求体示例：
```json
{
  "3": {
    "inputs": {
      "seed": 156680208700286,
      "steps": 20,
      "cfg": 8,
      "sampler_name": "euler",
      "scheduler": "normal",
      "denoise": 1
    },
    "class_type": "KSampler"
  },
  "5": {
    "inputs": {
      "width": 512,
      "height": 512,
      "batch_size": 1
    },
    "class_type": "EmptyLatentImage"
  },
  "6": {
    "inputs": {
      "text": "beautiful scenery"
    },
    "class_type": "CLIPTextEncode"
  },
  "7": {
    "inputs": {
      "text": "text, watermark"
    },
    "class_type": "CLIPTextEncode"
  }
}
```

- 响应示例：
```json
{
  "imageUrl": "http://localhost:8188/view?filename=ComfyUI_123456.png&type=output"
}
```

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/daogt/dreamflowapi/
│   │       ├── controller/
│   │       │   └── WorkflowController.java    # API 控制器
│   │       ├── service/
│   │       │   └── WorkflowService.java       # 业务逻辑处理
│   │       ├── model/
│   │       │   └── WorkflowRequest.java       # 请求数据模型
│   │       └── DreamFlowApiApplication.java    # 应用入口
│   └── resources/
│       ├── application.properties             # 配置文件
│       └── workflows/
│           └── text2img_workflow.json         # 工作流模板
└── test/                                      # 测试代码
```

## 工作流说明

项目使用了预定义的工作流模板（`text2img_workflow.json`），包含以下主要节点：
- 节点3：KSampler - 采样器配置
- 节点5：EmptyLatentImage - 图像尺寸设置
- 节点6：CLIPTextEncode - 正向提示词
- 节点7：CLIPTextEncode - 负向提示词

## 开发说明

### 添加新的工作流

1. 在 `resources/workflows/` 目录下添加新的工作流 JSON 文件
2. 在 `WorkflowService` 中添加相应的处理逻辑
3. 在 `WorkflowRequest` 中添加新的参数字段

### 错误处理

服务会处理以下情况：
- ComfyUI 服务不可用
- 参数验证失败
- 图片生成超时
- 结果解析错误

## 注意事项

1. 确保 ComfyUI 服务正常运行
2. 检查网络连接和端口配置
3. 注意请求参数的格式和类型
4. 图片生成可能需要一定时间，服务设置了 30 秒的超时时间

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 发起 Pull Request

## 许可证

[待添加许可证信息] 