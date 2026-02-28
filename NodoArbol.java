import java.util.ArrayList;
import java.util.List;

public class NodoArbol {
    private String descripcion;
    private boolean esRegla;
    private List<NodoArbol> hijos;

    public NodoArbol(String descripcion, boolean esRegla) {
        this.descripcion = descripcion;
        this.esRegla = esRegla;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(NodoArbol hijo) {
        this.hijos.add(hijo);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esRegla() {
        return esRegla;
    }

    public List<NodoArbol> getHijos() {
        return hijos;
    }
}
