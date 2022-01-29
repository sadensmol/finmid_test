package me.sadensmol.finmid.controller

import me.sadensmol.finmid.domain.InvalidAmountValueException
import me.sadensmol.finmid.domain.NotEnoughAmountException
import me.sadensmol.finmid.domain.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
/**
 * todo: add error validators
 * and return error codes in response
 */
class ErrorHandler{
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNonValidRequest(): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e:NotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    @ExceptionHandler(InvalidAmountValueException::class,NotEnoughAmountException::class)
    fun handleWrongAmount(e:Exception): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}