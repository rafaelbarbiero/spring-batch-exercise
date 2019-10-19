package br.com.rbarbiero.springbatchexercise.port.adapter.http;

import br.com.rbarbiero.springbatchexercise.application.ProcessApplicationService;
import br.com.rbarbiero.springbatchexercise.domain.exception.ProcessedFileNotFoundException;
import br.com.rbarbiero.springbatchexercise.port.adapter.http.controller.ApiExceptionHandler;
import br.com.rbarbiero.springbatchexercise.port.adapter.http.controller.ProcessController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProcessControllerTest {

    private MockMvc mockMvc;
    final ProcessApplicationService processApplicationService = Mockito.mock(ProcessApplicationService.class);

    @BeforeEach
    void setUp() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ProcessController(processApplicationService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar nome do recurso no header location")
    void create() throws Exception {
        final Map parameters = new HashMap<String, String>();
        parameters.put("uuid", new JobParameter("mockFileName.csv"));
        final JobParameters jobParameters = new JobParameters(parameters);
        Mockito.when(processApplicationService.process(any())).thenReturn(new JobExecution(1L, jobParameters));
        this.mockMvc.perform(post("/file")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/file/mockFileName.csv"));
    }

    @Test
    @DisplayName("Deve retornar arquivo processado no corpo da requisição")
    void output() throws Exception {
        Mockito.when(processApplicationService.getFile(any(UUID.class))).thenReturn(new File("mockFile.csv"));
        UUID uuid = UUID.randomUUID();
        this.mockMvc.perform(get(String.format("/file/%s", uuid)))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition", String.format("attachment; filename=%s.csv", uuid)))
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")));
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar o arquivo processado")
    void outputNotFound() throws Exception {
        Mockito.when(processApplicationService.getFile(any(UUID.class))).thenThrow(new ProcessedFileNotFoundException());
        this.mockMvc.perform(get(String.format("/file/%s", UUID.randomUUID()))).andExpect(status().isNotFound());
    }
}