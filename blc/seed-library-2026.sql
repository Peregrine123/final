-- Seed: movie library (movies + collections) demo data.
-- Safe to re-run.
--
-- Usage:
--   MYSQL_PWD=blc123456 mysql -h 127.0.0.1 -P 3306 -u blc -D blc < blc/seed-library-2026.sql

-- 1) Align category names with the frontend "影库/影单" genre menu.
UPDATE category
SET name = CASE id
  WHEN 1 THEN '动作'
  WHEN 2 THEN '喜剧'
  WHEN 3 THEN '剧情'
  WHEN 4 THEN '科幻'
  WHEN 5 THEN '恐怖'
  WHEN 6 THEN '纪录片'
  ELSE name
END
WHERE id IN (1, 2, 3, 4, 5, 6);

-- 2) Replace existing demo data (the old seed contains lots of book-like entries).
DELETE FROM movie;
ALTER TABLE movie AUTO_INCREMENT = 1;

DELETE FROM collection;
ALTER TABLE collection AUTO_INCREMENT = 1;

-- 3) Movies (影库).
-- Note: use weserv.nl to proxy Wikimedia posters (direct Wikimedia may be slow/blocked in some networks).
INSERT INTO movie (title, cast, date, press, summary, cover, cid) VALUES
  -- 动作
  ('无间道', '刘德华 / 梁朝伟 / 黄秋生 / 曾志伟', '2002-12-12', '寰亚电影', '警匪卧底的身份错位，三年又三年，在黑白之间逼近人性底线。', '/api/file/seed-lib-3ec3ca3c7c66.jpg', 1),
  ('让子弹飞', '姜文 / 葛优 / 周润发 / 刘嘉玲', '2010-12-16', '英皇电影', '一场劫火车的局，变成县城权力游戏；黑色幽默与反转一路拉满。', '/api/file/seed-lib-87f0e348dd73.jpg', 1),
  ('功夫', '周星驰 / 黄圣依 / 元华 / 梁小龙', '2004-12-23', '星辉海外', '街头混混误入高手云集的城寨，荒诞与热血并存的功夫童话。', '/api/file/seed-lib-d85309a9ad4a.jpg', 1),

  -- 喜剧
  ('大话西游', '周星驰 / 朱茵 / 吴孟达 / 莫文蔚', '1995-01-21', '彩星电影', '把爱情与宿命拍成喜剧：笑着笑着就懂了什么叫“错过”。', '/api/file/seed-lib-ffa70b160246.jpg', 2),
  ('玻璃樽', '成龙 / 舒淇 / 梁朝伟', '1999-02-06', '嘉禾电影', '轻快浪漫的都市童话，漂流瓶般的相遇让两颗孤独的心靠近。', '/api/file/seed-lib-bc614caa1682.jpg', 2),
  ('唐伯虎点秋香', '周星驰 / 巩俐 / 陈百祥', '1993-07-01', '永盛电影', '周星驰式无厘头喜剧，用夸张与机智重写才子佳人的经典套路。', '/api/file/seed-lib-ffd19087c8eb.jpg', 2),

  -- 剧情
  ('霸王别姬', '张国荣 / 巩俐 / 张丰毅', '1993-01-01', '汤臣电影', '戏里戏外皆人生，个人命运被时代裹挟，爱与执念走到尽头。', '/api/file/seed-lib-805a268c694c.jpg', 3),
  ('重庆森林', '梁朝伟 / 王菲 / 金城武 / 林青霞', '1994-07-14', '泽东电影', '霓虹雨夜里的孤独与想念，两段错身的爱情在香港夜色中发酵。', '/api/file/seed-lib-c376678bc57e.jpg', 3),
  ('后会无期', '冯绍峰 / 陈柏霖 / 钟汉良 / 王珞丹', '2014-07-24', '博纳影业', '公路尽头没有答案，年轻人把离别说得轻描淡写，却句句扎心。', '/api/file/seed-lib-6d2b41de8e14.jpg', 3),
  ('大鱼海棠', '季冠霖 / 苏尚卿', '2016-07-08', '彼岸天文化', '东方神话想象包裹的成长寓言，美术惊艳，代价沉重。', '/api/file/seed-lib-662ee4896778.jpg', 3),
  ('你的名字', '神木隆之介 / 上白石萌音', '2016-08-26', '东宝', '在时间与距离里寻找彼此，青春的遗憾被天空与彗星点亮。', '/api/file/seed-lib-c7e0d6035c4c.jpg', 3),
  ('我不是药神', '徐峥 / 王传君 / 周一围 / 谭卓', '2018-07-05', '坏猴子影业', '现实题材的笑与泪：一个普通人被推向“善与法”的灰色地带。', '/api/file/seed-lib-c939b07c942e.jpg', 3),
  ('活着', '葛优 / 巩俐', '1994-05-17', '上影集团', '在时代洪流里活下去，苦难与幽默交织，留下最朴素的生命感。', '/api/file/seed-lib-0868be5bdfe6.jpg', 3),
  ('千与千寻', '柊瑠美 / 入野自由', '2001-07-20', '东宝', '写给大人的童话：在异世界守住名字，学会勇敢与告别。', '/api/file/seed-lib-e55e0fc9fef4.jpg', 3),

  -- 科幻
  ('星际穿越', 'Matthew McConaughey / Anne Hathaway / Jessica Chastain', '2014-11-07', 'Paramount / Warner Bros.', '用宇宙尺度讲亲情与时间，科学外壳下是最柔软的情感。', '/api/file/seed-lib-1674ecce787b.jpg', 4),
  ('流浪地球', '吴京 / 屈楚萧 / 李光洁', '2019-02-05', '中国电影', '不逃离地球，而是带着家园上路；中国科幻的热血与集体情绪。', '/api/file/seed-lib-d4100d1c051b.jpg', 4),
  ('盗梦空间', 'Leonardo DiCaprio / Joseph Gordon-Levitt / Ellen Page', '2010-07-16', 'Warner Bros.', '梦中造梦的迷宫结构，关于欲望、记忆与自我救赎的高概念悬疑。', '/api/file/seed-lib-374250d0dbe2.jpg', 4),
  ('黑客帝国', 'Keanu Reeves / Laurence Fishburne / Carrie-Anne Moss', '1999-03-31', 'Warner Bros.', '红蓝药丸的选择，虚拟世界与现实的边界被彻底打碎。', '/api/file/seed-lib-0dd8849df62b.jpg', 4),

  -- 恐怖
  ('闪灵', 'Jack Nicholson / Shelley Duvall', '1980-05-23', 'Warner Bros.', '雪夜酒店的精神崩坏，压抑氛围拉满的心理恐怖经典。', '/api/file/seed-lib-c7f2d80a037b.jpg', 5),
  ('咒怨', '奥菜惠 / 伊东美咲', '2002-10-18', '东宝', '怨念循环不止，日常空间里渗出的恐惧让人后背发凉。', '/api/file/seed-lib-581b00f1fe2b.jpg', 5),
  ('釜山行', '孔刘 / 郑有美 / 马东锡', '2016-07-20', 'Next Entertainment World', '丧尸列车上的生存与选择，动作紧凑，结尾直击人心。', '/api/file/seed-lib-3384275dfc54.jpg', 5),

  -- 纪录片
  ('血色海湾', 'Louie Psihoyos', '2009-07-31', 'Participant Media', '记录海豚捕猎真相的调查纪录片，愤怒与震撼并存。', '/api/file/seed-lib-4ccf2cf19acb.jpg', 6),
  ('华氏911', 'Michael Moore', '2004-06-25', 'Lionsgate / Miramax', '以讽刺与剪辑解构美国政治与战争叙事的争议纪录片。', '/api/file/seed-lib-8ce8232f0f34.jpg', 6);

