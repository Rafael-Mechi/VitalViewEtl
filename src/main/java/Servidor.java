public class Servidor {
    private Integer idServidor;
    private String hostname;
    private String ip;
    private Integer fkHospital;

    public Servidor(){}

    public Servidor(Integer idServidor, String hostname, String ip, Integer fkHospital) {
        this.idServidor = idServidor;
        this.hostname = hostname;
        this.ip = ip;
        this.fkHospital = fkHospital;
    }

    public Integer getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(Integer idServidor) {
        this.idServidor = idServidor;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getFkHospital() {
        return fkHospital;
    }

    public void setFkHospital(Integer fkHospital) {
        this.fkHospital = fkHospital;
    }

    @Override
    public String toString() {
        return "Servidor{" +
                "idServidor=" + idServidor +
                ", hostname='" + hostname + '\'' +
                ", ip='" + ip + '\'' +
                ", fkHospital=" + fkHospital +
                '}';
    }
}
