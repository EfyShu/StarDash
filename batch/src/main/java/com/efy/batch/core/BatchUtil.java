package com.efy.batch.core;

import com.efy.batch.dto.ResultData;
import com.efy.batch.dto.RetCode;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Date 2019/12/6 19:41
 * @Author  by Efy
 * @Description TODO
 */
@Slf4j
@Component
public class BatchUtil{
    /**批量总数*/
    private int totalCount = 100;
    /**分片数量(并行计算数量)*/
    private int pieces = 50;
    /**分片线程池*/
    private ExecutorService piecesPool = null;
    private List<BatchJob> jobs = null;
    private BatchListener listener = null;

    public BatchUtil(){
        init(this.totalCount,this.pieces);
    }

    public BatchUtil(int totalCount,int pieces){
        this.totalCount = totalCount;
        this.pieces = pieces;
        init(totalCount,pieces);
    }
    /**
     * 分片计算函数
     * @param start
     * @param end
     * @throws InterruptedException
     */
    private void startPieces(int start,int end) throws InterruptedException {
        long pStartTime, pEndTime;
        pStartTime = System.currentTimeMillis();

        log.info("分片数据:" + start + "~" + end + "开始执行...");
        //开启cd
        CountDownLatch piecesCd = new CountDownLatch(end - start);
        for (int p = start; p < end; p++) {
            //模拟执行业务
            piecesPool.execute(() -> doSingleJob(piecesCd));
        }
        //等待cd
        piecesCd.await(60, TimeUnit.SECONDS);
        if (listener != null) {
            listener.piecesComplete(ResultData.ok(new int[]{start, end}));
        }
        log.info("分片数据:" + start + "~" + end + "执行结束.");

        pEndTime = System.currentTimeMillis();
        double cost = (pEndTime - pStartTime);
        log.info("当前分片:" + start + "~" + end + "执行时间为" + cost + "ms");
    }

    /**
     * 执行单笔任务
     * @param piecesCd
     */
    private void doSingleJob(CountDownLatch piecesCd){
        for(BatchJob job : jobs) {
            try {
                job.init();
                ResultData result = job.doJob();
                if(listener != null){
                    listener.singleComplete(result);
                }
            } catch (Exception e) {
                log.error("单笔任务执行出错:",e);
                if(listener != null){
                    listener.singleComplete(ResultData.failed(RetCode.CODE_ERROR,e.getMessage()));
                }
            }
        }
        piecesCd.countDown();
    }

    /**
     * 初始化批量任务
     * @param totalCount 批量数据总数
     * @param pieces     每次并行数
     */
    public void init(int totalCount,int pieces){
        this.totalCount = totalCount;
        this.pieces = pieces;
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().build();
        piecesPool = new ThreadPoolExecutor(pieces, pieces,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(pieces),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
        if(jobs == null){
            jobs = new ArrayList<>();
        }
    }

    /**
     * 注册单笔任务逻辑(累加)
     * @param job
     */
    public void addJobs(BatchJob job){
        if(jobs == null){
            jobs = new ArrayList<>();
        }
        jobs.add(job);
    }

    /**
     * 注册单笔任务逻辑(清除任务列表重新添加)
     * @param job
     */
    public void setJobs(BatchJob job){
        jobs = new ArrayList<>();
        jobs.add(job);
    }
    
    /**
     * 注册单笔任务完成后续处理逻辑
     * @param listener
     */
    public void setListener(BatchListener listener){
        this.listener = listener;
    }

    public void start(){
        if(jobs == null || jobs.isEmpty()){
            return;
        }
        long tStartTime,tEndTime;
        tStartTime = System.currentTimeMillis();

        log.info("本次批量任务总数为:" + totalCount);
        //已执行数量
        int execPieces = pieces;
        //分片运算
        for(int i=0;i<totalCount;i+=execPieces){
            int start = i;
            int end = i + execPieces;
            if(end >= totalCount){
                end = totalCount;
            }
            try {
                startPieces(start,end);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //关闭线程池
        piecesPool.shutdown();
        //通知结果
        if(listener != null){
            listener.allComplete(ResultData.ok());
        }

        tEndTime = System.currentTimeMillis();
        double cost = (tEndTime - tStartTime) / 1000;
        log.info("总计耗时:" + cost + "s");
    }

    public static void main(String[] args) {
        BatchUtil piecesTest = new BatchUtil();

        MavenUtil.build(null,false);
        String[] jars = MavenUtil.FILE_LIST.toString().split("\r\n");

        BatchMavenJob mavenUploadJob = new BatchMavenJob();
        MavenJobListener jobListener = new MavenJobListener();
        piecesTest.init(jars.length,5);
        piecesTest.addJobs(mavenUploadJob);
        piecesTest.setListener(jobListener);
        piecesTest.start();
    }
}


