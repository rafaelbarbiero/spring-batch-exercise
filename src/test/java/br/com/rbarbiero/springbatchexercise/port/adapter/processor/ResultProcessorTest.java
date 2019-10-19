package br.com.rbarbiero.springbatchexercise.port.adapter.processor;

import br.com.rbarbiero.springbatchexercise.domain.Input;
import br.com.rbarbiero.springbatchexercise.domain.Pair;
import br.com.rbarbiero.springbatchexercise.domain.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultProcessorTest {

    ResultProcessor resultProcessor = new ResultProcessor();

    @Test
    @DisplayName("Deve processar resultados para entrada ímpar")
    void processImpar() {
        final Input input = new Input(35);
        final Result result = new Result(35, 2, 1, Pair.IMPAR);
        final Result resultTest = resultProcessor.process(input);
        Assertions.assertAll("Checking result properties", () -> assertEquals(resultTest, result));
    }

    @Test
    @DisplayName("Deve processar resultados para entrada par")
    void processPar() {
        final Input input = new Input(34);
        final Result result = new Result(34, 2, 0, Pair.PAR);
        final Result resultTest = resultProcessor.process(input);
        Assertions.assertAll("Checking result properties", () -> assertEquals(resultTest, result));
    }
}