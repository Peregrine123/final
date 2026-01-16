-- Seed: movie-review (影评) blog articles for UI demo (TASK-006).
-- Safe to re-run (idempotent by article_title).
--
-- Usage:
--   MYSQL_PWD=blc123456 mysql -h 127.0.0.1 -P 3306 -u blc -D blc < blc/seed-blog-2026.sql

-- 1) Cleanup: remove older "demo/docs" type posts so the blog stays focused on movie reviews.
DELETE FROM blog_article
WHERE article_title IN (
  '项目简介',
  '欢迎来到 BLC：我们在做什么？',
  'Task-006 UI 重构记录：博客页面现代化改造',
  '开发指南：本地启动、环境依赖与排查思路',
  '接口速查表：登录、博客与影单',
  'Markdown 排版演示：标题、引用、代码块与图片',
  '数据库初始化：表结构、种子数据与注意事项',
  '无封面文章：阅读版式与分段测试'
);

-- 2) Enhance the original seed movies to real reviews (keeps existing ids).
UPDATE blog_article
SET
  article_abstract = '披着轻喜剧外衣的都市童话：关于孤独、勇气，以及一次看似偶然的相遇如何改变人生。',
  -- Use weserv.nl to proxy posters (Wikimedia may be slow/blocked in some networks).
  article_cover = 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/f/f5/GorgeousPoster.jpg',
  article_date = CURDATE(),
  article_content_html =
'<p><strong>一句话点评：</strong>它不靠宏大叙事取胜，而是用轻盈的节奏讲孤独，用一点点浪漫替现实松绑。</p>
<blockquote>有些相遇像漂流瓶，落在你手里那一刻，故事才开始。</blockquote>
<h2>无剧透简介</h2>
<p>故事的起点很简单：一个被丢进海里的“心愿”，在另一端被捡起。两个人因此踏上一段不算轰轰烈烈，却足够改变彼此的旅程。</p>
<h2>我喜欢的三点</h2>
<ul>
  <li><strong>情绪很克制</strong>：它把“喜欢”藏在细节里，不急着证明什么。</li>
  <li><strong>城市质感</strong>：繁华与空旷并存，衬出角色的漂泊感。</li>
  <li><strong>节奏轻快</strong>：笑点不靠硬挠，更多是人物之间的自然反应。</li>
</ul>
<h2>可能不适合的人</h2>
<p>如果你期待的是强情节、强反转的故事，它可能显得“太温”。但若你愿意把注意力交给人物的微表情和停顿，它会越来越耐看。</p>
<h2>我的评分</h2>
<table>
  <thead><tr><th>维度</th><th>评价</th></tr></thead>
  <tbody>
    <tr><td>故事</td><td>稳</td></tr>
    <tr><td>氛围</td><td>很强</td></tr>
    <tr><td>回味</td><td>越看越多</td></tr>
    <tr><td>综合</td><td>4.0 / 5</td></tr>
  </tbody>
</table>',
  article_content_md =
'**一句话点评：** 它不靠宏大叙事取胜，而是用轻盈的节奏讲孤独，用一点点浪漫替现实松绑。

> 有些相遇像漂流瓶，落在你手里那一刻，故事才开始。

## 无剧透简介

故事的起点很简单：一个被丢进海里的“心愿”，在另一端被捡起。两个人因此踏上一段不算轰轰烈烈，却足够改变彼此的旅程。

## 我喜欢的三点

- **情绪很克制**：它把“喜欢”藏在细节里，不急着证明什么。
- **城市质感**：繁华与空旷并存，衬出角色的漂泊感。
- **节奏轻快**：笑点不靠硬挠，更多是人物之间的自然反应。

## 可能不适合的人

如果你期待的是强情节、强反转的故事，它可能显得“太温”。但若你愿意把注意力交给人物的微表情和停顿，它会越来越耐看。

## 我的评分

| 维度 | 评价 |
| --- | --- |
| 故事 | 稳 |
| 氛围 | 很强 |
| 回味 | 越看越多 |
| 综合 | 4.0 / 5 |'
WHERE article_title = '玻璃樽';


