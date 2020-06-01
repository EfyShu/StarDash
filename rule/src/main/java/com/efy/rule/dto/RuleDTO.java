package com.efy.rule.dto;

import com.efy.rule.enums.FieldTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Project XinDai_POC
 * @Date 2019/1/8 18:44
 * @Author by Efy Shu
 * @Description TODO
 */
@Data
public class RuleDTO {
    /**ID*/
    @ApiModelProperty("ID")
    private String id;
    /**父级ID (评分卡模型用)*/
    @ApiModelProperty("父级ID (评分卡模型用)")
    private String pid;
    /**规则名*/
    @ApiModelProperty("规则名")
    private String ruleNbr;
    /**规则名*/
    @ApiModelProperty("规则名")
    private String ruleName;
    /**字段名*/
    @ApiModelProperty("字段名")
    private String fieldName;
    /**字段名(中文)*/
    @ApiModelProperty("字段名(中文)")
    private String fieldNameCN;
    /**字段类型(long,double,String)*/
    @ApiModelProperty("字段类型(long,double,String)")
    private String fieldType;
    /**规则分值 (评分卡模型用)*/
    @ApiModelProperty("规则分值 (评分卡模型用)")
    private Double score = 0.0;
    /**操作符(> , < , = , <= , >=,!=,(),!())*/
    @ApiModelProperty("操作符(> , < , = , <= , >=,!=,(),!())")
    private String operator;
    /**操作符(中文)*/
    @ApiModelProperty("操作符(中文)")
    private String operatorCN;
    /**比较值*/
    @ApiModelProperty("比较值")
    private String sourceValue;
    /**被比较值*/
    @ApiModelProperty("被比较值")
    private String targetValue;
    /**校验结果*/
    @ApiModelProperty("校验结果")
    private Boolean result = false;
    /**子项 (评分卡模型成功时进入子项)*/
    @ApiModelProperty("子项 (评分卡模型成功时进入子项)")
    private List<RuleDTO> children;

    public String getRuleName() {
        this.ruleName = getOperatorCN() + this.targetValue;
        return ruleName;
    }

    public String getOperatorCN() {
        this.operatorCN = ">".equals(this.operator) ? "大于" :
                          "<".equals(this.operator) ? "小于" :
                          "=".equals(this.operator) ? "等于" :
                          ">=".equals(this.operator) ? "大于等于" :
                          "<=".equals(this.operator) ? "小于等于" :
                          "!=".equals(this.operator) ? "不等于" :
                          "()".equals(this.operator) ? "包含于" :
                          "!()".equals(this.operator) ? "不包含于" : null;
        return operatorCN;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = FieldTypeEnum.LONG.code.equals(fieldType) ? FieldTypeEnum.LONG.desc :
                         FieldTypeEnum.DOUBLE.code.equals(fieldType) ? FieldTypeEnum.DOUBLE.desc :
                         FieldTypeEnum.STRING.code.equals(fieldType) ? FieldTypeEnum.STRING.desc : null;
    }
}
