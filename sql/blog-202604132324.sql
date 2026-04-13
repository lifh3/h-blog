-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: localhost    Database: blog
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_settings`
--

DROP TABLE IF EXISTS `ai_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ai_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(100) NOT NULL,
  `setting_value` text,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `setting_key` (`setting_key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_settings`
--

LOCK TABLES `ai_settings` WRITE;
/*!40000 ALTER TABLE `ai_settings` DISABLE KEYS */;
INSERT INTO `ai_settings` VALUES (1,'ai.auto_publish','false','AI生成文章后是否自动发布','2026-04-13 20:54:41','2026-04-13 20:54:41'),(2,'ai.default_category_id','','AI生成文章的默认分类ID','2026-04-13 20:54:41','2026-04-13 20:54:41'),(3,'scheduler.enabled','true','是否启用定时任务','2026-04-13 20:54:41','2026-04-13 20:54:41'),(4,'scheduler.cron','0 0 9 * * ?','定时任务cron表达式','2026-04-13 20:54:41','2026-04-13 20:54:41'),(5,'scheduler.auto_publish','false','定时任务生成文章后是否自动发布','2026-04-13 20:54:41','2026-04-13 20:54:41'),(6,'scheduler.category_id','','定时任务生成文章的默认分类ID','2026-04-13 20:54:41','2026-04-13 20:54:41');
/*!40000 ALTER TABLE `ai_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `summary` varchar(500) DEFAULT NULL,
  `content` longtext,
  `cover_image` varchar(255) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `collection_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `view_count` int(11) DEFAULT '0',
  `status` tinyint(4) DEFAULT '1' COMMENT '1=???,0=???',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL,
  CONSTRAINT `article_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (2,'为什么AI助手无法实时联网搜索？深入解析大语言模型的工作原理','在日常使用AI助手时，你可能会遇到这样的尴尬时刻：询问最近发生的新闻事件或查询当天的股票价格时，AI会礼貌地告诉你它没有实时获取信息的能力。这个看似简单的技术限制，背后其实蕴含着大语言模型（LLM）深厚的设计原理和架构特点。今天我们就来深入探讨一下这个话题，理解AI助手的工作机制以及它为什么在某些方面显得“滞后”。 要理解为什么AI助手无法实时联网，我们首先需要了解大语言模型的基本工作原理。现代A...','## 为什么AI助手无法实时联网搜索？深入解析大语言模型的工作原理\n\n在日常使用AI助手时，你可能会遇到这样的尴尬时刻：询问最近发生的新闻事件或查询当天的股票价格时，AI会礼貌地告诉你它没有实时获取信息的能力。这个看似简单的技术限制，背后其实蕴含着大语言模型（LLM）深厚的设计原理和架构特点。今天我们就来深入探讨一下这个话题，理解AI助手的工作机制以及它为什么在某些方面显得“滞后”。\n\n### 大语言模型的核心工作原理\n\n要理解为什么AI助手无法实时联网，我们首先需要了解大语言模型的基本工作原理。现代AI助手如ChatGPT、Claude等都是基于Transformer架构的深度神经网络模型。这些模型通过在海量文本数据上进行“预训练”来学习语言的规律、知识和推理能力。\n\n在预训练阶段，模型会处理数十亿甚至数万亿的文本tokens，通过预测下一个词的方式来学习语言的统计规律。这个过程使得模型能够理解语法结构、语义关系、世界知识甚至一些推理能力。但关键在于，模型的“知识”是在预训练阶段就被编码到模型参数中的，而不是在推理时实时获取的。这就像一个人通过多年的学习将知识存储在大脑中，而不是每次需要答案时临时去查阅资料。\n\n### 知识截止日期的本质含义\n\n当你看到AI助手说“我的知识截止到XXX日期”时，这并不是一个简单的营销话术，而是大语言模型架构的根本性限制。模型在完成预训练后，其参数就固定了，训练数据中包含的信息就成为模型“知道”的全部内容。模型不会自动学习新知识，也不会实时更新自己的认知。\n\n这种设计有其深层次的技术原因。首先，完整的预训练过程需要消耗巨大的计算资源，通常需要数百到数千个GPU运行数周甚至数月。其次，模型的参数更新是一个复杂的过程贸然添加新数据可能导致灾难性遗忘，破坏模型已有的能力平衡。因此，大语言模型通常采用“快照式”学习模式——在某个时间点之前的知识被固化在模型参数中。\n\n### 当前的技术解决方案\n\n虽然大语言模型本身无法实时联网，但工程师们已经开发出多种技术方案来弥补这一缺陷，其中最流行的就是检索增强生成（Retrieval-Augmented Generation，RAG）技术。\n\nRAG的工作原理是将外部知识库与大语言模型相结合。当用户提出问题时，系统首先从知识库中检索相关信息，然后将这些信息作为上下文提供给模型，帮助其生成更准确、更新的回答。这种架构既保留了语言模型强大的推理能力，又能利用外部数据库的实时信息。实际应用中，很多企业级AI助手都采用RAG架构来访问最新的文档、数据库或网络搜索结果。\n\n另一种解决方案是模型微调（Fine-tuning）或多阶段训练。开发者可以在基础模型上进行持续训练，将新数据不断融入模型。但这种方法成本高昂，且存在训练不稳定、可能损害原有能力的风险，因此通常只在特定场景下使用。\n\n### 实际应用中的权衡与思考\n\n理解AI助手的这一特性对于我们合理使用这项技术至关重要。在需要最新信息的场景中，比如查询新闻、股票价格、天气情况等，我们应该直接使用专门的搜索引擎或应用程序，而不是依赖AI助手。AI助手真正的强项在于理解复杂问题、进行逻辑推理、生成创意内容、解释技术概念等需要语言理解和生成的场景。\n\n从技术发展的角度来看，完全实时学习的AI系统目前仍面临诸多挑战，包括数据质量控制、隐私保护、计算资源消耗等问题。但随着技术的演进，我们有理由相信未来的AI助手将能够更好地平衡“知识储备”与“实时学习”之间的关系，为用户提供更智能、更及时的服务。\n\n理解技术背后的原理，不仅能帮助我们更好地使用工具，也能让我们对AI技术保持理性的期待。大语言模型的这一“时间胶囊”特性，既是当前技术的局限，也为我们思考人工智能的未来发展提供了有趣的视角。','',1,1,1,28,1,'2026-04-13 20:46:57','2026-04-13 21:30:31'),(19,'Hot Search：今日热门搜索事件追踪系统的技术实现与架构设计','在当今信息爆炸的时代，热门搜索（Hot Search）和趋势事件追踪已成为社交媒体、新闻平台、电商网站等各类互联网应用的核心功能模块。一个设计良好的热搜索系统不仅需要实时地捕捉和分析海量数据，还需要在毫秒级别内响应用户的查询请求。本文将从技术架构、核心算法、实时处理和性能优化等多个维度，深入探讨如何构建一个高效、可靠的热搜索系统。 热搜索系统的本质是一个实时排行系统，其核心目标是将海量的用户行为数...','## Hot Search：今日热门搜索事件追踪系统的技术实现与架构设计\n\n在当今信息爆炸的时代，热门搜索（Hot Search）和趋势事件追踪已成为社交媒体、新闻平台、电商网站等各类互联网应用的核心功能模块。一个设计良好的热搜索系统不仅需要实时地捕捉和分析海量数据，还需要在毫秒级别内响应用户的查询请求。本文将从技术架构、核心算法、实时处理和性能优化等多个维度，深入探讨如何构建一个高效、可靠的热搜索系统。\n\n### 一、热搜索系统的核心需求与技术挑战\n\n热搜索系统的本质是一个实时排行系统，其核心目标是将海量的用户行为数据（如搜索、点击、转发、评论等）进行聚合分析，最终以排行榜的形式展示给用户。从技术角度来看，这套系统面临着三个主要挑战：首先是数据量的挑战，现代社交平台每秒可能产生数十万次搜索请求；其次是实时性的要求，用户期望看到的内容能够反映当前时刻的真实热度；第三是准确性的考量，热度计算需要能够识别和抑制刷量行为，确保排行结果的公正性。\n\n在传统的单体架构中，我们通常会使用关系型数据库来存储和查询热度数据。然而这种方式在面对高并发场景时会遇到严重的性能瓶颈——数据库的读写锁竞争会导致响应延迟急剧增加。因此，我们需要引入分布式架构和缓存层来构建一套能够支撑千万级QPS的热搜索系统。\n\n### 二、系统架构设计：从数据采集到结果展示\n\n一个完整的热搜索系统通常采用分层架构设计。数据采集层负责收集用户的搜索行为数据，这些数据通过消息队列（如Kafka或RocketMQ）进行异步传输，以解耦数据生产者和消费者。数据处理层是整个系统的核心，它从消息队列中消费数据，进行实时的聚合计算，并将结果写入高性能的缓存系统（如Redis集群）。接口服务层则负责响应客户端的查询请求，从缓存中读取热度排行数据，并进行适当的聚合、过滤和格式化处理。\n\n在数据存储方面，我们通常采用多级缓存策略。Redis集群作为主要的缓存层，存储按时间窗口聚合的热度数据；而持久化的时序数据库（如InfluxDB或ClickHouse）则用于存储历史热度数据，支持后续的数据分析和挖掘。每条热度记录应当包含搜索关键词、热度分值、时间戳、分类标签等关键字段，以便支持多维度的查询和筛选。\n\n### 三、热度计算算法：指数平滑与时间衰减\n\n热度值的计算是热搜索系统的核心技术环节。一个经典的计算公式会综合考虑多个因子：搜索次数、点击率、转发数、评论数以及时间因素。对于不同类型的平台，各因子的权重配置会有所差异——新闻类平台更注重时效性，而电商平台则更看重转化率。\n\n时间衰减是热度计算中不可忽视的重要因素。常用的衰减模型包括指数衰减和线性衰减。以指数衰减为例，假设初始热度为H，衰减系数为λ，则经过时间t后的热度值为H×e^(-λt)。这种模型能够确保新发生的事件快速上升，而长期没有新动态的事件则逐渐淡出排行榜。衰减系数λ的选择需要根据业务场景进行调优——过大则排行变化过于频繁，过小则无法反映最新动态。\n\n```python\nclass HeatCalculator:\n    def __init__(self, decay_factor=0.95):\n        self.decay_factor = decay_factor\n    \n    def calculate_heat(self, events: list, current_time: int) -> float:\n        \"\"\"\n        计算关键词的热度值\n        events: [(weight, timestamp), ...]\n        \"\"\"\n        total_heat = 0.0\n        for weight, timestamp in events:\n            time_diff = current_time - timestamp\n            decay = pow(self.decay_factor, time_diff / 3600)  # 按小时衰减\n            total_heat += weight * decay\n        return total_heat\n```\n\n### 四、实时流处理：Apache Flink与Kafka的深度整合\n\n在大规模场景下，批处理模式已经无法满足实时性的要求。我们需要引入流处理框架来实现毫秒级的热度更新。Apache Flink是当前最主流的选择，它提供了精确一次（Exactly-Once）的语义保证，以及强大的时间窗口和状态管理能力。\n\n一个典型的实时热度计算流程如下：首先，搜索事件从Kafka的原始Topic消费进入Flink流处理作业；然后，通过滚动时间窗口（如5分钟窗口）对搜索关键词进行聚合，统计窗口内的搜索次数、点击次数等指标；接着，使用窗口末尾的时间戳作为事件时间，应用热度计算公式得到窗口级别的热度增量；最后，将增量数据写入Redis的Sorted Set中，通过ZINCRBY命令原子性地增加关键词的热度分值。\n\n```java\npublic class HeatAggregationJob {\n    public static void main(String[] args) throws Exception {\n        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();\n        env.enableCheckpointing(10000);\n        \n        DataStream<SearchEvent> stream = env\n            .addSource(new KafkaSource<>(\"search-events\", new SearchEventDeserializer()))\n            .keyBy(SearchEvent::getKeyword)\n            .timeWindow(Time.minutes(5))\n            .reduce((a, b) -> new SearchEvent(\n                a.getKeyword(),\n                a.getCount() + b.getCount(),\n                a.getClicks() + b.getClicks(),\n                Math.max(a.getTimestamp(), b.getTimestamp())\n            ));\n        \n        stream.addSink(new RedisHeatSink());\n        env.execute(\"Heat Aggregation Job\");\n    }\n}\n```\n\n### 五、性能优化与高可用保障\n\n在生产环境中，热搜索系统需要应对各种异常情况和性能挑战。降级策略是保障系统可用性的重要手段——当Redis集群出现故障时，系统可以切换到本地缓存模式，返回上一版本的排行结果，确保服务不中断。热点Key问题是另一个需要关注的性能瓶颈，可以通过Key的哈希打散和本地缓存来分散请求压力。\n\n数据倾斜是分布式计算中的常见问题。如果某个热点事件（如明星离婚、重大赛事）产生的数据量远超其他事件，会导致某些Flink TaskManager负载过高。此时可以采用两阶段聚合策略：首先在本地进行预聚合，然后发送到下游算子进行全局聚合。读写分离也是常用的优化手段——写入操作分散到多个Redis节点，而读取操作则通过统一的代理层进行负载均衡。\n\n### 六、总结与展望\n\n热搜索系统的设计与实现涉及分布式架构、实时计算、数据存储、算法优化等多个技术领域的交叉融合。一个优秀的系统不仅需要在技术上做到高并发、低延迟、强一致，还需要在业务层面做到公平公正、抗刷防作弊。随着深度学习技术的发展，未来热搜索系统可能会引入语义理解和用户画像等更智能的算法，为用户提供更加个性化和精准的热门内容推荐。\n\n从工程实践的角度来看，建议采用渐进式的建设策略：首先实现基于规则的简单版本快速上线验证业务价值，然后在业务增长过程中逐步引入更复杂的技术方案。同时，建立完善的监控告警体系和AB测试机制，持续优化系统的各项指标，最终打造一个既能满足当前业务需求，又能适应未来发展的热搜索平台。','',1,NULL,1,3,1,'2026-04-13 20:46:57','2026-04-13 21:32:50'),(20,'一周热点速览：当科技创新遇上国际博弈与社会民生','在这个信息爆炸的时代，每一天都有无数新闻事件争相抢占我们的注意力。本周的国际舞台热闹非凡，科技领域同样精彩不断，而社会民生议题也持续牵动人心。让我带你梳理这一周最值得关注的话题，挖掘新闻背后的深层逻辑与趋势走向。 西班牙首相桑切斯的中国之行引发了广泛关注。在清华大学参观期间，他与中方代表热情握手，以至于“握不过来了”——这个小细节生动展现了中国在欧洲外交中的受欢迎程度。而更引人注目的是，他在小米总...','## 一周热点速览：当科技创新遇上国际博弈与社会民生\n\n在这个信息爆炸的时代，每一天都有无数新闻事件争相抢占我们的注意力。本周的国际舞台热闹非凡，科技领域同样精彩不断，而社会民生议题也持续牵动人心。让我带你梳理这一周最值得关注的话题，挖掘新闻背后的深层逻辑与趋势走向。\n\n### 西班牙访华：科技外交的别样风景\n\n西班牙首相桑切斯的中国之行引发了广泛关注。在清华大学参观期间，他与中方代表热情握手，以至于“握不过来了”——这个小细节生动展现了中国在欧洲外交中的受欢迎程度。而更引人注目的是，他在小米总部亲自试坐了小米汽车SU7，小米创始人雷军全程陪同。\n\n这一场景的意义远超表面所见。在全球汽车产业加速向电动化转型的背景下，中国新能源车企的技术实力和产品竞争力正在获得国际认可。西班牙首相的这次“体验式外交”，既是两国经贸合作的具体体现，也为中国车企出海欧洲市场做了一次无声的“代言”。可以预见，随着中国新能源技术的持续突破，类似的高端访问活动将成为展示“中国智造”的重要窗口。\n\n与此同时，西班牙首相在公开场合明确表示：“世界离不开中国。”这番表态在当前复杂的国际形势下显得格外意味深长。它反映出，尽管个别国家试图推动“脱钩断链”，但理性的国际社会已经认识到，与中国合作才是符合各方利益的正确选择。\n\n### 中伊关系：谣言与真相的较量\n\n本周外交部记者会上，针对“向伊朗提供武器”的传闻，中方予以明确否认。而在稍早前，伊朗曾准备稀释450公斤浓缩铀表达善意，这一细节揭示了伊核问题的微妙复杂性。\n\n从伊朗的角度看，浓缩铀的稀释是一种善意的表达，但同时也折射出其核计划面临的国际压力。特朗普政府时期退出伊核协议的决定，使得伊朗核问题再度成为国际焦点。如今，特朗普再次执政后扬言要封锁伊朗港口，更是将地区局势推向紧张。\n\n在这一棋局中，中国始终坚持在伊核问题上发挥建设性作用。中国的立场很明确：反对核武器扩散，支持通过对话协商解决分歧。这不仅体现了中国作为负责任大国的担当，也契合了维护中东地区和平稳定的实际需要。毕竟，中东局势的任何恶化，都会冲击全球能源供应链，其影响将波及每一个经济体。\n\n### 巴西比亚迪风波：投资环境的警讯\n\n中国电动汽车巨头比亚迪在巴西遭遇的波折，折射出发展中国家在引进外资过程中面临的矛盾心理。巴西将比亚迪移出相关名单的消息，引发了各方对投资环境的担忧。\n\n从表面看，这可能涉及当地的政策调整或利益博弈。但深层次分析，这反映出一些国家在追求技术进步与保护本土产业之间的两难选择。比亚迪作为全球新能源汽车的领军企业，其在海外的每一步投资都备受关注。在巴西遭遇的阻碍，或许能为中国企业出海提供更多启示：不仅要有过硬的技术和产品，还需要更深入地了解当地的政治、经济、文化环境。\n\n### 科技前沿：折叠屏与量子计算的双重冲击\n\n本周最受关注的科技新闻当属华为横向阔折叠手机的曝光。继Mate X系列之后，华为再次在折叠屏领域迈出新步伐。横向阔折叠的设计意味着更大的屏幕展开面积，更接近平板电脑的使用体验，这或将成为折叠屏手机发展的新方向。\n\n量子计算领域同样传来重磅消息。所谓的“量子末日倒计时”引发热议，比特币等加密货币可能被破解的担忧并非空穴来风。虽然目前量子计算机的实际算力距离威胁现有加密体系还有相当距离，但未雨绸缪已是业界共识。加密货币社区已开始探索“量子安全”的新型加密方案，这场技术军备竞赛才刚刚开始。\n\n### 社会民生：每一个小故事背后都是大民生\n\n本周的社会新闻同样精彩。南京再现连夜排队抢房现象，让人看到楼市冷暖的微妙变化；醉酒叫代驾后车内身亡、家属索赔30万的案例，再次敲响了生命安全的警钟；长春姐弟俩被托管班遗忘两年的事件，则暴露了儿童看护领域的监管漏洞。\n\n食品安全方面，“洗生肉不要直接用水冲”的提醒引发了广泛讨论。这一看似简单的生活常识背后，涉及到交叉污染的科学问题，值得每个家庭重视。\n\n更令人揪心的是那些关于儿童的新闻。3岁男童每晚补营养却体重仅21斤，反映出科学育儿知识的普及仍然任重道远；被绳索锁喉的男孩及其家庭的遭遇，则让人看到社会救助机制完善的重要性。\n\n### 娱乐与商业：跨界的风口\n\n演员文章在上海开面馆的消息成为娱乐版头条。从演员到餐饮老板的转型，既是明星变现的一种方式，也暗含了演艺圈竞争的残酷。而梦龙雪糕广告被指低俗的事件，则提醒企业：营销可以有创意，但不能无底线。\n\n宿迁官宣何润东“霸王归来”，让这位台湾演员再次进入公众视野。在两岸文化交流日益频繁的今天，类似的活动不仅具有商业价值，更承载着文化认同的意义。\n\n### 结语：变化中的世界，聚焦中的我们\n\n一周的时间，几十条新闻，从国际博弈到科技创新，从经济民生到文化娱乐，共同编织出这个时代复杂而精彩的图景。每一则新闻背后，都有人在行动，有趋势在演进，有问题在浮现。\n\n作为信息的接收者和时代的见证者，我们能做的，是在纷繁复杂的信息洪流中保持清醒，在围观热闹的同时思考本质。毕竟，新闻不仅是茶余饭后的谈资，更是理解这个世界的窗口。\n\n下一周，又会有怎样的故事上演？让我们拭目以待。','',1,NULL,1,0,1,'2026-04-13 20:46:57','2026-04-13 20:46:57'),(21,'TechBlog 技术博客系统：从零到一的全栈项目实战总结','TechBlog 是一个采用前后端分离架构的现代化技术博客系统，由笔者独立开发完成。该项目最大的特点在于其独特的设计风格——赛博朋克（Cyberpunk）主题，整个界面采用了深色系配色方案，搭配霓虹蓝（Neon Blue）、霓虹紫（Neon Purple）等高对比度色彩，营造出极具科技感的视觉体验。项目分为三大核心模块：前台博客展示端、管理后台以及后端 API 服务，三者通过 RESTful AP...','## TechBlog 技术博客系统：从零到一的全栈项目实战总结\n\n### 一、项目概述与背景\n\nTechBlog 是一个采用前后端分离架构的现代化技术博客系统，由笔者独立开发完成。该项目最大的特点在于其独特的设计风格——赛博朋克（Cyberpunk）主题，整个界面采用了深色系配色方案，搭配霓虹蓝（Neon Blue）、霓虹紫（Neon Purple）等高对比度色彩，营造出极具科技感的视觉体验。项目分为三大核心模块：前台博客展示端、管理后台以及后端 API 服务，三者通过 RESTful API 进行解耦通信，体现了当今互联网应用开发的主流架构思想。\n\n从项目定位来看，TechBlog 不仅是一个简单的文章发布平台，更是一个功能完备的内容管理系统（CMS）。它支持文章的分类、标签、合集等多种组织形式，同时内置了评论系统、友链管理等社区功能，能够满足个人技术博主从写作到发布的全流程需求。值得一提的是，项目采用了 Markdown 作为文章的编写格式，配合前端实时渲染能力，为技术写作者提供了极佳的创作体验。\n\n### 二、技术架构深度剖析\n\n#### 2.1 前端技术选型\n\n前端采用了 Vue 3 作为核心框架，搭配 Vite 作为构建工具，这套组合在当今前端开发领域已经成为了事实标准。Vue 3 的 Composition API 为组件逻辑的复用提供了优雅的解决方案，而 Vite 凭借其基于 ESM 的即时热更新能力，大幅提升了开发效率。在状态管理方面，项目选用了 Pinia 作为全局状态管理库，相比 Vuex 更加轻量且对 TypeScript 支持更好。\n\n样式处理方面，项目采用了 TailwindCSS 原子化 CSS 框架。通过自定义配置，项目构建了一套完整的赛博朋克风格设计系统。在 `tailwind.config.js` 中，我们可以看到作者定义的丰富配色方案，包括 `cyber-dark`、`neon-blue`、`neon-purple` 等十余种专有色值，以及 `Orbitron` 和 `JetBrains Mono` 两种特色字体。这些配置不仅确保了视觉风格的一致性，更为后续的界面扩展奠定了基础。\n\n前端路由采用 Vue Router 4 实现，路由配置清晰地将前台页面和管理后台进行了分离。值得注意的是，项目实现了路由守卫（Navigation Guard）机制，当用户访问管理后台时，如果未通过身份认证，将自动重定向至登录页面，这种设计有效保障了后台系统的安全性。\n\n#### 2.2 后端技术架构\n\n后端采用 Spring Boot 4 构建 Web 服务，作为 Java 生态中最成熟的企业级框架，Spring Boot 为项目提供了稳定可靠的基础设施。数据访问层使用了 MyBatis Plus 框架，相比原生 MyBatis，MyBatis Plus 提供了更为便捷的 CRUD 操作和强大的条件构造器能力，大幅减少了样板代码的编写。\n\n数据库选用 MySQL 8，项目设计了八张核心数据表来支撑业务运转。`user` 表存储用户信息，`article` 表管理文章内容，`category` 和 `tag` 表分别对应分类和标签体系，`article_tag` 作为多对多关联表实现了文章与标签的灵活关联。`comment` 表支持评论功能，`collection` 和 `article_collection` 则实现了合集这一特色功能。这种表结构设计遵循了数据库设计范式，既保证了数据的完整性，又为后续功能扩展预留了空间。\n\n#### 2.3 认证与安全\n\n系统采用 JWT（JSON Web Token）实现无状态认证，配合 BCrypt 算法对密码进行哈希存储。用户登录成功后，后端生成包含用户信息的 Token，客户端将其存储在 localStorage 中，后续请求通过请求头携带 Token 完成身份验证。这种设计使得服务具有良好的横向扩展能力，适合部署在容器化环境中。\n\n### 三、项目开发流程总结\n\n#### 3.1 需求分析与项目规划\n\n项目启动之初，首先进行了详细的需求分析。考虑到目标用户群体为技术博主，我们确定了以下核心需求：简洁高效的写作体验、炫酷的视觉呈现、完善的内容管理能力、稳定的后端服务。基于这些需求，我们完成了技术选型，并制定了详细的开发计划。整个项目被划分为三个主要阶段：后端 API 开发、前台界面实现、管理后台开发，每个阶段都有明确的里程碑和交付物。\n\n#### 3.2 数据库设计与表结构创建\n\n根据业务需求，我们设计了完整的数据模型。以文章表为例，除了存储标题、内容、摘要等基本信息外，还包含了 `coverImage` 封面图字段、`viewCount` 浏览量统计字段、`status` 发布状态字段等业务相关属性。关联表的设计同样经过深思熟虑，`article_tag` 表采用复合主键设计，确保了数据的一致性约束。\n\n#### 3.3 后端 API 开发\n\n后端开发遵循 RESTful 设计规范，API 路径清晰表达了资源的层级关系。例如 `/api/articles` 代表文章集合，`/api/articles/{id}` 代表具体文章，HTTP 方法（GET、POST、PUT、DELETE）则表达了操作类型。项目中实现了完善的异常处理机制，通过全局异常处理器统一捕获并响应错误信息，提升了 API 的健壮性。\n\n#### 3.4 前台界面开发\n\n前台开发是项目中最具挑战性的部分，需要在保证功能完备的同时实现独特的视觉效果。作者在 TailwindCSS 基础上构建了一套自定义组件系统，包括 `glass-card` 毛玻璃卡片、`btn-cyber` 霓虹按钮、`tag-cyber` 赛博标签等。这些组件在保持一致性的同时，又各具特色，很好地诠释了赛博朋克美学。\n\n特别值得介绍的是首页的 Hero 区域，作者使用 Canvas API 实现了一个粒子动画效果。动画中包含多个具有位置、速度、透明度属性的粒子对象，粒子之间会形成连线，营造出科技感十足的视觉效果。这种纯前端实现的方式，不仅加载速度快，而且可以根据需要灵活调整粒子数量和样式。\n\n#### 3.5 Markdown 编辑器实现\n\n文章编辑是管理后台的核心功能之一。项目实现了一个功能完备的 Markdown 编辑器，支持标题、加粗、斜体、代码块、列表等常用格式的快捷插入。编辑器采用双栏布局，左侧为编辑区域，右侧为实时预览区域，通过 `markdown-it` 库实现 Markdown 到 HTML 的转换，配合 `highlight.js` 实现代码高亮，为技术文章的编写提供了专业的支持。\n\n### 四、技术亮点与创新点\n\n#### 4.1 赛博朋克视觉风格\n\n通过精心设计的配色方案和独特的 UI 组件，项目成功塑造了一个辨识度极高的赛博朋克风格博客。深色背景配合霓虹色点缀，营造出未来科技感；毛玻璃效果（Glassmorphism）的广泛应用增加了界面的层次感；动态渐变和发光效果则为静态页面注入了活力。这套视觉系统在保证美观的同时，也兼顾了可访问性和性能表现。\n\n#### 4.2 组件化架构设计\n\n项目在前端实现了高度的组件化，将界面拆分为 `ArticleCard`、`ArticleList`、`Sidebar`、`TagCloud`、`TechNavbar` 等多个可复用组件。每个组件职责单一，通过 Props 接收数据，通过 Emit 事件与父组件通信。这种设计不仅提高了代码的可维护性，更方便了功能的扩展和测试。\n\n#### 4.3 响应式布局适配\n\n借助 TailwindCSS 的响应式工具类，项目实现了良好的跨设备适配。从手机到桌面端，导航栏自动切换为汉堡菜单或完整导航，侧边栏在移动端会被隐藏或调整位置，文章卡片也会根据屏幕宽度调整列数。这种一站式适配策略确保了用户在不同设备上都能获得良好的浏览体验。\n\n### 五、部署与运维建议\n\n项目提供了详细的启动指南，但要在生产环境中部署，还需要考虑几个关键问题。首先是环境配置，MySQL 数据库需要提前创建 `blog` 数据库，并执行初始化脚本；后端需要配置数据库连接信息，建议使用环境变量而非硬编码。其次是反向代理配置，生产环境通常使用 Nginx 作为前端静态资源的服务器，同时将 API 请求代理到后端服务。\n\n对于安全性强化，建议在生产环境中启用 HTTPS 协议，配置 JWT Token 的过期时间和刷新机制，对敏感操作添加操作日志记录。性能方面，可以引入 Redis 缓存热门文章和统计数据，使用 CDN 加速静态资源分发，数据库层面则可以通过添加索引来优化查询性能。\n\n### 六、总结与展望\n\nTechBlog 项目完整地呈现了一个全栈项目的开发全貌，从需求分析、架构设计，到编码实现、测试部署，每一个环节都凝聚了开发者的思考和实践。项目采用的主流技术栈组合——Vue 3 + Spring Boot + MySQL——经过了大量生产项目的验证，具有良好的稳定性和社区支持。同时，项目在视觉设计上的创新尝试，展示了前端美学与功能实现的完美融合。\n\n对于希望学习全栈开发的读者，这个项目提供了极佳的参考价值。你可以从中学到前后端分离架构的设计思路、RESTful API 的最佳实践、现代化前端的组件化开发模式，以及数据库设计的规范化方法。未来，项目还可以进一步扩展，如添加深色/浅色主题切换、SEO 优化、多语言支持等功能，向着更完善的技术博客平台演进。','',2,NULL,1,0,1,'2026-04-13 20:46:57','2026-04-13 20:46:57'),(22,'热点纵览：当这些新闻串联在一起，我们看到了一个怎样的时代？','近期热点新闻如潮水般涌来，从国际外交到科技应用，从社会民生到文化娱乐，看似杂乱无章的信息背后，实则隐藏着中国社会发展的深层脉络。本文将尝试将这些碎片化的新闻串联起来，勾勒出一幅当代中国的生动画卷。 最近的外交舞台上，中国展现出了前所未有的从容与自信。西班牙首相访华期间，在清华大学的握手场景被网友调侃“握不过来了”，而试坐小米SU7时雷军陪同的画面更是刷屏网络。这不仅仅是简单的礼节性互动，更是中国科...','# 热点纵览：当这些新闻串联在一起，我们看到了一个怎样的时代？\n\n近期热点新闻如潮水般涌来，从国际外交到科技应用，从社会民生到文化娱乐，看似杂乱无章的信息背后，实则隐藏着中国社会发展的深层脉络。本文将尝试将这些碎片化的新闻串联起来，勾勒出一幅当代中国的生动画卷。\n\n## 一、外交风云：自信从容的中国姿态\n\n最近的外交舞台上，中国展现出了前所未有的从容与自信。西班牙首相访华期间，在清华大学的握手场景被网友调侃“握不过来了”，而试坐小米SU7时雷军陪同的画面更是刷屏网络。这不仅仅是简单的礼节性互动，更是中国科技实力在国际舞台上获得认可的直观体现。\n\n在伊朗问题上，中国外交部多次回应相关传闻，强调在伊核问题上的一贯立场。面对美方可能的制裁威胁，中方的回应既保持了原则性，又展现了灵活性。在中俄关系问题上，中方明确表示“世代友好”的初心不变，这种表态既是对两国关系的定调，也是对国际社会质疑的正面回应。\n\n值得注意的是匈牙利大选后中方立场的调整。欧尔班执政16年后的败选，被视为欧盟内部政治力量重新洗牌的信号。中方对此的反应体现了务实外交的特质——不固守旧交，而是着眼于未来的合作潜力。\n\n## 二、科技突破：从田间地头到智能终端\n\n科技创新是这一轮新闻中最为亮眼的线索之一。**中国机器人在波兰街头驱赶野猪**的新闻初看是趣闻，细想却是中国农业智能化输出的典型案例。这不仅展现了中国机器人在复杂环境中的应用能力，更说明了中国农业科技正在走向世界。\n\n华为在折叠屏领域的持续发力值得关注。继此前的小折叠和大折叠之后，华为推出的横向阔折叠手机标志着折叠屏技术的又一次突破。从问界Z6预订量突破10万台的数字中，我们看到了消费者对国产高端产品的认可度正在显著提升。这背后是供应链的成熟、品质的提升，更是国产品牌自信心的回归。\n\n量子计算领域的进展则更具前瞻性。“量子末日倒计时”的说法虽然夸张，但比特币等加密货币面临的潜在威胁确实存在。量子计算对现有加密体系的挑战，将是未来数年科技博弈的重要战场。\n\n## 三、社会民生：进步中的烦恼\n\n社会新闻往往最能触动公众神经。**长春姐弟俩被遗留在托管班两年**的案例，折射出儿童托管行业的监管盲区。家长将孩子送入托管班本是无奈之举，但当托管机构出现问题时，儿童的权益如何保障？这需要制度层面的进一步规范。\n\n食品安全方面，“洗生肉不要直接用水冲”的提醒看似常识，实则反映了公众健康意识的提升。研究显示厨房油烟与肺癌风险的关联，虽然具体数据有待更严格的科学验证，但“油烟机怎样用能把风险降到最低”这个问题本身，就说明了现代人对生活质量的更高追求。\n\n北京“四月飞雪”的异常天气现象引发了广泛讨论。气候变化带来的极端天气正在成为新常态，如何应对这种不确定性，是城市治理面临的新课题。\n\n## 四、文化娱乐：跨界与转型\n\n演员文章在上海开面馆的消息引发了关于明星转型的讨论。曾经的“周一见”主角如今做起餐饮生意，这个选择既可能是困境中的妥协，也可能是人生下半场的重新出发。\n\n影视行业同样热闹，央视多部大剧官宣，宿迁官宣何润东“霸王归来”，梦龙雪糕广告被指低俗……文化产业的活跃度由此可见一斑。争议本身也是一种关注，如何在商业诉求与公众感受之间找到平衡，是每一个文创从业者都需要思考的问题。\n\n## 五、深层趋势：转型中的中国社会\n\n将这一时期的新闻串联起来，我们可以看到几个清晰的趋势：\n\n**开放与自信并存**。无论是外交场合的从容互动，还是国产品牌在国际市场的崛起，都说明中国正在以更加自信的姿态面向世界。\n\n**科技正在重塑生活**。从农业机器人到智能汽车，从折叠屏手机到量子计算，科技不再是高高在上的概念，而是正在深度融入日常生活的每一个细节。\n\n**社会治理精细化**。从食品安全提醒到儿童托管规范，从景区管理到城市天气应对，社会治理的颗粒度正在变得越来越细。\n\n**多元价值观共生**。明星可以开面馆，博士可以养毒蛇，每一种人生选择都在获得更多的尊重与理解。\n\n这些看似零散的新闻，实际上勾勒出了一个转型期的中国社会图景。在这个图景中，有挑战也有机遇，有争议也有共识，有困境也有突破。理解这些新闻背后的逻辑，不仅能帮助我们更好地把握当下，也为我们思考未来提供了宝贵的参照。','',1,NULL,1,0,1,'2026-04-13 20:46:57','2026-04-13 20:46:57');
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_collection`
--

DROP TABLE IF EXISTS `article_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `article_collection` (
  `article_id` bigint(20) NOT NULL,
  `collection_id` bigint(20) NOT NULL,
  `sort` int(11) DEFAULT '0',
  PRIMARY KEY (`article_id`,`collection_id`),
  KEY `collection_id` (`collection_id`),
  CONSTRAINT `article_collection_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `article_collection_ibfk_2` FOREIGN KEY (`collection_id`) REFERENCES `collection` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_collection`
--

LOCK TABLES `article_collection` WRITE;
/*!40000 ALTER TABLE `article_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `article_tag` (
  `article_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL,
  PRIMARY KEY (`article_id`,`tag_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `article_tag_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `article_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag`
--

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `alias` varchar(50) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `sort` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'AI每日热点情报','AI Daily Hotspot Intelligence','',0,NULL,NULL),(2,'项目分享','Project Sharing','',0,NULL,NULL);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collection`
--

DROP TABLE IF EXISTS `collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT '0',
  `article_count` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collection`
--

LOCK TABLES `collection` WRITE;
/*!40000 ALTER TABLE `collection` DISABLE KEYS */;
INSERT INTO `collection` VALUES (1,'每日热点','',NULL,0,0,NULL,NULL);
/*!40000 ALTER TABLE `collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT '0',
  `content` text NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `reply_to` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `article_id` (`article_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (3,2,NULL,0,'你好','匿名用户',NULL,1,NULL,NULL),(4,2,NULL,0,'你好','匿名用户',NULL,1,NULL,NULL),(5,2,NULL,0,'你好','匿名用户',NULL,1,'2026-04-13 21:26:01','2026-04-13 21:26:01'),(6,19,NULL,0,'可以','匿名用户',NULL,1,'2026-04-13 21:30:47','2026-04-13 21:30:47'),(7,19,NULL,6,'可以','匿名用户',NULL,1,'2026-04-13 21:31:21','2026-04-13 21:31:21');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1' COMMENT '1=???,0=???',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$v9OjAUimQyIH18xITAUw5OBFRmTqP2Y8VaolhFhXfgzE40yMntPBi',NULL,'???',NULL,1,NULL,'2026-04-12 16:01:14'),(3,'admin2','$2a$10$v9OjAUimQyIH18xITAUw5OBFRmTqP2Y8VaolhFhXfgzE40yMntPBi','admin2@blog.com','管理员',NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'blog'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-13 23:24:12
