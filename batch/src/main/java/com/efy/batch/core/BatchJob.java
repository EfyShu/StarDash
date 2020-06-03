package com.efy.batch.core;


import com.efy.batch.dto.ResultData;

public interface BatchJob {
    /**
     * 用来初始化相关数据
     */
    void init();
    ResultData doJob();
}
