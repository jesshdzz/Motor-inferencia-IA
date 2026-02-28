import java.util.ArrayList;
import java.util.List;

public class Regla {
    private List<String> antecedentes;
    private String consecuente;

    public Regla(String linea) {
        antecedentes = new ArrayList<>();
        parsearRegla(linea);
    }

    private void parsearRegla(String linea) {
        if (!linea.contains("->"))
            throw new IllegalArgumentException("La regla debe tener formato: A ∧ B -> C");

        String[] partes = linea.split("->");

        if (partes.length != 2)
            throw new IllegalArgumentException("Formato inválido");

        if (partes[0].contains(","))
            throw new IllegalArgumentException("Use ∧ para conjunción, no coma");

        String[] ants = partes[0].split("∧");

        for (String a : ants) {
            String limpio = a.trim();
            if (!limpio.matches("(NO )?[a-zA-Z0-9_]+"))
                throw new IllegalArgumentException("Antecedente inválido: " + limpio);

            antecedentes.add(limpio);
        }

        String cons = partes[1].trim();
        if (!cons.matches("[a-zA-Z0-9_]+"))
            throw new IllegalArgumentException("Consecuente inválido");

        consecuente = cons;
    }

    public List<String> getAntecedentes() {
        return antecedentes;
    }

    public String getConsecuente() {
        return consecuente;
    }

    public String toString() {
        return "SI " + String.join(" Y ", antecedentes) + " ENTONCES " + consecuente;
    }
}