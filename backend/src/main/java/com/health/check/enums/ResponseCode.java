package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),

    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    PASSWORD_ERROR(1003, "密码错误"),
    USERNAME_EXISTS(1004, "用户名已存在"),

    CHECK_IN_EXISTS(2001, "今天已经打过卡了"),
    RECORD_EXISTS(2002, "今天该类型记录已存在，请更新而非新增"),

    RECORD_NOT_FOUND(3001, "记录不存在"),
    NO_PERMISSION(3002, "无权操作该记录"),
    ;

    private final Integer code;
    private final String message;
}
