package com.efy.batch.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 接口返回值
 */
@Data
public class ResultData<T> {
    @ApiModelProperty(value = "请求状态代码",required = true)
    private Integer status = 0;
    @ApiModelProperty(value = "请求状态描述",required = true)
    private String message;
    @ApiModelProperty(value = "请求返回数据",required = true)
    private T data;

    public ResultData() {
    }

    public ResultData(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResultData(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ResultData ok() {
        ResultData resultData = new ResultData(RetCode.OK.code, RetCode.OK.msg);
        return resultData;
    }

    public static <T> ResultData<T> ok(T data) {
        ResultData resultData = new ResultData(RetCode.OK.code, RetCode.OK.msg, data);
        return resultData;
    }

    public static ResultData ok(RetCode ret, String msg) {
        ResultData resultData = new ResultData(ret.code, msg);
        return resultData;
    }

    public static ResultData failed(RetCode ret) {
        ResultData resultData = new ResultData(ret.code, ret.msg);
        return resultData;
    }

    public static ResultData failed(int code, String message) {
        ResultData resultData = new ResultData(code, message);
        return resultData;
    }

    public static ResultData failed(RetCode ret, String message) {
        ResultData resultData = new ResultData(ret.code, message);

        return resultData;
    }

    public static <T> ResultData<T> failed(int code, String message, T data) {
        ResultData resultData = new ResultData(code, message, data);

        return resultData;
    }

    public String toJSON() {
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("status", status);
        jsonResult.put("message", message);
        jsonResult.put("data", null);
        return jsonResult.toJSONString();
    }
}
