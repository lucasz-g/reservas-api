package br.com.garcia.reservas_api.exceptions;

public class ConflitoHorarioException extends RuntimeException {

    public ConflitoHorarioException(String message) {
        super(message);
    }
}
