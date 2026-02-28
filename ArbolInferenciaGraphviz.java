import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ArbolInferenciaGraphviz {
    private static int contadorNodos = 0;

    public static void mostrar(NodoArbol raiz) {
        if (raiz == null)
            return;

        System.out.println("\n--- Generando representación visual (Graphviz) ---");
        try (PrintWriter pw = new PrintWriter(new FileWriter("arbol.dot"))) {
            pw.println("digraph ArbolInferencia {");
            pw.println("node [shape=box, style=filled, color=lightblue];");
            Map<NodoArbol, String> ids = new HashMap<>();
            generarNodosYEdges(raiz, pw, ids);
            pw.println("}");
            System.out.println("Archivo 'arbol.dot' generado con éxito.");
            System.out.println("Para visualizarlo, instala graphviz y usa: dot -Tpng arbol.dot -o arbol.png");
        } catch (Exception e) {
            System.out.println("Error al generar el archivo .dot: " + e.getMessage());
        }
    }

    private static void generarNodosYEdges(NodoArbol nodo, PrintWriter pw, Map<NodoArbol, String> ids) {
        if (!ids.containsKey(nodo)) {
            String id = "nodo" + contadorNodos++;
            ids.put(nodo, id);

            String label = nodo.getDescripcion().replace("\"", "\\\"");
            String estilo = nodo.esRegla() ? "shape=ellipse, color=lightgreen" : "shape=box, color=lightblue";

            // Colorear puntos muertos y ciclos
            if (label.contains("Punto muerto") || label.contains("Ciclo detectado")) {
                estilo = "shape=box, color=salmon";
            }

            pw.println(id + " [label=\"" + label + "\", " + estilo + "];");
        }

        String idPadre = ids.get(nodo);
        for (NodoArbol hijo : nodo.getHijos()) {
            generarNodosYEdges(hijo, pw, ids);
            String idHijo = ids.get(hijo);
            pw.println(idPadre + " -> " + idHijo + ";");
        }
    }
}
