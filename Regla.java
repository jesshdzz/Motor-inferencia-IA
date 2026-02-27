import java.util.ArrayList;
import java.util.List;

public class Regla {
    private final List<String> antecedentes;
    private String consecuente;

    public Regla(String linea) {
        this.antecedentes = new ArrayList<>();
        parsearRegla(linea);
    }

    private void parsearRegla(String linea) {
        String[] partes = linea.split("->");

        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de regla invalido: " + linea + ". Debe tener la forma: [antecedentes] -> [consecuente]\n");
        }

        if (partes[0].contains(",")) {
            throw new IllegalArgumentException("Formato de regla invalido: " + linea + ". Utilice '∧' en lugar de ',' para conectar antecedentes\n");
        }

        String[] antecedentes = partes[0].split("∧");
        for (String antecedente : antecedentes) {
            String antecedenteLimpio = normalizarAntecedente(antecedente);
            if (antecedenteLimpio.isEmpty()) {
                throw new IllegalArgumentException("Formato de regla invalido: " + linea + ". Los antecedentes no pueden estar vacios\n");
            }
            this.antecedentes.add(antecedenteLimpio);
        }
        this.consecuente = partes[1].trim();
        if (this.consecuente.isEmpty()) {
            throw new IllegalArgumentException("Formato de regla invalido: " + linea + ". El consecuente no puede estar vacio\n");
        }
    }

    private String normalizarAntecedente(String antecedente) {
        String limpio = antecedente.trim();
        if (limpio.toLowerCase().startsWith("no ")) {
            return "!" + limpio.substring(3).trim();
        }
        return limpio;
    }

    public List<String> getAntecedentes() {
        return antecedentes;
    }

    public String getConsecuente() {
        return consecuente;
    }

    @Override
    public String toString() {
        List<String> legibles = new ArrayList<>();
        for (String antecedente : antecedentes) {
            if (antecedente.startsWith("!")) {
                legibles.add("NO " + antecedente.substring(1));
            } else {
                legibles.add(antecedente);
            }
        }
        return "SI " + String.join(" Y ", legibles) + " ENTONCES " + consecuente;
    }

}
