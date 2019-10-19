package br.com.rbarbiero.springbatchexercise.application;

import br.com.rbarbiero.springbatchexercise.domain.Input;
import br.com.rbarbiero.springbatchexercise.domain.Processing;
import br.com.rbarbiero.springbatchexercise.domain.exception.ProcessedFileNotFoundException;
import br.com.rbarbiero.springbatchexercise.port.adapter.job.JobConfiguration;
import br.com.rbarbiero.springbatchexercise.port.adapter.processor.ResultProcessor;
import br.com.rbarbiero.springbatchexercise.port.adapter.step.StepConfiguration;
import br.com.rbarbiero.springbatchexercise.port.adapter.writer.WriterConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static br.com.rbarbiero.springbatchexercise.application.ProcessApplicationServiceTestFixture.validJobParameters;
import static br.com.rbarbiero.springbatchexercise.application.ProcessApplicationServiceTestFixture.validMultipartFile;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProcessApplicationServiceTest {

    final JobBuilderFactory factory = mock(JobBuilderFactory.class);
    final JobConfiguration jobConfiguration = mock(JobConfiguration.class);
    final Processing processing = mock(Processing.class);
    final JobRepository jobRepository = mock(JobRepository.class);
    ProcessApplicationService applicationService;
    StepConfiguration stepConfiguration;

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @BeforeEach
    void setUp() {
        stepConfiguration = new StepConfiguration(
                new StepBuilderFactory(jobRepository, new ResourcelessTransactionManager()),
                new ResultProcessor(),
                new WriterConfiguration("headers", Arrays.asList("fields")));
        applicationService = new ProcessApplicationService(factory, stepConfiguration,
                jobConfiguration, processing, jobRepository);
    }

    @Test
    @DisplayName("Deve retornar Job Execution criado com sucesso")
    void process() throws Exception {
        when(factory.get(any())).thenReturn(new JobBuilder("job").repository(jobRepository));
        when(processing.process(any(), any(), any())).thenReturn(new JobExecution(1L, validJobParameters()));
        final JobExecution process = applicationService.process(validMultipartFile());
        Assertions.assertAll("Checking job execution properties", () -> {
            Assertions.assertNotNull(process.getJobParameters().getString("uuid"));
            Assertions.assertEquals("filename", process.getJobParameters().getString("uuid"));
            Assertions.assertEquals(BatchStatus.STARTING, process.getStatus());
        });
    }

    @Test
    @DisplayName("Deve retornar arquivo criado com sucesso")
    void getFile() {
        final UUID uuid = UUID.randomUUID();
        new File(String.format("%s/%s.%s", TEMP_DIR, uuid, "csv"));
        final JobExecution jobExecution = new JobExecution(1L, validJobParameters());
        jobExecution.setStatus(BatchStatus.COMPLETED);
        when(jobRepository.getLastJobExecution(any(), any())).thenReturn(jobExecution);
        final File file = applicationService.getFile(uuid);
        Assertions.assertAll("Checking file properties", () -> {
            Assertions.assertNotNull(file);
            Assertions.assertEquals(file.getName(), String.format("%s.csv", uuid.toString()));
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando processamento não estiver concluído")
    void getInvalidFile() {
        final UUID uuid = UUID.randomUUID();
        new File(String.format("%s/%s.%s", TEMP_DIR, uuid, "csv"));
        final JobExecution jobExecution = new JobExecution(1L, validJobParameters());
        jobExecution.setStatus(BatchStatus.STARTED);
        when(jobRepository.getLastJobExecution(any(), any())).thenReturn(jobExecution);
        Assertions.assertThrows(ProcessedFileNotFoundException.class, () -> applicationService.getFile(uuid));
    }

    @Test
    @DisplayName("Deve criar parametros com sucesso")
    void createJobParameters() {
        final UUID uuid = UUID.randomUUID();
        final JobParameters jobParameters = applicationService.createJobParameters(uuid.toString());
        Assertions.assertAll("Checking job parameters properties", () -> {
            Assertions.assertNotNull(jobParameters);
            Assertions.assertNotNull(jobParameters.getString("uuid"));
            Assertions.assertEquals(jobParameters.getString("uuid"), uuid.toString());
        });
    }

    @Test
    @DisplayName("Deve criar arquivo temporario com sucesso")
    void createTempFile() {
        final UUID uuid = UUID.randomUUID();
        final File tempFile = applicationService.createTempFile(uuid);
        Assertions.assertAll("Checking job parameters properties", () -> {
            Assertions.assertNotNull(tempFile);
            Assertions.assertEquals(tempFile.getName(), String.format("%s.csv", uuid.toString()));
        });
    }

    @Test
    @DisplayName("Deve criar Job com sucesso")
    void createJob() throws IOException {
        when(factory.get(any())).thenReturn(new JobBuilder("job").repository(jobRepository));
        final Job job = applicationService.createJob(validMultipartFile().getInputStream(),
                new File(""), UUID.randomUUID());
        Assertions.assertAll("Checking job parameters properties", () -> {
            Assertions.assertNotNull(job);
            Assertions.assertEquals("job", job.getName());
        });
    }

    @Test
    void createReader() throws IOException {
        final ItemReader<Input> reader = applicationService.createReader(validMultipartFile().getInputStream());
        Assertions.assertAll("Checking job parameters properties", () -> {
            Assertions.assertNotNull(reader);
            Assertions.assertTrue(reader instanceof FlatFileItemReader);
        });
    }
}