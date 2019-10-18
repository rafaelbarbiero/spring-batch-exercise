package br.com.rbarbiero.springbatchexercise.port.adapter.http;

import br.com.rbarbiero.springbatchexercise.application.ProcessApplicationService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/")
class ProcessController {

    final ProcessApplicationService processApplicationService;

    ProcessController(ProcessApplicationService processApplicationService) {
        this.processApplicationService = processApplicationService;
    }

    @PostMapping("input")
    ResponseEntity<String> create(MultipartFile file) throws Exception {
        final JobExecution jobExecution = processApplicationService.process(file);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{filename}")
                .buildAndExpand(jobExecution.getJobParameters().getString("filename"))
                .toUri())
                .body(jobExecution.getStatus().toString());
    }

    @GetMapping("output/{id}")
    ResponseEntity<FileSystemResource> output(@PathVariable String id) {
        final File output = processApplicationService.getFile(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment; filename=%s.csv", id))
                .contentLength(output.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(output));
    }
}
