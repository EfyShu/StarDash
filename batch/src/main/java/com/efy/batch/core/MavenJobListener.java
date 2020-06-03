package com.efy.batch.core;

import com.efy.batch.dto.ResultData;
import com.efy.batch.dto.RetCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @Date 2019/12/18 15:53
 * @Created by Efy
 * @Description TODO
 */
@Slf4j
public class MavenJobListener implements BatchListener{
    @Override
    public void singleComplete(ResultData result) {
        String currJar = (String) result.getData();
        if(result.getStatus() == RetCode.OK.code){
            MavenUtil.writeToFile(new File(MavenUtil.FILE_PATH+"succ.txt"),(String) result.getData(),true);
        }else{
            String[] temp = currJar.split("\\|");
            String jarName = temp[1] + "-" + temp[3] + "." + temp[5];
            log.info("失败了:" + jarName + " 错误码:" + result.getStatus() + " 错误信息:" + result.getMessage());
        }
    }

    @Override
    public void piecesComplete(ResultData result) {
        log.info("接收分片结果:" + result);
    }

    @Override
    public void allComplete(ResultData result) {
        log.info("接收批量任务完成结果:" + result);
    }
}