UPDATE blog_article
SET
  article_abstract = '喜剧的壳、悲剧的核：当你笑着回头，才发现“成长”是学会接受错过。',
  article_cover = 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/0c/A_Chinese_Odyssey_2.jpg',
  article_date = CURDATE(),
  article_content_html =
'<p><strong>一句话点评：</strong>它最狠的地方在于：把遗憾拍成了笑话，让你在笑声里突然沉默。</p>
<blockquote>所谓成长，不是得到更多，而是学会承认：有些人只能同行一段路。</blockquote>
<h2>为什么它经得起重看</h2>
<ul>
  <li><strong>情绪反差</strong>：上一秒荒诞，下一秒真心，反差越大越扎人。</li>
  <li><strong>人物弧光</strong>：主角并不是突然“成熟”，而是在一次次选择里被迫清醒。</li>
  <li><strong>隐喻很多</strong>：关于命运、责任、以及把爱说出口的时机。</li>
</ul>
<h2>无剧透聊两点</h2>
<p>第一，它并不只是在讲爱情，更像在讲“人如何成为自己”。第二，许多经典桥段之所以经典，不是因为台词，而是因为你会在不同年龄读出不同的刺。</p>
<h2>推荐人群</h2>
<p>适合：想看笑着笑着就鼻酸的人；以及想把一些旧事翻出来重新理解的人。</p>
<h2>我的评分</h2>
<table>
  <thead><tr><th>维度</th><th>评价</th></tr></thead>
  <tbody>
    <tr><td>娱乐性</td><td>高</td></tr>
    <tr><td>情感力度</td><td>很高</td></tr>
    <tr><td>可重看性</td><td>很高</td></tr>
    <tr><td>综合</td><td>4.5 / 5</td></tr>
  </tbody>
</table>',
  article_content_md =
'**一句话点评：** 它最狠的地方在于：把遗憾拍成了笑话，让你在笑声里突然沉默。

> 所谓成长，不是得到更多，而是学会承认：有些人只能同行一段路。

## 为什么它经得起重看

- **情绪反差**：上一秒荒诞，下一秒真心，反差越大越扎人。
- **人物弧光**：主角并不是突然“成熟”，而是在一次次选择里被迫清醒。
- **隐喻很多**：关于命运、责任、以及把爱说出口的时机。

## 无剧透聊两点

第一，它并不只是在讲爱情，更像在讲“人如何成为自己”。第二，许多经典桥段之所以经典，不是因为台词，而是因为你会在不同年龄读出不同的刺。

## 推荐人群

适合：想看笑着笑着就鼻酸的人；以及想把一些旧事翻出来重新理解的人。

## 我的评分

| 维度 | 评价 |
| --- | --- |
| 娱乐性 | 高 |
| 情感力度 | 很高 |
| 可重看性 | 很高 |
| 综合 | 4.5 / 5 |'
WHERE article_title = '大话西游';


UPDATE blog_article
SET
  article_abstract = '公路尽头不是答案，是选择：关于理想、现实与年轻人的沉默告别。',
  article_cover = 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/5/5b/Houhuiwuqi_poster.jpg',
  article_date = CURDATE(),
  article_content_html =
'<p><strong>一句话点评：</strong>看起来很松，内里很硬。它用漫不经心的口吻，讲一代人的认真。</p>
<blockquote>路上遇见的人，会把你带去不同的方向；而你最终要为自己的方向负责。</blockquote>
<h2>无剧透简介</h2>
<p>一段旅程串起几个人的相遇与分岔。它不急着给答案，更像在记录：人怎么在路上慢慢变得像自己。</p>
<h2>我喜欢的地方</h2>
<ul>
  <li><strong>对白有劲</strong>：不是金句堆砌，而是把尴尬、嘴硬和真心放在同一句话里。</li>
  <li><strong>节奏像呼吸</strong>：有空拍、有停顿，让情绪自己浮出来。</li>
  <li><strong>人物很真实</strong>：不完美，但你能理解他为什么那样做。</li>
