import java.util.ArrayList;
import java.util.List;

public class MotorInferencia {
    private final List<Regla> reglas;
    private final List<String> hechos;
    private NodoArbol raizArbol;
    private final InteraccionManual interactuador;

    public MotorInferencia(List<Regla> reglas, List<String> hechos, InteraccionManual interactuador) {
        this.reglas = reglas;
        this.hechos = hechos;
        this.interactuador = interactuador;
    }

    public List<String> getHechos() {
        return this.hechos;
    }

    public NodoArbol getArbolInferencia() {
        return this.raizArbol;
    }

    // --- ENCADENAMIENTO HACIA ADELANTE (Basado en version3) ---
    public List<String> encadenamientoHaciaAdelante() {
        List<String> nuevosHechos = new ArrayList<>();
        boolean huboCambios;
        int iteracion = 1;

        System.out.println("\n=== ENCADENAMIENTO HACIA ADELANTE ===");
        do {
            huboCambios = false;
            System.out.println("\nIteración " + iteracion + ":");

            for (Regla regla : reglas) {
                System.out.println(" Evaluando regla: " + regla);
                if (hechos.contains(regla.getConsecuente())) {
                    System.out.println("   -> No dispara (consecuente ya conocido).");
                    continue;
                }

                if (antecedentesCumplidos(regla)) {
                    hechos.add(regla.getConsecuente());
                    nuevosHechos.add(regla.getConsecuente());
                    huboCambios = true;
                    System.out.println("   -> ¡Dispara! Nuevo hecho inferido = " + regla.getConsecuente());
                } else {
                    System.out.println("   -> No dispara (faltan antecedentes).");
                }
            }
            iteracion++;
        } while (huboCambios);

        return nuevosHechos;
    }

    private boolean antecedentesCumplidos(Regla regla) {
        for (String antecedente : regla.getAntecedentes()) {
            if (esNegado(antecedente)) {
                String positivo = quitarNegacion(antecedente);
                if (hechos.contains(positivo)) {
                    return false;
                }
            } else if (!hechos.contains(antecedente)) {
                return false;
            }
        }
        return true;
    }

    // --- ENCADENAMIENTO HACIA ATRAS (Mezcla de version3 lógica y sonia-version
    // gráfica) ---
    public boolean encadenamientoHaciaAtras(String objetivo) {
        System.out.println("\n=== ENCADENAMIENTO HACIA ATRÁS ===");
        List<String> pila = new ArrayList<>();
        raizArbol = new NodoArbol(objetivo, false);

        System.out.println("Traza de demostración:");
        boolean demostrado = demostrarObjetivo(objetivo.trim(), pila, raizArbol, 0);

        if (demostrado) {
            System.out.println("\n>>> Objetivo demostrado: " + objetivo);
        } else {
            System.out.println("\n>>> No fue posible demostrar el objetivo: " + objetivo);
        }
        return demostrado;
    }

