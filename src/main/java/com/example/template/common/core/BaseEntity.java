package com.example.template.common.core;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * 所有实体类的基类
 * <p>
 * 包含企业项目通用的公共字段：
 * - id: 主键（雪花算法生成）
 * - createTime/createBy: 创建审计
 * - updateTime/updateBy: 更新审计
 * - deleted: 逻辑删除标志
 * <p>
 * 使用方式：业务实体继承此类即可自动拥有这些字段，
 * 配合 MybatisPlusConfig 的自动填充处理器，无需手动 set 这些字段。
 * <p>
 * MyBatis-Plus 注解说明：
 * - @TableId(type = IdType.ASSIGN_ID): 主键策略为雪花算法分布式 ID
 * - @TableField(fill = FieldFill.INSERT): 插入时自动填充
 * - @TableField(fill = FieldFill.INSERT_UPDATE): 插入和更新时都自动填充
 * - @TableLogic: 标记为逻辑删除字段，删除操作变为 UPDATE 而非 DELETE
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "逻辑删除标志（0-正常 1-删除）")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