</ul>
<h2>我的评分</h2>
<p>4.2 / 5。适合夜里一个人看，看完会想给朋友发一句：有空，路上见。</p>',
  article_content_md =
'**一句话点评：** 看起来很松，内里很硬。它用漫不经心的口吻，讲一代人的认真。

> 路上遇见的人，会把你带去不同的方向；而你最终要为自己的方向负责。

## 无剧透简介

一段旅程串起几个人的相遇与分岔。它不急着给答案，更像在记录：人怎么在路上慢慢变得像自己。

## 我喜欢的地方

- **对白有劲**：不是金句堆砌，而是把尴尬、嘴硬和真心放在同一句话里。
- **节奏像呼吸**：有空拍、有停顿，让情绪自己浮出来。
- **人物很真实**：不完美，但你能理解他为什么那样做。

## 我的评分

4.2 / 5。适合夜里一个人看，看完会想给朋友发一句：有空，路上见。'
WHERE article_title = '后会无期';


UPDATE blog_article
SET
  article_abstract = '极美的画面里藏着成长的代价：当选择变得沉重，童话就不再轻飘。',
  article_cover = 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/d/dc/P2361744534.jpg',
  article_date = CURDATE(),
  article_content_html =
'<p><strong>一句话点评：</strong>它的美术几乎无可挑剔，而故事的争议点也恰恰来自它的“执念”。</p>
<blockquote>选择不是愿望清单，选择意味着你要承担后果。</blockquote>
<h2>无剧透简介</h2>
<p>在一个充满神话想象的世界里，主角因为一次决定卷入巨大的波澜。故事的核心并不复杂：你愿意为自己的愿望付出什么？</p>
<h2>亮点</h2>
<ul>
  <li><strong>画面与配色</strong>：每一帧都像海报，情绪被颜色放大。</li>
  <li><strong>音乐与氛围</strong>：旋律一响，世界观就立住了。</li>
  <li><strong>情感直给</strong>：它很少拐弯，喜欢与不舍都摆在明面。</li>
</ul>
<h2>不足</h2>
<p>叙事上有些地方跳得快，人物动机需要你用情绪去补齐。接受这一点，它会更好看。</p>
<h2>我的评分</h2>
<p>3.8 / 5。推荐给：重视画面与氛围的人。</p>',
  article_content_md =
'**一句话点评：** 它的美术几乎无可挑剔，而故事的争议点也恰恰来自它的“执念”。

> 选择不是愿望清单，选择意味着你要承担后果。

## 无剧透简介

在一个充满神话想象的世界里，主角因为一次决定卷入巨大的波澜。故事的核心并不复杂：你愿意为自己的愿望付出什么？

## 亮点

- **画面与配色**：每一帧都像海报，情绪被颜色放大。
- **音乐与氛围**：旋律一响，世界观就立住了。
- **情感直给**：它很少拐弯，喜欢与不舍都摆在明面。

## 不足

叙事上有些地方跳得快，人物动机需要你用情绪去补齐。接受这一点，它会更好看。

## 我的评分

3.8 / 5。推荐给：重视画面与氛围的人。'
WHERE article_title = '大鱼海棠';


-- 3) More movie reviews (insert if missing).
INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '重庆森林',
  '<p><strong>一句话点评：</strong>它把孤独拍得很轻，把想念拍得很重；像一罐快过期的菠萝，酸甜都来不及解释。</p>
<blockquote>人与人的靠近，有时只差一个“刚好”。</blockquote>
<h2>我喜欢的地方</h2>
<ul>
  <li><strong>节奏与剪辑</strong>：晃动、跳帧、重复音乐，让情绪像潮水一样涌上来。</li>
  <li><strong>城市夜色</strong>：霓虹、雨、玻璃反光，像把心事摊在灯下。</li>
  <li><strong>细节很会讲</strong>：日常物件被赋予情感，孤独因此有了形状。</li>
