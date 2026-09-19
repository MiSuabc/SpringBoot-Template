package com.example.template.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页查询统一响应结果
 * <p>
 * 所有分页查询接口统一返回 PageResult<T>，前端按固定结构渲染分页表格。
 * <p>
 * 使用示例：
 * <pre>
 * Page<SysUser> page = userService.page(new Page<>(current, size));
 * return R.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
 * </pre>
 *
 * @param <T> 分页数据元素类型
 */
@Data
@Schema(description = "分页响应结果")
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    @Schema(description = "总记录数")
    private long total;

    /** 当前页码 */
    @Schema(description = "当前页码")
    private long current;

    /** 每页条数 */
    @Schema(description = "每页条数")
    private long size;

    /** 当前页数据列表 */
    @Schema(description = "分页数据")
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, long current, long size, List<T> records) {
        this.total = total;
        this.current = current;
        this.size = size;
        this.records = records;
    }

    /**
     * 快速构建分页结果
     */
    public static <T> PageResult<T> of(long total, long current, long size, List<T> records) {
        return new PageResult<>(total, current, size, records);
    }
}