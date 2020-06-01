package com.efy.rule.enums;

import lombok.AllArgsConstructor;

/**
 * @Date 2020/3/5 15:23
 * @Author by Efy
 * @Description TODO
 */
@AllArgsConstructor
public enum FieldTypeEnum {
    LONG("0","long"),
    DOUBLE("1","double"),
    STRING("2","String"),


    ;


    public String code;
    public String desc;
}
