package com.thanos.contract.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ContractField {

    private String name;
    private String content;

    public ContractField(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
