package br.com.rbarbiero.springbatchexercise.port.adapter.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;

@Configuration
@EnableBatchProcessing
public class StepConfiguration {

    public Step step1(final InputStream fileInputStream, File output){
        return null;
    }
}
