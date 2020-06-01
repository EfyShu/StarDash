package com.efy.rule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project XinDai_POC
 * @Date 2019/1/14 3:17
 * @Author by Efy
 * @Description TODO
 */
@Data
public class ResultDTO implements Serializable {
    /**总分数*/
    @ApiModelProperty("总分数")
    private Double score = 0D;

    /**总体结果(命中情况,任意规则命中则为true,否则false)*/
    private Boolean result = false;

    /**详细内容*/
    @ApiModelProperty("详细内容")
    private List<String> details = new ArrayList<>();

    /**原子规则结果信息*/
    @ApiModelProperty("原子规则结果信息")
    private List<RuleDTO> ruleResult = new ArrayList<>();
}
