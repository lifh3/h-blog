package com.blog.service;

import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * MCP (Model Context Protocol) 工具服务
 * 提供 AI 可调用的工具函数，用于自动选择分类和标签
 */
@Service
public class McpToolService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取所有可用的分类列表
     */
    public Map<String, Object> getCategories() {
        List<Category> categories = categoryService.listAll();
        List<Map<String, Object>> categoryList = new ArrayList<>();
        for (Category cat : categories) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", cat.getId());
            item.put("name", cat.getName());
            item.put("description", cat.getDescription() != null ? cat.getDescription() : "");
            item.put("alias", cat.getAlias() != null ? cat.getAlias() : "");
            categoryList.add(item);
        }
        return Map.of(
            "categories", categoryList,
            "count", categoryList.size()
        );
    }

    /**
     * 获取所有可用的标签列表
     */
    public Map<String, Object> getTags() {
        List<Tag> tags = tagService.listAll();
        List<Map<String, Object>> tagList = new ArrayList<>();
        for (Tag tag : tags) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", tag.getId());
            item.put("name", tag.getName());
            item.put("count", tag.getCount() != null ? tag.getCount() : 0);
            tagList.add(item);
        }
        return Map.of(
            "tags", tagList,
            "count", tagList.size()
        );
    }

    /**
     * 分析文章内容，智能推荐分类和标签
     * @param content 文章内容（标题+正文）
     * @return 推荐结果 {categoryId, categoryName, tagIds, tagNames, reason}
     */
    public Map<String, Object> analyzeAndRecommend(String content) {
        // 获取所有分类和标签
        List<Category> categories = categoryService.listAll();
        List<Tag> tags = tagService.listAll();

        // 简单的关键词匹配算法
        String lowerContent = content.toLowerCase();

        // 分类关键词映射
        Map<String, List<Long>> categoryKeywords = new HashMap<>();
        categoryKeywords.put("ai", List.of(1L));  // AI每日热点情报
        categoryKeywords.put("人工智能", List.of(1L));
        categoryKeywords.put("大模型", List.of(1L));
        categoryKeywords.put("llm", List.of(1L));
        categoryKeywords.put("chatgpt", List.of(1L));
        categoryKeywords.put("项目", List.of(2L));  // 项目分享
        categoryKeywords.put("开源", List.of(2L));
        categoryKeywords.put("github", List.of(2L));
        categoryKeywords.put("demo", List.of(2L));
        categoryKeywords.put("教程", List.of(2L));
        categoryKeywords.put("实战", List.of(2L));

        // 标签关键词映射
        Map<String, String> tagKeywords = new HashMap<>();
        tagKeywords.put("ai", "AI");
        tagKeywords.put("人工智能", "AI");
        tagKeywords.put("大模型", "AI");
        tagKeywords.put("llm", "AI");
        tagKeywords.put("python", "Python");
        tagKeywords.put("java", "Java");
        tagKeywords.put("javascript", "JavaScript");
        tagKeywords.put("typescript", "TypeScript");
        tagKeywords.put("vue", "Vue");
        tagKeywords.put("react", "React");
        tagKeywords.put("spring", "Spring");
        tagKeywords.put("springboot", "Spring Boot");
        tagKeywords.put("docker", "Docker");
        tagKeywords.put("kubernetes", "Kubernetes");
        tagKeywords.put("k8s", "Kubernetes");
        tagKeywords.put("数据库", "Database");
        tagKeywords.put("mysql", "Database");
        tagKeywords.put("redis", "Redis");
        tagKeywords.put("缓存", "Redis");
        tagKeywords.put("前端", "Frontend");
        tagKeywords.put("后端", "Backend");
        tagKeywords.put("全栈", "Fullstack");
        tagKeywords.put("架构", "Architecture");
        tagKeywords.put("微服务", "Microservice");
        tagKeywords.put("云", "Cloud");
        tagKeywords.put("aws", "Cloud");
        tagKeywords.put("阿里云", "Cloud");
        tagKeywords.put("kubernetes", "DevOps");
        tagKeywords.put("devops", "DevOps");
        tagKeywords.put("cicd", "DevOps");
        tagKeywords.put("机器学习", "Machine Learning");
        tagKeywords.put("深度学习", "Deep Learning");
        tagKeywords.put("神经网络", "Neural Network");
        tagKeywords.put("算法", "Algorithm");
        tagKeywords.put("优化", "Optimization");
        tagKeywords.put("性能", "Performance");
        tagKeywords.put("安全", "Security");
        tagKeywords.put("加密", "Security");
        tagKeywords.put("api", "API");
        tagKeywords.put("rest", "API");
        tagKeywords.put("grpc", "API");

        // 匹配分类
        Long matchedCategoryId = null;
        String matchedCategoryName = null;
        Set<String> matchedTagNames = new LinkedHashSet<>();
        List<String> reasons = new ArrayList<>();

        for (Map.Entry<String, List<Long>> entry : categoryKeywords.entrySet()) {
            if (lowerContent.contains(entry.getKey())) {
                for (Category cat : categories) {
                    if (entry.getValue().contains(cat.getId())) {
                        matchedCategoryId = cat.getId();
                        matchedCategoryName = cat.getName();
                        reasons.add("内容涉及「" + entry.getKey() + "」，匹配分类「" + cat.getName() + "」");
                        break;
                    }
                }
                break;
            }
        }

        // 如果没有匹配到具体分类，但内容涉及项目相关，也归到项目分享
        if (matchedCategoryId == null) {
            if (lowerContent.contains("项目") || lowerContent.contains("开源") ||
                lowerContent.contains("github") || lowerContent.contains("代码")) {
                for (Category cat : categories) {
                    if (cat.getId() == 2L) { // 项目分享
                        matchedCategoryId = cat.getId();
                        matchedCategoryName = cat.getName();
                        reasons.add("内容涉及代码/项目，归类到「项目分享」");
                        break;
                    }
                }
            }
        }

        // 默认使用第一个分类
        if (matchedCategoryId == null && !categories.isEmpty()) {
            Category defaultCat = categories.get(0);
            matchedCategoryId = defaultCat.getId();
            matchedCategoryName = defaultCat.getName();
            reasons.add("未识别到特定分类，使用默认分类「" + defaultCat.getName() + "」");
        }

        // 匹配标签
        Set<Long> matchedTagIds = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : tagKeywords.entrySet()) {
            if (lowerContent.contains(entry.getKey())) {
                for (Tag tag : tags) {
                    if (tag.getName().equalsIgnoreCase(entry.getValue())) {
                        matchedTagIds.add(tag.getId());
                        matchedTagNames.add(tag.getName());
                        reasons.add("识别到关键词「" + entry.getKey() + "」，推荐标签「" + tag.getName() + "」");
                    }
                }
            }
        }

        // 构建返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("categoryId", matchedCategoryId);
        result.put("categoryName", matchedCategoryName);
        result.put("tagIds", new ArrayList<>(matchedTagIds));
        result.put("tagNames", new ArrayList<>(matchedTagNames));
        result.put("reason", String.join("；", reasons));

        // 如果没有匹配到任何标签，给出通用建议
        if (matchedTagNames.isEmpty()) {
            result.put("suggestion", "建议手动添加一些相关标签");
        }

        return result;
    }

    /**
     * 获取工具定义列表（用于 AI 函数调用）
     */
    public List<Map<String, Object>> getToolDefinitions() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // get_categories 工具
        Map<String, Object> getCategoriesTool = new LinkedHashMap<>();
        getCategoriesTool.put("name", "get_categories");
        getCategoriesTool.put("description", "获取所有可用的文章分类列表。返回分类ID和名称，用于将文章分配到合适的分类。");
        getCategoriesTool.put("parameters", Map.of(
            "type", "object",
            "properties", Map.of(),
            "required", List.of()
        ));
        tools.add(getCategoriesTool);

        // get_tags 工具
        Map<String, Object> getTagsTool = new LinkedHashMap<>();
        getTagsTool.put("name", "get_tags");
        getTagsTool.put("description", "获取所有可用的文章标签列表。返回标签ID和名称，用于给文章打上相关标签。");
        getTagsTool.put("parameters", Map.of(
            "type", "object",
            "properties", Map.of(),
            "required", List.of()
        ));
        tools.add(getTagsTool);

        // analyze_and_recommend 工具
        Map<String, Object> analyzeTool = new LinkedHashMap<>();
        analyzeTool.put("name", "analyze_and_recommend");
        analyzeTool.put("description", "分析文章内容，智能推荐最合适的分类和标签。根据文章的主题、技术栈、关键词进行匹配。");
        Map<String, Object> analyzeParams = new LinkedHashMap<>();
        analyzeParams.put("type", "object");
        analyzeParams.put("properties", Map.of(
            "content", Map.of(
                "type", "string",
                "description", "文章的完整内容，包括标题和正文"
            )
        ));
        analyzeParams.put("required", List.of("content"));
        analyzeTool.put("parameters", analyzeParams);
        tools.add(analyzeTool);

        return tools;
    }

    /**
     * 调用指定的工具
     */
    public Object callTool(String toolName, Map<String, Object> arguments) {
        switch (toolName) {
            case "get_categories":
                return getCategories();
            case "get_tags":
                return getTags();
            case "analyze_and_recommend":
                String content = arguments.containsKey("content")
                    ? String.valueOf(arguments.get("content"))
                    : "";
                return analyzeAndRecommend(content);
            default:
                return Map.of("error", "Unknown tool: " + toolName);
        }
    }
}
