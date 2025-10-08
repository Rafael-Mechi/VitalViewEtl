public class Componente {
    private Integer idComponente;
    private Integer fkTipo;
    private Integer fkServidor;
    private Integer limite;

    public Componente() {}

    public Componente(Integer idComponente, Integer fkTipo, Integer fkServidor, Integer limite) {
        this.idComponente = idComponente;
        this.fkTipo = fkTipo;
        this.fkServidor = fkServidor;
        this.limite = limite;
    }

    public Integer getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(Integer idComponente) {
        this.idComponente = idComponente;
    }

    public Integer getFkTipo() {
        return fkTipo;
    }

    public void setFkTipo(Integer fkTipo) {
        this.fkTipo = fkTipo;
    }

    public Integer getFkServidor() {
        return fkServidor;
    }

    public void setFkServidor(Integer fkServidor) {
        this.fkServidor = fkServidor;
    }

    public Integer getLimite() {
        return limite;
    }

    public void setLimite(Integer limite) {
        this.limite = limite;
    }
}
