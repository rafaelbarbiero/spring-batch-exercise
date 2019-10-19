package br.com.rbarbiero.springbatchexercise.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static br.com.rbarbiero.springbatchexercise.application.ProcessApplicationServiceTestFixture.validJobParameters;

class ProcessingTest {

    Processing processing;
    JobLauncher jobLauncher;

    @BeforeEach
    void setUp() {
        processing = new Processing();
        jobLauncher = Mockito.mock(JobLauncher.class);
    }

    @Test
    @DisplayName("Deve processar novo Job com as propriedades corretas")
    void process() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        final Job job = new SimpleJob();
        final ArgumentCaptor<Job> jobCapture = ArgumentCaptor.forClass(Job.class);
        final ArgumentCaptor<JobParameters> jobParametersCapture = ArgumentCaptor.forClass(JobParameters.class);
        processing.process(job, validJobParameters(), jobLauncher);
        Mockito.verify(jobLauncher).run(jobCapture.capture(), jobParametersCapture.capture());
        Assertions.assertAll("Checking captured properties", () -> {
            Assertions.assertNotNull(jobCapture.getValue());
            Assertions.assertNotNull(jobParametersCapture.getValue());
            Assertions.assertNotNull(jobParametersCapture.getValue().getString("uuid"));
            Assertions.assertEquals("filename", jobParametersCapture.getValue().getString("uuid"));
        });
    }
}