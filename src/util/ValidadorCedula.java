package util;

public final class ValidadorCedula {

    private ValidadorCedula() {
    }

    public static String normalizar(String cedulaCruda) {
        if (cedulaCruda == null || cedulaCruda.isBlank()) {
            throw new IllegalArgumentException("La cédula está vacía.");
        }

        String soloDigitos = cedulaCruda.replaceAll("[^0-9]", "");
        if (soloDigitos.isBlank()) {
            throw new IllegalArgumentException("La cédula debe contener dígitos.");
        }
 
        if (soloDigitos.length() > 9) {
            throw new IllegalArgumentException("La cédula no puede tener más de 9 dígitos.");
        }

        return String.format("%09d", Long.parseLong(soloDigitos));
    }
}
