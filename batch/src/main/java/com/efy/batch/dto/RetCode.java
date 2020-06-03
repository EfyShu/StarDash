package com.efy.batch.dto;

/**
 * 返回状态码
 */
public enum RetCode {
    OK(0, "操作成功"),

    FAILED(-1, "操作失败"),
    
    EXCEPTION(-2, "服务器异常，请联系系统管理员"),

    ACCESS_DENIED(-3, "当前用户无此权限"),

    TOKEN_OVERTIME(-4, "登录凭证已失效,请重新登录"),

    DATA_EXIST(1000, "数据已存在"),

    UNAUTHORIZED(1001, "用户名或密码错误"),

    DATA_NOT_EXIST(1002, "数据不存在"),

    DATA_INVALID(1003, "参数不合法"),

    ACCOUNTSTATUS_DISABLE(1004, "账号已被禁用"),

    DATA_STATUS_CHANGED(1005, "数据状态已变更，请刷新页面"),

    FILE_TYPE_INVALID(1007, "文件格式不正确"),

    FILE_SIZE_INVALID(1008, "文件大小不正确"),

    COMPANY_ISUSE(1009, "客户已被项目使用，不能删除"),

    ASSETS_ISUSE(1009, "资产已被项目使用，不能删除"),

    CODE_ERROR(1010, "验证码错误"),

    BANK_ACCOUNT_EXIST(1011, "银行帐号已存在"),

    BUSINESS_PROJECT_STAGE_CHANGE(2001, "项目阶段已变更，请刷新页面"),

    FRAMENUMBER_EXIST(1012, "车架号已存在"),
    
    IS_BLACKLIST(1013, "客户为黑名单企业"),

    END_DAY_DOING(100021,"日终执行中，禁止交易"),

    END_DAY_PROCESSING(100022,"日终处理中"),

    HOOK_SERIAL_EXIST(10066,"已存在挂接关系，请换条数据进行操作"),

    TEMPBALANCE_IS_ZERO(10086,"该流水暂存金额为0，请换条数据进行操作"),

    PAID_EXIST(10068,"该编号已经存在，不能重复插入"),
    
    MD5_EXCEPTION(1314,"获取文件MD5码值异常"),
    
    SAVE_EXCEPTION(1315,"待上传文件存入本地异常"),

    BARCODE_NOT_EXISTS(999,"没有对应的授信信息"),

    REPEATE_APPLY_EXCEPTION(20001,"不能重复申请"),
    ;

    public int code;
    public String msg;

    RetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
