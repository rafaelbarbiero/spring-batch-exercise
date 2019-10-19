package br.com.rbarbiero.springbatchexercise.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Input {
    private int value;
}