</ul>
<h2>适合什么时候看</h2>
<p>适合深夜。看完不会立刻哭，但会在回家路上突然想起某个人。</p>
<h2>我的评分</h2>
<p>4.6 / 5。</p>',
  '**一句话点评：** 它把孤独拍得很轻，把想念拍得很重；像一罐快过期的菠萝，酸甜都来不及解释。

> 人与人的靠近，有时只差一个“刚好”。

## 我喜欢的地方

- **节奏与剪辑**：晃动、跳帧、重复音乐，让情绪像潮水一样涌上来。
- **城市夜色**：霓虹、雨、玻璃反光，像把心事摊在灯下。
- **细节很会讲**：日常物件被赋予情感，孤独因此有了形状。

## 适合什么时候看

适合深夜。看完不会立刻哭，但会在回家路上突然想起某个人。

  ## 我的评分

4.6 / 5。',
  '霓虹与雨夜里的孤独：一部把想念拍成味道的电影。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/c/c0/Chungking_Express.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '重庆森林');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '让子弹飞',
  '<p><strong>一句话点评：</strong>表面是爽片，内里是权力与叙事的博弈；你以为你看懂了，其实你只是站在某个人的叙事里。</p>
<blockquote>最有意思的从来不是“赢没赢”，而是谁在定义“赢”。</blockquote>
<h2>看点</h2>
<ul>
  <li><strong>台词密度高</strong>：信息量大，反复看会冒出新的意思。</li>
  <li><strong>角色都有面具</strong>：每个人都在演，但演着演着就变成了真。</li>
  <li><strong>节奏很稳</strong>：冲突一波接一波，几乎不给你喘气。</li>
</ul>
<h2>我的评分</h2>
<p>4.4 / 5。适合和朋友一起看，边看边聊会更爽。</p>',
  '**一句话点评：** 表面是爽片，内里是权力与叙事的博弈；你以为你看懂了，其实你只是站在某个人的叙事里。

> 最有意思的从来不是“赢没赢”，而是谁在定义“赢”。

## 看点

- **台词密度高**：信息量大，反复看会冒出新的意思。
- **角色都有面具**：每个人都在演，但演着演着就变成了真。
- **节奏很稳**：冲突一波接一波，几乎不给你喘气。

  ## 我的评分

4.4 / 5。适合和朋友一起看，边看边聊会更爽。',
  '爽感之下是叙事博弈：每个人都在讲故事，真相反而最安静。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/c/c6/Let_the_Bullets_Fly_Poster.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '让子弹飞');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '霸王别姬',
  '<p><strong>一句话点评：</strong>它不是在讲戏，而是在讲人如何被时代与自我共同塑形。</p>
<blockquote>戏里有真情，戏外更难做人。</blockquote>
<h2>为什么震撼</h2>
<ul>
  <li><strong>人物命运</strong>：不是跌宕起伏，而是一路被推着走，越走越窄。</li>
  <li><strong>时代切面</strong>：大时代不靠说教出现，而是渗进每个选择里。</li>
  <li><strong>情感复杂</strong>：爱、恨、依赖、逃避，全都纠缠在一起。</li>
</ul>
<h2>我的评分</h2>
<p>4.8 / 5。它值得你在不同阶段重看。</p>',
  '**一句话点评：** 它不是在讲戏，而是在讲人如何被时代与自我共同塑形。

> 戏里有真情，戏外更难做人。

## 为什么震撼

- **人物命运**：不是跌宕起伏，而是一路被推着走，越走越窄。
- **时代切面**：大时代不靠说教出现，而是渗进每个选择里。
- **情感复杂**：爱、恨、依赖、逃避，全都纠缠在一起。

  ## 我的评分

4.8 / 5。它值得你在不同阶段重看。',
  '戏里戏外皆人生：一部关于命运、时代与自我认同的史诗。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/0c/Bawangbieji.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '霸王别姬');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '千与千寻',
  '<p><strong>一句话点评：</strong>童话的外壳里装着现实：成长就是学会克制、记住名字、然后继续往前走。</p>
