package sacm.com.mx.compositores.common.dtos.Sacm_pkg_Compras;

public class CarritoDto {
    public CarritoDto() {
        super();
    }
    private Integer id_usuario;
    private Integer id_carrito;
    private Integer id_carrito_detalle;
    private Integer id_obra;
    private Integer numero_obra;
    private String titulo_obra;

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getId_carrito() {
        return id_carrito;
    }

    public void setId_carrito(Integer id_carrito) {
        this.id_carrito = id_carrito;
    }

    public Integer getId_carrito_detalle() {
        return id_carrito_detalle;
    }

    public void setId_carrito_detalle(Integer id_carrito_detalle) {
        this.id_carrito_detalle = id_carrito_detalle;
    }

    public Integer getId_obra() {
        return id_obra;
    }

    public void setId_obra(Integer id_obra) {
        this.id_obra = id_obra;
    }

    public Integer getNumero_obra() {
        return numero_obra;
    }

    public void setNumero_obra(Integer numero_obra) {
        this.numero_obra = numero_obra;
    }

    public String getTitulo_obra() {
        return titulo_obra;
    }

    public void setTitulo_obra(String titulo_obra) {
        this.titulo_obra = titulo_obra;
    }
}