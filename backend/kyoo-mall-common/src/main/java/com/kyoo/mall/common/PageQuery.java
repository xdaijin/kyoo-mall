package com.kyoo.mall.common;

/**
 * 分页查询条件（跨域原语）。技术无关，可作为 domain 仓储接口与应用服务的入参。
 * 构造时归一化：页码从 1 开始，pageSize 限制在 1~MAX_SIZE。
 */
public record PageQuery(long current, long size) {

    public static final long DEFAULT_CURRENT = 1;
    public static final long DEFAULT_SIZE = 10;
    public static final long MAX_SIZE = 100;

    public PageQuery {
        if (current < 1) {
            current = DEFAULT_CURRENT;
        }
        if (size < 1) {
            size = DEFAULT_SIZE;
        }
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
    }

    public static PageQuery of(long current, long size) {
        return new PageQuery(current, size);
    }

    /** 第一条记录的偏移量（从 0 开始），供持久化层换算 */
    public long offset() {
        return (current - 1) * size;
    }
}
