package br.com.rbarbiero.springbatchexercise.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {

    private int value;
    private int multiple;
    private int rest;
    private String pair;
}