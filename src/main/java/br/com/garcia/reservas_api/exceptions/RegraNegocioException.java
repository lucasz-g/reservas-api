package br.com.garcia.reservas_api.exceptions;

public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String message) {
        super(message);
    }
}
