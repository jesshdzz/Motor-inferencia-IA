import java.util.ArrayList;
import java.util.List;

public class Regla {
    private List<String> antecedentes;
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
            if (antecedente.trim().isEmpty()) {
                throw new IllegalArgumentException("Formato de regla invalido: " + linea + ". Los antecedentes no pueden estar vacios\n");
            }
            this.antecedentes.add(antecedente.trim());
        }
        this.consecuente = partes[1].trim();
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
