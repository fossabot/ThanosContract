# ThanosContract 灭霸契约

[![Travis Build Status](https://travis-ci.org/abigail830/ThanosContract.svg?branch=master)](https://travis-ci.org/abigail830/ThanosContract)
[![Jitpack Statuc](https://jitpack.io/v/abigail830/thanoscontract.svg)](https://jitpack.io/#abigail830/thanoscontract)
[![Maintainability](https://api.codeclimate.com/v1/badges/9a15dac7b088a848522f/maintainability)](https://codeclimate.com/github/abigail830/ThanosContract/maintainability)
[![codecov](https://codecov.io/gh/abigail830/ThanosContract/branch/master/graph/badge.svg)](https://codecov.io/gh/abigail830/ThanosContract)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fabigail830%2FThanosContract.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fabigail830%2FThanosContract?ref=badge_shield)

# Vision 愿景

ThanosContract is target to help the systems which still using TCP & Fix-length message to implement Contract Test. 灭霸契约旨在为还在使用TCP+定长报文的系统提供契约测试工具，核心功能包括：

* Provide MockServer for consumer: 为消费者提供挡板服务
* Provider Junit Test Sample for provider: 为生产者提供自动化测试案例

### Background 背景
在内外部系统依赖关系比较复杂的场景下，常会遇到：
* 并行开发困难，经常需要等待联调测试，往往到端到端测试阶段才发现问题。因为发现问题的时机太晚，重复翻修导致效率低。
* 因为端到端测试经常发现问题，于是添加的大量端到端测试集合，从测试的编写、维护到准备大量的测试数据，投入非常大，然而后续运行不稳定，需要大量人工介入。

![常见流程](background.jpg "常见流程")


### Taget 目标流程

契约测试让生产者与消费者之间能基于契约而更高效也更高质量地并行开发，减少端到端联调的压力。在依赖关系复杂的系统间尤为重要。它目标是在开发初期就协定契约，并在整个开发测试过程中以同一份契约规约双方，测试驱动从而保证质量，减轻端到端的压力。

![目标流程](target.jpg "目标流程")


### Difference 差异化
Different from **SpringCloudContract** & **Pact**, ThanosContract support(& only support) TCP + fix-length messaging.

现有成熟的契约测试框架SpringCloudContract和Pact提供了基于HTTP+JSON格式的契约测试框架，但现实仍有不少的系统（如银行核心系统）依然徘徊在TCP+定长报文的场景之中，无法使用契约测试的概念去提高质量和减轻对端到端测试的压力。而灭霸契约则提供了TCP+定长报文的契约测试支持。

# 契约DSL

跟JSON的契约定义不同，定长报文的DSL分为两部分：
* **Schema：** 基础接口定义，如字段类型，长度，内容限制
* **Contract：** 具体场景下，面向一对生产者-消费者之间Request和Response的消息体具体内容

**注：** 编写契约有一定门槛，这跟其他契约测试框架遇到的问题是一样的，但后续完全可以通过UI等的方式去降低编写门槛，如使用更简易的CSV输入Schema，正则也是可以把常用正则更好的提前封装

## Schema 接口
* Content内容可以是某些固定值，也可以是正则（regex开头），将来还会支持定制化FUNC
* 下面所有都是必填， request/response里面是列表
* Field列表里面固定4个元素name/type/length/content (方便后续对接UI的表格）
* Schema必须把接口里面的所有field完整定义好

**FileBase Format**
``` yaml
name: 'schema1'
version: 'v1'
provider: 'provider'
request:
  - name: MSG_TYPE
    type: CHAR
    length: 1
    content: 'regex(0|1)'
response:
  - name: SUPPLEMENT
    type: CHAR
    length: 10
    content: 'regex([a-zA-Z0-9]{10})'
```
**JSON Format**
``` Java
public class SchemaDTO {
    private String provider;
    private String version;
    private String name;
    private LinkedList<SchemaFieldDTO> request;
    private LinkedList<SchemaFieldDTO> response;
}

public class SchemaFieldDTO {
    String name;
    String type;
    Integer length;
    String content;
}
```

## Contract 契约

* 契约里面不需要把全部field都填上，只需要写出该场景下关键的field就可以了，其他可以随意内容的参数可以忽略
* 后续在匹配时候会按照契约优先排序进行匹配，req部分定义了越多的field优先级就越高，优先匹配

**FileBase Format**
``` yaml
name: 'contract1'
consumer: 'consumer1'
version: 'v2'
schema:
  name: 'schema1'
  version: 'v1'
  provider: 'provider'
req:
  MSG_TYPE: '0'
  TRAN_ASYNC: '0'
  MESSAGE_TYPE: 'schema1'
  TCODE: '123456'
  ACCT_NO: '12345678901234567'
  CHOICE: '1'
res:
  SUPPLEMENT: 'SUPPLEMENT'
  MEMO: 'This is the expected memo for choice 1  '
```

**JSON Format**
``` Java
public class ContractDTO {
    private String name;
    private String version;
    private String consumer;
    private String provider;
    private String schemaIndex;
    private LinkedList<ContractFieldDTO> req;
    private LinkedList<ContractFieldDTO> res;
}

public class ContractFieldDTO {
    private String name;
    private String content;
}
```


# Core Components 核心组件

### MockServer 挡板服务
Detail please reference: [README](MockServer/README.md)
* [x] STANDALONE模式：能根据路径内的契约文件自动生成对应的挡板服务
* [x] PLATFORM模式：能从ContractService中获取最新契约及更新，动态维护挡板服务

### CodeGenerator 代码生成引擎
* [ ] STANDALONE模式：根据指定路径契约文件自动生成对应的生产者测试案例，可按需复制黏贴到项目工程代码中
* [ ] PLUGIN模式(?)：插件根据生产者项目工程路径内的契约文件自动生成对应的生产者测试案例，可直接修改运行
* [ ] PLATFORM模式：能从ContractService中获取最新契约及更新，动态生成对应的生产者测试案例


### PLATFORM mode draft diagram 设计草图
<img src="https://plantuml-server.kkeisuke.app/svg/ZLFDZjem4BxxAQOSAbRgagwqfvwsZQfjLuL56r2FQ716nAfZH_O4XAg-UyU19G5QjRxOutn_aqaU8cFWjhQGtwy-Mwh9SLHlZDEsBQaD5Yhc9dx8ie2xoSPmrQxRI45xnmATQ5zdBqPZAFtCK4Cso-zlvnOTBwVVia2MC59sT_jUxbUvaD0u2fD679MQKuZUZPFUEnWjPHl9BgZOrl88ti8y-AqfvJowKUf47tE5piVbLq4lZiV4dE9-JV2nCO_8jCCzO8GTMIladfkI1zHSKDWQJGV3uKrIUi8j7XIDOtYbf4HHLrHZbwVOHwPwzScthiIm7-kxKvxEJZFaFj3e1dugaDLdgBt-aTowlgUdujgv1m9gxKDft0ROdnA9jgmBTUrBUYGv8VlGzJUeXnx7IuAdrXc60kl6qj-Cr2-bNiWUfpMOm9V5Ojwf5i-pz5fwq5cRYxn3RGt24Pc0lSxrN2w4B-7dJLw3LpCocshjXv_BMQ8_1RzASSRuJkj2Qk94ym6UkPAf7Q8DWV_LoqK7_-Ie1tAb_3U_0G00.svg" width="550" >


## License
ThanosContract is under [MIT](LICENSE) license



[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fabigail830%2FThanosContract.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fabigail830%2FThanosContract?ref=badge_large)