import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class VisorArbolInferencia extends JFrame {

    // Constructor que prepara la ventana interactiva y la vista visual inicial a
    // partir del árbol lógico de la demostración
    public VisorArbolInferencia(NodoArbol raizLogica) {
        setTitle("Árbol de Inferencia");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultMutableTreeNode raizVisual = crearNodoVisual(raizLogica);
        JTree arbol = new JTree(raizVisual);

        // Expandir por defecto
        for (int i = 0; i < arbol.getRowCount(); i++) {
            arbol.expandRow(i);
        }

        // Estilos
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setTextSelectionColor(Color.black);
        renderer.setBackgroundSelectionColor(new Color(184, 207, 229));
        arbol.setCellRenderer(renderer);

        JScrollPane scrollPane = new JScrollPane(arbol);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Convierte el nodo lógico de nuestra demostración y también le agrega un icono
    // visual lindo como ⧪, ✅, 🛑, o ❌
    private DefaultMutableTreeNode crearNodoVisual(NodoArbol nodoLogico) {
        if (nodoLogico == null)
            return new DefaultMutableTreeNode("Raíz Nula");

        String etiqueta = nodoLogico.getDescripcion();
        if (nodoLogico.esRegla()) {
            etiqueta = "⧪ [REGLA] " + etiqueta;
        } else {
            if (etiqueta.contains("Punto muerto") || etiqueta.contains("Ciclo detectado")) {
                etiqueta = "❌ " + etiqueta;
            } else if (etiqueta.startsWith("NO ")) {
                etiqueta = "🛑 " + etiqueta;
            } else {
                etiqueta = "✅ [OBJETIVO] " + etiqueta;
            }
        }

        DefaultMutableTreeNode nodoVisual = new DefaultMutableTreeNode(etiqueta);

        for (NodoArbol hijo : nodoLogico.getHijos()) {
            nodoVisual.add(crearNodoVisual(hijo));
        }

        return nodoVisual;
    }

    // Función estática auxiliar que lanza toda la ventana gráfica de forma segura
    // en un hilo separado de Swing
    public static void mostrar(NodoArbol raizLogica) {
        if (raizLogica == null) {
            System.out.println("No hay árbol que mostrar.");
            return;
        }

        System.out.println("\n--- Abriendo ventanta de Árbol de Inferencia ---");
        System.out.println("Por favor, revisa las ventanas de tu sistema.");

        SwingUtilities.invokeLater(() -> {
            VisorArbolInferencia visor = new VisorArbolInferencia(raizLogica);
            visor.setVisible(true);
        });
    }
}
