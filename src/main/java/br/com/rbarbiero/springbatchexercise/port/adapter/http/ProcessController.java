package br.com.rbarbiero.springbatchexercise.port.adapter.http;

import br.com.rbarbiero.springbatchexercise.application.ProcessApplicationService;
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

@RestController
@RequestMapping("/")
class ProcessController {

    final ProcessApplicationService processApplicationService;

    ProcessController(ProcessApplicationService processApplicationService) {
        this.processApplicationService = processApplicationService;
    }

    @PostMapping("input")
    ResponseEntity<Void> create(MultipartFile file) {
        final String filename = processApplicationService.process(file);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{filename}")
                .buildAndExpand(filename)
                .toUri())
                .build();
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
