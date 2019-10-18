package br.com.rbarbiero.springbatchexercise.application;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProcessApplicationService {

    final JobBuilderFactory jobBuilderFactory;
    final JobCompletionNotificationListener listener;
    final StepConfiguration stepConfiguration;
    final JobLauncher jobLauncher;

    public ProcessApplicationService(JobBuilderFactory jobBuilderFactory, JobCompletionNotificationListener listener,
                                     StepConfiguration stepConfiguration, JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.listener = listener;
        this.stepConfiguration = stepConfiguration;
        this.jobLauncher = jobLauncher;
    }

    public JobExecution process(MultipartFile file) throws IOException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        final File tempFile = this.createTempFile();
        final Job job = this.createJob(file.getInputStream(), tempFile);
        final JobParameters jobParameters = this.createJobParameters(tempFile.getName());
        return Processing.process(job, jobParameters, jobLauncher);
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
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepConfiguration.step1(inputStream, output))
                .end().build();
    }
}
