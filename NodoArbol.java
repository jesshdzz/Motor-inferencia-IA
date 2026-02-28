import java.util.*;

public class NodoArbol {
    private String contenido;      // hecho o regla aplicada
    private List<NodoArbol> hijos;
    private boolean esRegla;        // true si es un nodo de regla, false si es hecho

    public NodoArbol(String contenido, boolean esRegla) {
        this.contenido = contenido;
        this.esRegla = esRegla;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(NodoArbol hijo) {
        hijos.add(hijo);
    }

    public String getContenido() {
        return contenido;
    }

    public boolean isEsRegla() {
        return esRegla;
    }

    public List<NodoArbol> getHijos() {
        return hijos;
    }
}