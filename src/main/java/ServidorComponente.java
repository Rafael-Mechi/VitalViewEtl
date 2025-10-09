public class ServidorComponente {
    private String hostname;
    private Double limite;
    private String nome;

    public ServidorComponente(){}

    public ServidorComponente(String hostname, Double limite, String nome) {
        this.hostname = hostname;
        this.limite = limite;
        this.nome = nome;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Double getLimite() {
        return limite;
    }

    public void setLimite(Double limite) {
        this.limite = limite;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "ServidorComponente{" +
                "hostname='" + hostname + '\'' +
                ", limite=" + limite +
                ", nome='" + nome + '\'' +
                '}';
    }
}
