package com.efy.rule.core;

import com.efy.rule.dto.ResultDTO;
import com.efy.rule.dto.RuleDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project Sunyard_RLS
 * @Date 2020/1/7 10:49
 * @Created by Efy
 * @Description TODO
 */
public class RuleEngineDemo {
    public static void main(String[] args) throws Exception {
        RuleEngine method = new RuleEngine();
        List<RuleDTO> rawRules = new ArrayList<>();
        RuleDTO age18 = new RuleDTO();
        RuleDTO age25 = new RuleDTO();
        RuleDTO age35 = new RuleDTO();
        RuleDTO name = new RuleDTO();
        RuleDTO sex = new RuleDTO();

        age18.setId("1");
        age18.setFieldName("age");
        age18.setFieldNameCN("年龄");
        age18.setFieldType("0");
        age18.setScore(10D);
        age18.setOperator("()");
        age18.setTargetValue("17|19|21|30");

        age25.setId("2");
        age25.setFieldName("age");
        age25.setFieldNameCN("年龄");
        age25.setFieldType("0");
        age25.setScore(20D);
        age25.setOperator("!()");
        age25.setTargetValue("25|18|19|21|30");

        age35.setId("3");
        age35.setFieldName("age");
        age35.setFieldNameCN("年龄");
        age35.setFieldType("0");
        age35.setScore(30D);
        age35.setOperator(">");
        age35.setTargetValue("35");

        sex.setId("4");
        sex.setPid(age18.getId());
        sex.setFieldName("sex");
        sex.setFieldNameCN("性别");
        sex.setFieldType("2");
        sex.setScore(15D);
        sex.setOperator("=");
        sex.setTargetValue("male");

        name.setId("5");
        name.setPid(sex.getId());
        name.setFieldName("name");
        name.setFieldNameCN("姓名");
        name.setFieldType("2");
        name.setScore(15D);
        name.setOperator("=");
        name.setTargetValue("Efy");


        rawRules.add(age18);
        rawRules.add(age25);
        rawRules.add(age35);
        rawRules.add(sex);
        rawRules.add(name);

        Map<String,String> user1 = new HashMap<>();
        Map<String,String> user2 = new HashMap<>();
        Map<String,String> user3 = new HashMap<>();
        user1.put("age","18");
        user1.put("sex","male");
        user1.put("name","Efy");

        user2.put("age","18");
        user2.put("sex","female");
        user2.put("name","Efy");

        user3.put("age","37");
        user3.put("sex","male");
        user3.put("name","Efy");

        ResultDTO score;
        long start,end;
        start = System.currentTimeMillis();
        System.out.println("用户1开始批卷...");
        score = method.start(user1,rawRules);
        System.out.println("用户1最终得分:"+score);
        end = System.currentTimeMillis();
        System.out.println("耗时:" + (end-start)/1000D + "s");

        start = System.currentTimeMillis();
        System.out.println("用户2开始批卷...");
        score = method.start(user2,rawRules);
        System.out.println("用户2最终得分:"+score);
        end = System.currentTimeMillis();
        System.out.println("耗时:" + (end-start)/1000D + "s");

        start = System.currentTimeMillis();
        System.out.println("用户3开始批卷...");
        score = method.start(user3,rawRules);
        System.out.println("用户3最终得分:"+score);
        end = System.currentTimeMillis();
        System.out.println("耗时:" + (end-start)/1000D + "s");
    }
}
