package br.com.rbarbiero.springbatchexercise.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private int value;
    private int multiple;
    private int rest;
    private Pair pair;
}