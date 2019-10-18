package br.com.rbarbiero.springbatchexercise.application;

import br.com.rbarbiero.springbatchexercise.domain.Input;
import br.com.rbarbiero.springbatchexercise.domain.Processing;
import br.com.rbarbiero.springbatchexercise.domain.exception.ProcessedFileNotFoundException;
import br.com.rbarbiero.springbatchexercise.port.adapter.job.JobConfiguration;
import br.com.rbarbiero.springbatchexercise.port.adapter.step.StepConfiguration;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProcessApplicationService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private final JobBuilderFactory jobBuilderFactory;
    private final StepConfiguration stepConfiguration;
    private final JobConfiguration jobConfiguration;
    private final Processing processing;
    private final JobRepository jobRepository;

    ProcessApplicationService(JobBuilderFactory jobBuilderFactory,
                              StepConfiguration stepConfiguration, JobConfiguration jobConfiguration, Processing processing, JobRepository jobRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepConfiguration = stepConfiguration;
        this.jobConfiguration = jobConfiguration;
        this.processing = processing;
        this.jobRepository = jobRepository;
    }

    public JobExecution process(MultipartFile file) throws Exception {
        final UUID uuid = UUID.randomUUID();
        final File tempFile = this.createTempFile(uuid);
        final Job job = this.createJob(file.getInputStream(), tempFile, uuid);
        final JobParameters jobParameters = this.createJobParameters(uuid.toString());
        return processing.process(job, jobParameters, jobConfiguration.simpleJobLauncher());
    }

    public File getFile(final UUID id) {
        final Map parameters = new HashMap<String, String>();
        parameters.put("uuid", new JobParameter(id.toString()));
        final JobExecution lastJobExecution = jobRepository.getLastJobExecution(id.toString(), new JobParameters(parameters));
        return Optional.ofNullable(lastJobExecution)
                .map(JobExecution::getStatus)
                .filter(BatchStatus.COMPLETED::equals)
                .map(value -> new File(String.format("%s/%s.%s", TEMP_DIR, id, "csv")))
                .orElseThrow(ProcessedFileNotFoundException::new);
    }

    JobParameters createJobParameters(final String uuid) {
        final Map parameters = new HashMap<String, String>();
        parameters.put("uuid", new JobParameter(uuid));
        return new JobParameters(parameters);
    }

    File createTempFile(UUID uuid) {
        return new File(String.format("%s/%s.%s", TEMP_DIR, uuid, "csv"));
    }

    Job createJob(final InputStream inputStream, File output, final UUID uuid) {
        final ItemReader<Input> reader = this.createReader(inputStream);
        return jobBuilderFactory.get(uuid.toString())
                .incrementer(new RunIdIncrementer())
                .flow(stepConfiguration.step1(output, reader))
                .end().build();
    }

    ItemReader<Input> createReader(final InputStream inputStream) {
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
