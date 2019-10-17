package br.com.rbarbiero.springbatchexercise.domain.exception;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ProcessedFileNotFoundException extends UncheckedIOException {

    public ProcessedFileNotFoundException(IOException cause) {
        super(cause);
    }
}