-- 4) Collections (影单/收藏) - keep 6 items so the slideshow has content.
INSERT INTO collection (title, cast, date, press, summary, cover, cid) VALUES
  ('无间道', '刘德华 / 梁朝伟 / 黄秋生 / 曾志伟', '2002-12-12', '寰亚电影', '警匪卧底的身份错位，三年又三年，在黑白之间逼近人性底线。', '/api/file/seed-lib-3ec3ca3c7c66.jpg', 1),
  ('大话西游', '周星驰 / 朱茵 / 吴孟达 / 莫文蔚', '1995-01-21', '彩星电影', '把爱情与宿命拍成喜剧：笑着笑着就懂了什么叫“错过”。', '/api/file/seed-lib-ffa70b160246.jpg', 2),
  ('霸王别姬', '张国荣 / 巩俐 / 张丰毅', '1993-01-01', '汤臣电影', '戏里戏外皆人生，个人命运被时代裹挟，爱与执念走到尽头。', '/api/file/seed-lib-805a268c694c.jpg', 3),
  ('星际穿越', 'Matthew McConaughey / Anne Hathaway / Jessica Chastain', '2014-11-07', 'Paramount / Warner Bros.', '用宇宙尺度讲亲情与时间，科学外壳下是最柔软的情感。', '/api/file/seed-lib-1674ecce787b.jpg', 4),
  ('釜山行', '孔刘 / 郑有美 / 马东锡', '2016-07-20', 'Next Entertainment World', '丧尸列车上的生存与选择，动作紧凑，结尾直击人心。', '/api/file/seed-lib-3384275dfc54.jpg', 5),
  ('血色海湾', 'Louie Psihoyos', '2009-07-31', 'Participant Media', '记录海豚捕猎真相的调查纪录片，愤怒与震撼并存。', '/api/file/seed-lib-4ccf2cf19acb.jpg', 6);

