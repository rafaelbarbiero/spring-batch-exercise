package br.com.rbarbiero.springbatchexercise.application;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProcessApplicationServiceTestFixture {

    public static JobParameters validJobParameters() {
        final Map parameters = new HashMap<String, String>();
        parameters.put("uuid", new JobParameter("filename"));
        return new JobParameters(parameters);
    }

    static MultipartFile validMultipartFile() {
        return new MultipartFile() {
            @Override public String getName() {
                return "filename";
            }

            @Override public String getOriginalFilename() {
                return "filename";
            }

            @Override public String getContentType() {
                return null;
            }

            @Override public boolean isEmpty() {
                return false;
            }

            @Override public long getSize() {
                return 0;
            }

            @Override public byte[] getBytes() {
                return new byte[0];
            }

            @Override public InputStream getInputStream() {
                return new InputStream() {
                    @Override public int read() throws IOException {
                        return 0;
                    }
                };
            }

            @Override public void transferTo(File file) throws IllegalStateException {
            }
        };
    }
}
