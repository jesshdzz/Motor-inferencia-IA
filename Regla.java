import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Regla {
    private List<String> antecedentes;
    private String consecuente;

    public Regla(String linea) {
        String texto = linea;

        if (texto.contains("->")) {
            texto = texto.replace("->", "ENTONCES");
        }
        if (texto.contains("∧")) {
            texto = texto.replace("∧", "Y");
        }

        if (!texto.contains("ENTONCES")) {
            throw new IllegalArgumentException("Regla inválida, falta 'ENTONCES' o '->': " + linea);
        }

        String[] partes = texto.split("ENTONCES");
        if (partes.length < 2) {
            throw new IllegalArgumentException("Sintaxis incorrecta en: " + linea);
        }

        String parteAntecedentes = partes[0].replace("SI ", "").trim();
        this.consecuente = partes[1].trim();

        // Elimina comas y divide por Y
        this.antecedentes = new ArrayList<>(Arrays.asList(parteAntecedentes.replace(",", " ").split("\\s+Y\\s+")));

        for (int i = 0; i < antecedentes.size(); i++) {
            antecedentes.set(i, antecedentes.get(i).trim());
        }
    }

    public List<String> getAntecedentes() {
        return antecedentes;
    }

    public String getConsecuente() {
        return consecuente;
    }

    @Override
    public String toString() {
        return "SI " + String.join(" Y ", antecedentes) + " ENTONCES " + consecuente;
    }
}
