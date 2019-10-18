package br.com.rbarbiero.springbatchexercise.port.adapter.writer;

import br.com.rbarbiero.springbatchexercise.domain.Result;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class WriterConfiguration {

    final String header;
    final List<String> fields;

    public WriterConfiguration(@Value("${app.config.sheet.header}") String header,
                               @Value("${app.extractor.fields}") List<String> fields) {
        this.header = header;
        this.fields = fields;
    }

    public FlatFileItemWriter<Result> writer(final File output) {
        final FlatFileItemWriter<Result> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(output));
        writer.setAppendAllowed(true);
        writer.setHeaderCallback(writer1 -> writer1.write(header));
        writer.setLineAggregator(this.lineAggregator());
        return writer;
    }

    private LineAggregator<Result> lineAggregator() {
        final DelimitedLineAggregator<Result> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(this.fieldExtractor());
        return delimitedLineAggregator;
    }

    private FieldExtractor<Result> fieldExtractor() {
        final BeanWrapperFieldExtractor beanWrapperFieldExtractor = new BeanWrapperFieldExtractor();
        beanWrapperFieldExtractor.setNames(fields.toArray(new String[0]));
        return beanWrapperFieldExtractor;
    }
}
