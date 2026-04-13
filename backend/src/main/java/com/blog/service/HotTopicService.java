package com.blog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class HotTopicService {

    private static final Logger log = LoggerFactory.getLogger(HotTopicService.class);

    private static final String MCP_SERVER = "http://localhost:3333";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Block serious violations only - allow all other topics
    private static final Set<String> BLOCK_KEYWORDS = Set.of(
            "色情", "黄网", "赌博", "毒品", "制毒", "枪支", "博彩", "裸聊"
    );

    public List<String> getAllHotTopics() {
        // Try MCP Server first
        try {
            List<String> topics = getFromMcpServer(30);
            if (!topics.isEmpty()) {
                log.info("[HotTopic] MCP Server returned {} topics", topics.size());
                return topics;
            }
        } catch (Exception e) {
            log.warn("[HotTopic] MCP Server failed, fallback to direct API: {}", e.getMessage());
        }

        // Fallback: direct newsnow API
        return getFromNewsnowApi();
    }

    /**
     * 按平台获取热点话题
     * @param platform 平台名（weibo, zhihu, baidu, bilibili, douyin 等）
     * @return 该平台的热点话题列表
     */
    public List<String> getTopicsByPlatform(String platform) {
        try {
            String apiUrl = "https://newsnow.busiyi.world/api/s?id=" + platform + "&latest";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode items = root.get("items");
            if (items == null || !items.isArray()) {
                return List.of();
            }

            List<String> topics = new ArrayList<>();
            for (JsonNode item : items) {
                String title = extractTitle(item);
                if (title != null && title.length() > 4 && !isBlocked(title)) {
                    topics.add(title);
                }
            }
            return topics;
        } catch (Exception e) {
            log.debug("[HotTopic] platform {} failed: {}", platform, e.getMessage());
            return List.of();
        }
    }

    /**
     * 获取支持的平台列表
     */
    public List<Map<String, String>> getAvailablePlatforms() {
        return List.of(
                Map.of("id", "weibo", "label", "微博", "icon", "📮"),
                Map.of("id", "zhihu", "label", "知乎", "icon", "💬"),
                Map.of("id", "baidu", "label", "百度", "icon", "🔍"),
                Map.of("id", "bilibili", "label", "Bilibili", "icon", "📺"),
                Map.of("id", "douyin", "label", "抖音", "icon", "🎵"),
                Map.of("id", "wallstreetcn", "label", "财经", "icon", "📈"),
                Map.of("id", "toutiao", "label", "头条", "icon", "📰"),
                Map.of("id", "thepaper", "label", "澎湃", "icon", "🗞️")
        );
    }

    private List<String> getFromMcpServer(int limit) throws Exception {
        String url = MCP_SERVER + "/topics?top_n=" + limit;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("MCP Server returned " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode topicsNode = root.get("topics");
        if (topicsNode == null || !topicsNode.isArray()) {
            return List.of();
        }

        List<String> topics = new ArrayList<>();
        for (JsonNode n : topicsNode) {
            topics.add(n.asText());
        }
        return topics;
    }

    private List<String> getFromNewsnowApi() {
        String[] platforms = {
                "baidu", "zhihu", "weibo", "bilibili", "douyin",
                "wallstreetcn-hot", "tieba", "cailian", "thepaper", "toutiao"
        };

        List<String> allTopics = new ArrayList<>();

        for (String platform : platforms) {
            try {
                String apiUrl = "https://newsnow.busiyi.world/api/s?id=" + platform + "&latest";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(response.body());
                    JsonNode items = root.get("items");
                    if (items != null && items.isArray()) {
                        for (JsonNode item : items) {
                            String title = extractTitle(item);
                            if (title != null && title.length() > 4 && !isBlocked(title)) {
                                allTopics.add(title);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("[HotTopic] platform {} failed: {}", platform, e.getMessage());
            }
        }

        if (allTopics.isEmpty()) {
            return getDefaultTopics();
        }

        // Deduplicate, preserve order, max 50
        Set<String> seen = new LinkedHashSet<>();
        for (String t : allTopics) {
            seen.add(t);
            if (seen.size() >= 50) break;
        }

        log.info("[HotTopic] newsnow API returned {} topics (deduped)", seen.size());
        return new ArrayList<>(seen);
    }

    private String extractTitle(JsonNode item) {
        if (item.has("title")) {
            String title = item.get("title").asText();
            if (title != null && !title.isBlank()) {
                return decodeUrl(title);
            }
        }
        return null;
    }

    private String decodeUrl(String encoded) {
        try {
            String decoded = java.net.URLDecoder.decode(encoded, java.nio.charset.StandardCharsets.UTF_8);
            // newsnow API returns ROT13-encoded Chinese text, decode it
            return rot13(decoded);
        } catch (Exception e) {
            return encoded;
        }
    }

    private String rot13(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                sb.append((char) ('a' + (c - 'a' + 13) % 26));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append((char) ('A' + (c - 'A' + 13) % 26));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private boolean isBlocked(String title) {
        if (title == null) return true;
        String lower = title.toLowerCase();
        for (String kw : BLOCK_KEYWORDS) {
            if (lower.contains(kw)) return true;
        }
        return false;
    }

    public List<String> selectBestTopic(List<String> topics) {
        if (topics == null || topics.isEmpty()) return getDefaultTopics();
        return topics.subList(0, Math.min(10, topics.size()));
    }

    private List<String> getDefaultTopics() {
        return List.of(
                "Hot Search: Todays Top Trending Events",
                "Breaking News: Most Discussed Topics Online",
                "Weibo Hot Search Rankings",
                "Zhihu Hot Questions",
                "Bilibili Trending Videos",
                "Baidu Hot Search List",
                "Toutiao Hot News",
                "Douyin Trending Topics",
                "Wallstreetcn Hot Topics",
                "Cailian Press Hot News"
        );
    }
}