<blockquote>真正的勇敢，不是打败怪物，而是不被世界同化。</blockquote>
<h2>我喜欢的地方</h2>
<ul>
  <li><strong>世界观有规矩</strong>：奇幻不是乱来，每条规则都服务于主题。</li>
  <li><strong>角色很温柔</strong>：即使是“怪”，也有自己的孤独与欲望。</li>
  <li><strong>细节密度</strong>：每次重看都能发现新暗线。</li>
</ul>
<h2>我的评分</h2>
<p>4.7 / 5。</p>',
  '**一句话点评：** 童话的外壳里装着现实：成长就是学会克制、记住名字、然后继续往前走。

> 真正的勇敢，不是打败怪物，而是不被世界同化。

## 我喜欢的地方

- **世界观有规矩**：奇幻不是乱来，每条规则都服务于主题。
- **角色很温柔**：即使是“怪”，也有自己的孤独与欲望。
- **细节密度**：每次重看都能发现新暗线。

  ## 我的评分

4.7 / 5。',
  '童话写给大人：在失控的世界里，学会守住自己。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/09/Spirited_away_poster.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '千与千寻');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '星际穿越',
  '<p><strong>一句话点评：</strong>它用硬核的科学外壳包住柔软的情感：时间并不残酷，残酷的是我们错过彼此。</p>
<blockquote>当你穿过时间去理解一个人，你会更珍惜当下的每一次告别。</blockquote>
<h2>看点</h2>
<ul>
  <li><strong>想象力</strong>：宇宙尺度下，人的渺小被放大得更动人。</li>
  <li><strong>情绪推进</strong>：宏大的设定最终落回到“人和人”的关系。</li>
  <li><strong>配乐加成</strong>：旋律一响，心就跟着失重。</li>
</ul>
<h2>我的评分</h2>
<p>4.5 / 5。</p>',
  '**一句话点评：** 它用硬核的科学外壳包住柔软的情感：时间并不残酷，残酷的是我们错过彼此。

> 当你穿过时间去理解一个人，你会更珍惜当下的每一次告别。

## 看点

- **想象力**：宇宙尺度下，人的渺小被放大得更动人。
- **情绪推进**：宏大的设定最终落回到“人和人”的关系。
- **配乐加成**：旋律一响，心就跟着失重。

  ## 我的评分

4.5 / 5。',
  '用宇宙讲亲情：科学越冷，情感越热。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/b/bc/Interstellar_film_poster.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '星际穿越');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '无间道',
  '<p><strong>一句话点评：</strong>身份是一座牢房：你越想证明自己是谁，就越像在失去自己。</p>
<blockquote>最难过的不是黑白不分，而是你发现自己已经没有退路。</blockquote>
<h2>好看在哪</h2>
<ul>
  <li><strong>张力</strong>：每一次对视都像在赌命。</li>
  <li><strong>节奏</strong>：紧凑但不乱，信息交代得很清楚。</li>
  <li><strong>结尾余味</strong>：不是爽，而是冷。</li>
</ul>
<h2>我的评分</h2>
<p>4.3 / 5。</p>',
  '**一句话点评：** 身份是一座牢房：你越想证明自己是谁，就越像在失去自己。

> 最难过的不是黑白不分，而是你发现自己已经没有退路。

## 好看在哪

- **张力**：每一次对视都像在赌命。
- **节奏**：紧凑但不乱，信息交代得很清楚。
- **结尾余味**：不是爽，而是冷。

  ## 我的评分

4.3 / 5。',
  '身份与命运的对撞：每个人都在黑白之间走钢丝。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/a/a3/Infernal_Affairs.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '无间道');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '你的名字',
  '<p><strong>一句话点评：</strong>它用最青春的方式讲“遗憾”：当你终于想起一个人，世界已经转了好几圈。</p>
<blockquote>有些人你没见过几次，却像认识了很久。</blockquote>
<h2>我喜欢的地方</h2>
<ul>
  <li><strong>情绪很准确</strong>：那种说不清的心动与失落，被拍得很具体。</li>
  <li><strong>画面清澈</strong>：天空、光线、城市与乡镇的对比很动人。</li>
  <li><strong>节奏抓人</strong>：你会想一直看下去，直到最后一秒。</li>
