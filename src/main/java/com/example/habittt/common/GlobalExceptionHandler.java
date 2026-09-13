package com.example.habittt.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        // 根据错误信息映射到对应的业务状态码
        if (msg.contains("货币不足")) return Result.error(40001, msg);
        if (msg.contains("任务已完成")) return Result.error(40002, msg);
        if (msg.contains("已在队伍中")) return Result.error(40003, msg);
        if (msg.contains("队伍已满")) return Result.error(40004, msg);
        if (msg.contains("未登录") || msg.contains("Token")) return Result.error(40100, msg);
        if (msg.contains("无权限")) return Result.error(40300, msg);
        if (msg.contains("不存在")) return Result.error(40400, msg);
        if (msg.contains("冲突")) return Result.error(40900, msg);

        return Result.error(50000, msg);
    }
}
