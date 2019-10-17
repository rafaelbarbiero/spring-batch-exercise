package br.com.rbarbiero.springbatchexercise.port.adapter.processor;

import br.com.rbarbiero.springbatchexercise.port.adapter.http.Input;
import br.com.rbarbiero.springbatchexercise.domain.Result;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ResultProcessor implements ItemProcessor<Input, Result> {

    @Override
    public Result process(final Input input) {
        return null;
    }
}