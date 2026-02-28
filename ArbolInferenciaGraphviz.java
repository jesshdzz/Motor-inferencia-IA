import java.io.*;

public class ArbolInferenciaGraphviz {

    private static int contador = 0;

    public static void mostrar(NodoArbol raiz) {
        try {
            String dot = generarDOT(raiz);

            File archivoDot = new File("arbol.dot");
            FileWriter fw = new FileWriter(archivoDot);
            fw.write(dot);
            fw.close();

            // generar imagen
            ProcessBuilder pb = new ProcessBuilder(
                    "dot", "-Tpng", "arbol.dot", "-o", "arbol.png");
            pb.start().waitFor();

            // abrir imagen automáticamente
            abrirImagen("arbol.png");

        } catch (Exception e) {
            System.out.println("Error generando árbol: " + e.getMessage());
        }
    }

    private static String generarDOT(NodoArbol raiz) {
        contador = 0;

        StringBuilder sb = new StringBuilder();

        sb.append("digraph Arbol {\n");
        sb.append("rankdir=TB;\n");
        sb.append("node [fontname=\"Arial\"];\n");

        recorrerNodo(raiz, sb, -1);

        sb.append("}");
        return sb.toString();
    }

    private static int recorrerNodo(NodoArbol nodo, StringBuilder sb, int padreId) {

        int idActual = contador++;
        String texto = nodo.getContenido().replace("\"", "");

        // estilo visual
        if (texto.contains("punto muerto")) {
            sb.append(idActual + " [label=\"" + texto + "\", shape=diamond, color=red];\n");
        }
        else if (nodo.isEsRegla()) {
            sb.append(idActual + " [label=\"" + texto + "\", shape=box];\n");
        }
        else {
            // hecho = doble círculo
            sb.append(idActual + " [label=\"" + texto + "\", shape=doublecircle];\n");
        }

        if (padreId != -1) {
            sb.append(padreId + " -> " + idActual + ";\n");
        }

        for (NodoArbol hijo : nodo.getHijos()) {
            recorrerNodo(hijo, sb, idActual);
        }

        return idActual;
    }

    private static void abrirImagen(String archivo) throws Exception {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(new File(archivo));
        }
    }
}