package br.com.rbarbiero.springbatchexercise.port.adapter.http;

import br.com.rbarbiero.springbatchexercise.application.ProcessApplicationService;
import br.com.rbarbiero.springbatchexercise.domain.exception.ProcessedFileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        Mockito.when(processApplicationService.process(any())).thenReturn("idMockFile.csv");
        this.mockMvc.perform(post("/input")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/input/idMockFile.csv"));
    }

    @Test
    @DisplayName("Deve retornar arquivo processado no corpo da requisição")
    void output() throws Exception {
        Mockito.when(processApplicationService.getFile(anyString())).thenReturn(new File("mockFile.csv"));
        this.mockMvc.perform(get("/output/mockFile"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition", "attachment; filename=mockFile.csv"))
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")));
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar o arquivo processado")
    void outputNotFound() throws Exception {
        Mockito.when(processApplicationService.getFile(anyString())).thenThrow(new ProcessedFileNotFoundException(new IOException("mockFile")));
        this.mockMvc.perform(get("/output/mockFile")).andExpect(status().isNotFound());
    }
}