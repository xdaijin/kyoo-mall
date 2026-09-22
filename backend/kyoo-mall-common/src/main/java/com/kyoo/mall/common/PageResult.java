package com.kyoo.mall.common;

import java.util.List;

/**
 * 分页查询结果（跨域原语）。技术无关，domain 仓储接口、应用服务、HTTP 响应共用。
 * 持久化框架的分页对象（如 MyBatis-Plus 的 Page）只允许出现在 infrastructure，
 * 由仓储实现转换为本类型后再向外传递。
 */
public record PageResult<T>(List<T> records, long total, long current, long size) {

    public PageResult {
        records = records == null ? List.of() : records;
    }

    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        return new PageResult<>(records, total, current, size);
    }

    /** 总页数 */
    public long pages() {
        return size <= 0 ? 0 : (total + size - 1) / size;
    }
}
