package br.com.rbarbiero.springbatchexercise.port.adapter.step;

import br.com.rbarbiero.springbatchexercise.domain.Input;
import br.com.rbarbiero.springbatchexercise.domain.Result;
import br.com.rbarbiero.springbatchexercise.port.adapter.processor.ResultProcessor;
import br.com.rbarbiero.springbatchexercise.port.adapter.writer.WriterConfiguration;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;

@Configuration
@EnableBatchProcessing
public class StepConfiguration {

    final StepBuilderFactory stepBuilderFactory;
    final ResultProcessor resultProcessor;
    final WriterConfiguration writer;

    public StepConfiguration(StepBuilderFactory stepBuilderFactory, ResultProcessor resultProcessor, WriterConfiguration writer) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.resultProcessor = resultProcessor;
        this.writer = writer;
    }

    Step step1(final InputStream fileInputStream, File output) {
        return stepBuilderFactory.get("step1")
                .<Input, Result>chunk(10)
                .reader(null)
                .processor(resultProcessor)
                .writer(writer.writer(output))
                .build();
    }
}