    private boolean demostrarObjetivo(String objetivo, List<String> pila, NodoArbol nodoPadre, int nivel) {
        String prefijo = "  ".repeat(nivel);
        System.out.println(prefijo + "Objetivo -> " + objetivo);

        if (objetivo.isEmpty()) {
            return false;
        }

        // Manejo de ciclos (Versión 3)
        if (pila.contains(objetivo)) {
            System.out.println(prefijo + "  Fallo: ciclo detectado (" + objetivo + ").");
            nodoPadre.agregarHijo(new NodoArbol("Ciclo detectado: " + objetivo, false));
            return false;
        }

        // Nodo actual para el árbol (sonia-version)
        NodoArbol nodoActual;
        // Si el padre ya es el objetivo raíz creado en encadenamientoHaciaAtras, usamos
        // ese.
        if (nodoPadre.getDescripcion().equals(objetivo) && nodoPadre.getHijos().isEmpty() && nivel == 0) {
            nodoActual = nodoPadre;
        } else {
            nodoActual = new NodoArbol(objetivo, false);
            nodoPadre.agregarHijo(nodoActual);
        }

        // Si es negado (Versión 3)
        if (esNegado(objetivo)) {
            String positivo = quitarNegacion(objetivo);
            System.out.println(prefijo + "  Es negado, intentar demostrar " + positivo + " y negar resultado.");
            boolean positivoEsVerdadero = demostrarObjetivo(positivo, new ArrayList<>(pila), nodoActual, nivel + 1);
            boolean resultado = !positivoEsVerdadero;

            if (!resultado) {
                // Si el hecho positivo (lo contrario) se demostró, entonces el negado falla.
                System.out.println(
                        prefijo + "  El hecho positivo '" + positivo + "' es cierto, por lo que el negado falla.");
                return false;
            } else {
                // Si no se demostró el positivo... es momento de preguntar si es un punto
                // muerto absoluto.
            }
        }

        // Si es un hecho conocido positivo
        if (hechos.contains(objetivo)) {
            System.out.println(prefijo + "  Hecho conocido: verdadero.");
            return true;
        }

        pila.add(objetivo);
        boolean existeReglaConObjetivo = false;

        for (Regla regla : reglas) {
            if (!regla.getConsecuente().equals(objetivo)) {
                continue;
            }
            existeReglaConObjetivo = true;
            System.out.println(prefijo + "  Evaluando regla: " + regla);

            NodoArbol nodoRegla = new NodoArbol(regla.toString(), true);
            nodoActual.agregarHijo(nodoRegla);

            boolean todosCumplidos = true;
            for (String antecedente : regla.getAntecedentes()) {
                boolean ok = demostrarAntecedente(antecedente, pila, nodoRegla, nivel + 1);
                if (!ok) {
                    System.out.println(prefijo + "  Regla falla por antecedente: " + antecedente);
                    todosCumplidos = false;
                    break;
                }
            }

            if (todosCumplidos) {
                if (!hechos.contains(objetivo)) {
                    hechos.add(objetivo);
                }
                System.out.println(prefijo + "  Regla satisfecha, se infiere: " + objetivo);
                pila.remove(pila.size() - 1);
                return true;
            }
        }

        pila.remove(pila.size() - 1);

        // Si no se puede deducir por ninguna regla o hecho -> Interactuar con el
        // usuario
        System.out.println(prefijo + "  Punto muerto para '" + objetivo + "'. Consultando al usuario...");

        boolean respuestaUsuario = interactuador.preguntarHechoAlUsuario(objetivo);
        if (respuestaUsuario) {
            hechos.add(objetivo);
            System.out.println(prefijo + "  Usuario confirma: " + objetivo + " es VERDADERO.");
            return true;
        } else {
            nodoActual.agregarHijo(new NodoArbol("Punto muerto (FALSO): " + objetivo, false));
            System.out.println(prefijo + "  Usuario confirma: " + objetivo + " es FALSO.");
            return false;
        }
    }

    private boolean demostrarAntecedente(String antecedente, List<String> pila, NodoArbol nodoPadre, int nivel) {
        String prefijo = "  ".repeat(nivel);
        System.out.println(prefijo + "Antecedente -> " + antecedente);

        if (esNegado(antecedente)) {
            String positivo = quitarNegacion(antecedente);
            // Creamos un nodo para la negación
            NodoArbol nodoNegado = new NodoArbol("NO " + positivo, false);
            nodoPadre.agregarHijo(nodoNegado);

            boolean positivoEsVerdadero = demostrarObjetivo(positivo, new ArrayList<>(pila), nodoNegado, nivel + 1);
            boolean resultado = !positivoEsVerdadero;

            System.out.println(prefijo + "Resultado antecedente negado " + antecedente + ": " + resultado);
            return resultado;
        }

        boolean resultado = demostrarObjetivo(antecedente, pila, nodoPadre, nivel + 1);
        System.out.println(prefijo + "Resultado antecedente " + antecedente + ": " + resultado);
        return resultado;
    }

    private boolean esNegado(String termino) {
        return termino.startsWith("!");
    }

    private String quitarNegacion(String termino) {
        if (esNegado(termino)) {
            return termino.substring(1).trim();
        }
        return termino;
    }
}
