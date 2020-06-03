package com.efy.batch.core;


import com.efy.batch.dto.ResultData;
import com.efy.batch.dto.RetCode;

/**
 * @Date 2019/12/18 15:52
 * @Created by Efy
 * @Description TODO
 */
public class BatchMavenJob implements BatchJob{
    private String[] jars;
    private String[] deploedList;
    private volatile int index = -1;


    @Override
    public void init() {
        jars = MavenUtil.FILE_LIST.toString().split("\r\n");
        deploedList = MavenUtil.readDeployed();
    }

    @Override
    public ResultData doJob() {
        String currJar = jars[next()];
        if(MavenUtil.checkDeployed(currJar,deploedList)){
            return ResultData.failed(RetCode.DATA_EXIST.code,RetCode.DATA_EXIST.msg,currJar);
        }
        boolean result = MavenUtil.doSingleDeploy(currJar,false);
        if(result){
            return ResultData.ok(currJar+"\r\n");
        }
        return ResultData.failed(RetCode.FAILED.code,RetCode.FAILED.msg,currJar);
    }

    /**
     * 全局变量需要考虑多线程安全
     * @return
     */
    private synchronized int next(){
        return index = (index >= jars.length -1) ? jars.length -1 : index + 1;
    }
}
