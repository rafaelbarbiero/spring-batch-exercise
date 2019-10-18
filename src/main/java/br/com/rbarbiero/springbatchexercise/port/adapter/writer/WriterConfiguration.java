package br.com.rbarbiero.springbatchexercise.port.adapter.writer;

import br.com.rbarbiero.springbatchexercise.domain.Result;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.stereotype.Component;

import java.io.File;

@Component public
class WriterConfiguration {

    public FlatFileItemWriter<Result> writer(final File output) {
        return null;
    }
}
