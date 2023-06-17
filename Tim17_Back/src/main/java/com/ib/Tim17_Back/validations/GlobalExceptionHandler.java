package com.ib.Tim17_Back.validations;


import com.ib.Tim17_Back.exceptions.*;
import com.ib.Tim17_Back.models.ErrorResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.net.UnknownHostException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(AuthenticationException ex)
    {
        return new ErrorResponseMessage("Wrong username or password!");
    }

    @ExceptionHandler(value
            = UnknownHostException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponseMessage
    handleException(UnknownHostException ex)
    {
        return new ErrorResponseMessage("No internet connection");
    }

    @ExceptionHandler(value
            = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(BadCredentialsException ex)
    {
        return new ErrorResponseMessage("Wrong username or password!");
    }

    @ExceptionHandler(value
            = CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(CustomException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = CertificateNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(CertificateNotFoundException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = FieldCannotBeEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(FieldCannotBeEmptyException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidCertificateType.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(InvalidCertificateType ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = StatusNotPendingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(StatusNotPendingException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(UserNotFoundException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(UsernameNotFoundException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidCredentials.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(InvalidCredentials ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = ForbiddenActionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(ForbiddenActionException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

    @ExceptionHandler(value
            = InvalidRecaptchaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage
    handleException(InvalidRecaptchaException ex)
    {
        return new ErrorResponseMessage(ex.getMessage());
    }

}