package com.jy.crypto.system.infrastructure.web;

import com.jy.crypto.system.infrastructure.domain.WebResult;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public WebResult handleBusinessException(BusinessException e) {
        StringBuilder message = new StringBuilder("Business exception: [").append(e.getErrorCode().name()).append("] ")
                .append(e.getMessage());
        Throwable caseException = e.getCause();
        while (caseException != null) {
            message.append("\nCase by: ");
            if (caseException instanceof BusinessException caseBusinessException) {
                message.append("[").append(caseBusinessException.getErrorCode().name()).append("] ")
                        .append(caseBusinessException.getMessage());
            } else {
                StringWriter sw = new StringWriter();
                caseException.printStackTrace(new PrintWriter(sw, true));
                String exceptionStack = sw.toString();
                message.append(exceptionStack);
                break;
            }
            caseException = caseException.getCause();
        }
        log.warn(message.toString());
        return new WebResult(e.getErrorCode().name(), e.getMessage());
    }
}
