-- 为 problem 表添加 hydro_problem_id 字段
-- 用于存储 Hydro OJ 系统中的题目ID，确保本地和Hydro的题目ID同步

ALTER TABLE `problem` 
ADD COLUMN `hydro_problem_id` VARCHAR(50) DEFAULT NULL COMMENT 'Hydro OJ题目ID（用于判题系统同步）' AFTER `visible`;

-- 更新现有数据：将已有题目的hydro_problem_id设置为与本地id相同
UPDATE `problem` 
SET `hydro_problem_id` = CAST(`id` AS CHAR) 
WHERE `hydro_problem_id` IS NULL;
