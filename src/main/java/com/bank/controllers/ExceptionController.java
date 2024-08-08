package com.bank.controllers;

import com.bank.exceptions.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;

@Controller
public class ExceptionController implements ErrorController {
    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        var body = new HashMap<String, Object>();
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(status == null) {
            body.put("error", "Unknown Error");
            body.put("message", "An unknown error occurred.");
            body.put("timestamp", LocalDateTime.now());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        var statusCode = Integer.parseInt(status.toString());
        body.put("status", statusCode);

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            body.put("error", "Not Found");
            body.put("message", "The requested resource was not found.");
            body.put("timestamp", LocalDateTime.now());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            body.put("error", "Forbidden");
            body.put("message", "You don't have permission to access this resource.");
            body.put("timestamp", LocalDateTime.now());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            var throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            if (throwable != null && throwable.getCause() instanceof DomainException) {
                body.put("error", "Business Error");
                body.put("message", throwable.getCause().getMessage());
                body.put("timestamp", LocalDateTime.now());
            }
            else {
                body.put("error", "Internal Server Error");
                body.put("message", "An internal server error occurred.");
                body.put("timestamp", LocalDateTime.now());
            }
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            body.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
            body.put("message", "An unexpected error occurred.");
            body.put("timestamp", LocalDateTime.now());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}