package com.bank.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class ExceptionController implements ErrorController {
    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        var body = new HashMap<String, Object>();
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (status != null) {
            var statusCode = Integer.parseInt(status.toString());
            body.put("status", statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                httpStatus = HttpStatus.NOT_FOUND;
                body.put("error", "Not Found");
                body.put("message", "The requested resource was not found.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                body.put("error", "Internal Server Error");
                body.put("message", "An internal server error occurred.");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                httpStatus = HttpStatus.FORBIDDEN;
                body.put("error", "Forbidden");
                body.put("message", "You don't have permission to access this resource.");
            } else {
                httpStatus = HttpStatus.valueOf(statusCode);
                body.put("error", httpStatus.getReasonPhrase());
                body.put("message", "An unexpected error occurred.");
            }
        }

        return new ResponseEntity<>(body, httpStatus);
    }
}