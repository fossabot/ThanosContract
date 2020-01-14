package com.thanos.contract.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    private String name;
    private String version;
    private String schemaIndex;
    private String consumer;
    private String provider;

    private LinkedList<ContractField> req;
    private LinkedList<ContractField> res;

    public String getIndex() {
        return provider + '-' + consumer;
    }

}
