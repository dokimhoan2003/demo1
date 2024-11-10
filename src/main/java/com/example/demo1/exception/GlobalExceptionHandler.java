package com.example.demo1.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi 404 khi không tìm thấy đường dẫn
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundError(Model model) {
        model.addAttribute("error", "Trang bạn tìm kiếm không tồn tại!");
        return "homes/error";
    }

    // Xử lý các lỗi chung khác
    @ExceptionHandler(Exception.class)
    public String handleGlobalError(Model model, Exception ex) {
        model.addAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
        return "homes/error";
    }
}

