package br.com.rbarbiero.springbatchexercise.application;

import br.com.rbarbiero.springbatchexercise.domain.Input;
import br.com.rbarbiero.springbatchexercise.domain.Processing;
import br.com.rbarbiero.springbatchexercise.port.adapter.listener.JobCompletionNotificationListener;
import br.com.rbarbiero.springbatchexercise.port.adapter.step.StepConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessApplicationService {

    private final JobBuilderFactory jobBuilderFactory;
    private final JobCompletionNotificationListener listener;
    private final StepConfiguration stepConfiguration;
    private final JobLauncher jobLauncher;
    private final Processing processing;

    ProcessApplicationService(JobBuilderFactory jobBuilderFactory, JobCompletionNotificationListener listener,
                              StepConfiguration stepConfiguration, JobLauncher jobLauncher, Processing processing) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.listener = listener;
        this.stepConfiguration = stepConfiguration;
        this.jobLauncher = jobLauncher;
        this.processing = processing;
    }

    public JobExecution process(MultipartFile file) throws IOException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        final File tempFile = this.createTempFile();
        final Job job = this.createJob(file.getInputStream(), tempFile);
        final JobParameters jobParameters = this.createJobParameters(tempFile.getName());
        return processing.process(job, jobParameters, jobLauncher);
    }

    public File getFile(final String id) {
        return null;

    }

    private JobParameters createJobParameters(final String fileName) {
        final Map parameters = new HashMap<String, String>();
        parameters.put("filename", new JobParameter(fileName));
        return new JobParameters(parameters);
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("output-", ".csv");
    }

    private Job createJob(final InputStream inputStream, File output) {
        final ItemReader<Input> reader = this.createReader(inputStream);
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepConfiguration.step1(output, reader))
                .end().build();
    }

    private ItemReader<Input> createReader(final InputStream inputStream) {
        return new FlatFileItemReaderBuilder<Input>()
                .name("inputStreamItemReader")
                .linesToSkip(1)
                .resource(new InputStreamResource(inputStream))
                .delimited()
                .names(new String[]{"value"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Input>() {{
                    setTargetType(Input.class);
                }})
                .build();
    }
}
