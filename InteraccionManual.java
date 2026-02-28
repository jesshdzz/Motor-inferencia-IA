public interface InteraccionManual {
    /**
     * Pregunta al usuario si un hecho desconocido es verdadero o falso.
     * 
     * @param hecho El hecho a validar.
     * @return true si el usuario confirma que es verdadero, false si lo niega.
     */
    boolean preguntarHechoAlUsuario(String hecho);
}
