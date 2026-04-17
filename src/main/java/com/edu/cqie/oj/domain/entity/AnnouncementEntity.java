package com.edu.cqie.oj.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;
import java.lang.String;
import java.lang.Integer;

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "announcement")
public class AnnouncementEntity {

    /**
     * 公告ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 公告标题
     */
    @Column(value = "title")
    private String title;

    /**
     * 公告内容
     */
    @Column(value = "content")
    private String content;

    /**
     * 发布者ID
     */
    @Column(value = "publisher_id")
    private Integer publisherId;

    /**
     * 重要程度：1-一般，2-重要，3-紧急
     */
    @Column(value = "importance")
    private Integer importance;

    /**
     * 是否可见
     */
    @Column(value = "visible")
    private Integer visible;

    @Column(value = "created_at")
    private Date createdAt;

    @Column(value = "updated_at")
    private Date updatedAt;

}
