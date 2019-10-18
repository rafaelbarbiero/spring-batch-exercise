package br.com.rbarbiero.springbatchexercise.port.adapter.writer;

import br.com.rbarbiero.springbatchexercise.domain.Result;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WriterConfiguration {

    public FlatFileItemWriter<Result> writer(final File output) {
        final FlatFileItemWriter<Result> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(output));
        writer.setAppendAllowed(true);
        writer.setHeaderCallback(writer1 -> writer1.write("Número, Par/Impar, Múltiplo17, Resto17"));
        writer.setLineAggregator(new DelimitedLineAggregator<Result>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Result>() {
                    {
                        setNames(new String[]{"value", "pair", "multiple", "rest"});
                    }
                });
            }
        });
        return writer;
    }
}
