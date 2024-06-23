package tools.descartes.teastore.utils;

public class EnvVarNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8568850075065336909L;

    public EnvVarNotFoundException(String variableName) {
        super("Environment variable not found: " + variableName);
    }
}