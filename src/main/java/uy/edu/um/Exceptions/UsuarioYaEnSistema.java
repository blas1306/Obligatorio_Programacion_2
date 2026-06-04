package uy.edu.um.Exceptions;

public class UsuarioYaEnSistema extends RuntimeException {
    public UsuarioYaEnSistema(String message) {
        super(message);
    }
}
