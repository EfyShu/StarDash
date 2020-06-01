package com.efy.rule.core;


import com.efy.rule.dto.ResultDTO;
import com.efy.rule.dto.RuleDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Project XinDai_POC
 * @Date 2019/1/8 14:39
 * @Author by Efy Shu
 * @Description 规则原始方法,包含大于,小于,等于,大于等于,小于等于
 */
@Slf4j
public class RuleEngine {
    /**是否为链表模式,false-评分卡模式,true-规则集模式*/
    private boolean listMode = true;
    /**是否简单模式(true-命中任意规则返回,false-遍历全部原子规则(无论是否命中))*/
    private boolean simpleMode = false;

    /**
     * 比对方法
     * @param source 比较值(动态,接口传递)
     * @param target 被比较值(静态,配置中或数据库读取)
     * @param type 字段类型
     * @param operator 操作符
     * @return
     */
    private boolean compair(String source, String target,String type,String operator) throws Exception{
        if(type == null){
            return false;
        }
        if("()".equals(operator)){
            if(source == null || target == null) {
                return false;
            }
            if(target.contains("|")){
                String[] tList = target.split("\\|");
                for(String tStr : tList){
                    if(tStr.equals(source)){
                        return true;
                    }
                }
                return false;
            }else{
                return target.equals(source);
            }
        }else if("!()".equals(operator)){
            if(source == null || target == null) {
                return false;
            }
            if(target.contains("|")){
                String[] tList = target.split("\\|");
                for(String tStr : tList){
                    if(tStr.equals(source)){
                        return false;
                    }
                }
                return true;
            }else{
                return !target.equals(source);
            }
        }else if(type.matches("string|String")){
            if(source == null || target == null) {
                return false;
            }
            return "=".equals(operator) ? source.equals(target) :
                   "!=".equals(operator) && !source.equals(target);
        }else{
            BigDecimal bSource = toNumber(source,type);
            BigDecimal bTarget = toNumber(target,type);
            if(bSource == null || bTarget == null) {
                return false;
            }
            int result = bSource.compareTo(bTarget);
            return "=".equals(operator) ? result == 0 :
                   "<".equals(operator) ? result < 0 :
                   ">".equals(operator) ? result > 0 :
                   "<=".equals(operator) ? result < 0 || result == 0 :
                   ">=".equals(operator) ? result > 0 || result == 0 :
                   "!=".equals(operator) && result != 0;
        }
    }

    /**
     * 字符转数字
     * @param source
     * @param type
     * @return
     */
    private BigDecimal toNumber(String source,String type) throws Exception{
        try {
            if(type.matches("long|Long")){
                BigDecimal lSource = BigDecimal.valueOf(Long.valueOf(source));
                return lSource;
            }else if(type.matches("double|Double")){
                BigDecimal dSource = BigDecimal.valueOf(Double.valueOf(source));
                return dSource;
            }
        }catch (Exception e){
            log.error("业务数据内容:{}|数据类型:{}|字符转换时发生异常!{}",source,type,e.getMessage(),e);
        }
        return null;
    }

    /**
     * 生成规则树
     * @param source 校验数据源
     * @param ruleList 规则列表(数组形态)
     * @return 规则列表(树形态)
     */
    private List<RuleDTO> buildTree(Map<String,String> source, List<RuleDTO> ruleList){
        List<RuleDTO> ruleTree = new ArrayList<>();
        for (RuleDTO m1 : ruleList) {
            if(null == m1.getSourceValue() || "".equals(m1.getSourceValue())){
                m1.setSourceValue(source.get(m1.getFieldName()));
            }
            //链表模式直接添加,不存在父子级结构
            if (listMode || m1.getPid() == null) {
                ruleTree.add(m1);
            }
            //链表模式不获取子项
            if(listMode){
                continue;
            }
            //获取子项
            List<RuleDTO> childs = new ArrayList<>();
            for (RuleDTO m2 : ruleList) {
                //不遍历自身和非子项
                if(m2.getId().equals(m1.getId()) || !m1.getId().equals(m2.getPid())) {
                    continue;
                }
                m2.setSourceValue(source.get(m2.getFieldName()));
                childs.add(m2);
            }
            m1.setChildren(childs);
        }
        return ruleTree;
    }

    /**
     * 批卷算法(正确时打上标记,并进入该分支下一规则)
     * (递归算法实现,超过50层时建议优化)
     * @param ruleTree
     */
    private void marking(List<RuleDTO> ruleTree) throws Exception{
        for(RuleDTO dto : ruleTree){
            boolean result;
            result = compair(dto.getSourceValue(),dto.getTargetValue(),dto.getFieldType(),dto.getOperator());
            dto.setResult(result);
            log.debug("【决策平台】【规则引擎】规则运算结果：{}",dto);
            //链表模式命中规则时中断遍历
            if(listMode){
                if(result && simpleMode){
                    break;
                }else{
                    continue;
                }
            }
            //成功则进入子项比较
            if(result && dto.getChildren() != null) {
                marking(dto.getChildren());
                //其中一条成功则代表其他支线不再有效
                break;
            }
        }
    }

    /**
     * 计算得分
     * (递归算法实现,超过50层时建议优化)
     * @param ruleTree
     * @return
     */
    private ResultDTO sumScore(ResultDTO result, List<RuleDTO> ruleTree){
        if(ruleTree == null) {
            return result;
        }
        for(RuleDTO temp : ruleTree){
            result.getRuleResult().add(temp);
            if(!temp.getResult()) {
                continue;
            }
            double score = result.getScore() + temp.getScore();
            result.setScore(score);
            result.getDetails().add(print(temp));
            //任意一条原子规则命中,则总体结果为命中
            if(temp.getResult()){
                result.setResult(true);
            }
            //链表模式继续计算得分
            if(listMode){
                continue;
            }
            //如果没有子规则,则直接中断计算
            if(temp.getChildren() == null || temp.getChildren().isEmpty()) {
                break;
            }
            result = sumScore(result,temp.getChildren());
            //其中一条成功则代表其他支线不再有效
            break;
        }
        return result;
    }

    /**
     * 引擎启动按钮
     * @param data 校验数据源（key-字段名，value-字段值）
     * @param ruleList 规则列表(数组形态)
     * @return
     */
    public ResultDTO start(Map<String,String> data, List<RuleDTO> ruleList) throws Exception{
        List<RuleDTO> ruleTree = buildTree(data,ruleList);
        marking(ruleTree);
        return sumScore(new ResultDTO(),ruleTree);
    }

    /**
     * 打印输出格式化
     * @param temp
     * @return
     */
    private String print(RuleDTO temp){
        String format = "字段[%s-%s]命中[%s]规则: %s %s %s 得分: %s";
        if(listMode){
            format = "字段[%s-%s]命中[%s]规则: %s %s %s";
        }
        String detail = String.format(format,temp.getFieldName(),temp.getFieldNameCN(),
                temp.getRuleName(),temp.getSourceValue(),temp.getOperatorCN(),temp.getTargetValue(),temp.getScore());
        return detail;
    }

    public void setListMode(boolean listMode){
        this.listMode = listMode;
    }
}
