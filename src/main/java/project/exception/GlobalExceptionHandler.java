package project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.model.dto.response.ApiDataResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiDataResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiDataResponse<Map<String, String>> apiDataResponse = new ApiDataResponse<>(
                false,
                "Dữ liệu gửi lên không hợp lệ !",
                null,
                errors,
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(apiDataResponse , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiDataResponse<String>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiDataResponse<String> apiDataResponse = new ApiDataResponse<>(
                false,
                "Sai email hoặc password !",
                null,
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED
        );

        return new ResponseEntity<>(apiDataResponse , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiDataResponse<String>> handleBadRequestException(BadRequestException ex) {
        ApiDataResponse<String> apiDataResponse = new ApiDataResponse<>(
                false,
                ex.getMessage(),
                null,
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return new ResponseEntity<>(apiDataResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiDataResponse<String>> handleNotFoundException(NotFoundException ex) {
        ApiDataResponse<String> apiDataResponse = new ApiDataResponse<>(
                false,
                "Không tìm thấy dữ liệu !",
                null,
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );

        return new ResponseEntity<>(apiDataResponse, HttpStatus.NOT_FOUND);
    }

}