</ul>
<h2>我的评分</h2>
<p>4.2 / 5。</p>',
  '**一句话点评：** 它用最青春的方式讲“遗憾”：当你终于想起一个人，世界已经转了好几圈。

> 有些人你没见过几次，却像认识了很久。

## 我喜欢的地方

- **情绪很准确**：那种说不清的心动与失落，被拍得很具体。
- **画面清澈**：天空、光线、城市与乡镇的对比很动人。
- **节奏抓人**：你会想一直看下去，直到最后一秒。

  ## 我的评分

4.2 / 5。',
  '关于相遇与错过：把青春拍成一封迟到的信。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/4/4e/Your_name_poster.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '你的名字');


INSERT INTO blog_article (article_title, article_content_html, article_content_md, article_abstract, article_cover, article_date)
SELECT
  '流浪地球',
  '<p><strong>一句话点评：</strong>最打动人的不是特效，而是那种“把家带上路”的执拗与热血。</p>
<blockquote>当选择只剩下坚持，人就会变得很团结。</blockquote>
<h2>看点</h2>
<ul>
  <li><strong>情绪是集体的</strong>：它把个人命运放进宏大工程里，热血因此更真实。</li>
  <li><strong>节奏够紧</strong>：灾难推进清晰，冲突不断升级。</li>
  <li><strong>中国式浪漫</strong>：不是逃离地球，而是带着地球走。</li>
</ul>
<h2>我的评分</h2>
<p>4.1 / 5。</p>',
  '**一句话点评：** 最打动人的不是特效，而是那种“把家带上路”的执拗与热血。

> 当选择只剩下坚持，人就会变得很团结。

## 看点

- **情绪是集体的**：它把个人命运放进宏大工程里，热血因此更真实。
- **节奏够紧**：灾难推进清晰，冲突不断升级。
- **中国式浪漫**：不是逃离地球，而是带着地球走。

  ## 我的评分

4.1 / 5。',
  '把家背在身上：一部带着热度的中国科幻。',
  'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/05/%E3%80%8A%E6%B5%81%E6%B5%AA%E5%9C%B0%E7%90%83%E3%80%8B%E7%94%B5%E5%BD%B1%E6%B5%B7%E6%8A%A5.jpg',
  CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM blog_article WHERE article_title = '流浪地球');


-- 4) Keep covers in sync when re-running (even if rows already exist).
UPDATE blog_article
SET article_cover = CASE article_title
  WHEN '玻璃樽' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/f/f5/GorgeousPoster.jpg'
  WHEN '大话西游' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/0c/A_Chinese_Odyssey_2.jpg'
  WHEN '后会无期' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/5/5b/Houhuiwuqi_poster.jpg'
  WHEN '大鱼海棠' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/d/dc/P2361744534.jpg'
  WHEN '重庆森林' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/c/c0/Chungking_Express.jpg'
  WHEN '让子弹飞' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/c/c6/Let_the_Bullets_Fly_Poster.jpg'
  WHEN '霸王别姬' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/0c/Bawangbieji.jpg'
  WHEN '千与千寻' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/09/Spirited_away_poster.jpg'
  WHEN '星际穿越' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/b/bc/Interstellar_film_poster.jpg'
  WHEN '无间道' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/a/a3/Infernal_Affairs.jpg'
  WHEN '你的名字' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/4/4e/Your_name_poster.jpg'
  WHEN '流浪地球' THEN 'https://images.weserv.nl/?url=upload.wikimedia.org/wikipedia/zh/0/05/%E3%80%8A%E6%B5%81%E6%B5%AA%E5%9C%B0%E7%90%83%E3%80%8B%E7%94%B5%E5%BD%B1%E6%B5%B7%E6%8A%A5.jpg'
  ELSE article_cover
END
WHERE article_title IN (
  '玻璃樽',
  '大话西游',
  '后会无期',
  '大鱼海棠',
  '重庆森林',
  '让子弹飞',
  '霸王别姬',
  '千与千寻',
  '星际穿越',
  '无间道',
  '你的名字',
  '流浪地球'
);
