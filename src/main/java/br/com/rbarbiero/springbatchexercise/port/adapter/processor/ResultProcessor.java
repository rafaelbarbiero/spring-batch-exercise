package br.com.rbarbiero.springbatchexercise.port.adapter.processor;

import br.com.rbarbiero.springbatchexercise.domain.Input;
import br.com.rbarbiero.springbatchexercise.domain.Pair;
import br.com.rbarbiero.springbatchexercise.domain.Result;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ResultProcessor implements ItemProcessor<Input, Result> {

    @Override
    public Result process(final Input input) {

        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final int value = input.getValue();
        final int multiple = value / 17;
        final int rest = value % 17;
        final Pair pair = (value % 2) == 0 ? Pair.PAR : Pair.IMPAR;
        return Result.builder()
                .value(value)
                .multiple(multiple)
                .rest(rest)
                .pair(pair.name())
                .build();
    }
}