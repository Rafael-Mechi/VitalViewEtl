import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.List;

public class LeituraCsv {
    private String nomeDaMaquina;
    private String dataDaColeta;
    private Double usoDeCPU;
    private Double load1;
    private Double load5;
    private Double load15;
    private Double usoDeRAM;
    private Long ramTotal;
    private Long ramUsada;
    private Double usoDeSwap;
    private Long swapTotal;
    private Long swapUsada;
    private Double usoDeDisco;
    private Long discoTotal;
    private Long discoUsado;
    private Long discoLivre;
    private Long netBytesEnviados;
    private Long netBytesRecebidos;
    private Double freqCPU;
    private Double tempCPU;
    private Long uptime;
    private String processo;
    private Integer pid;
    private String usuario;
    private Double cpuProc;
    private Double memProc;
    private Integer threads;
    private Long rss;
    private Long ioLeitura;
    private Long ioEscrita;
    private String quandoFoiIniciado;
    private String status;

    public LeituraCsv(){}

    public LeituraCsv(String nomeDaMaquina, String dataDaColeta, Double usoDeCPU, Double load1, Double load5, Double load15, Double usoDeRAM, Long ramTotal, Long ramUsada, Double usoDeSwap, Long swapTotal, Long swapUsada, Double usoDeDisco, Long discoTotal, Long discoUsado, Long discoLivre, Long netBytesEnviados, Long netBytesRecebidos, Double freqCPU, Double tempCPU, Long uptime, String processo, Integer pid, String usuario, Double cpuProc, Double memProc, Integer threads, Long rss, Long ioLeitura, Long ioEscrita, String quandoFoiIniciado, String status) {
        this.nomeDaMaquina = nomeDaMaquina;
        this.dataDaColeta = dataDaColeta;
        this.usoDeCPU = usoDeCPU;
        this.load1 = load1;
        this.load5 = load5;
        this.load15 = load15;
        this.usoDeRAM = usoDeRAM;
        this.ramTotal = ramTotal;
        this.ramUsada = ramUsada;
        this.usoDeSwap = usoDeSwap;
        this.swapTotal = swapTotal;
        this.swapUsada = swapUsada;
        this.usoDeDisco = usoDeDisco;
        this.discoTotal = discoTotal;
        this.discoUsado = discoUsado;
        this.discoLivre = discoLivre;
        this.netBytesEnviados = netBytesEnviados;
        this.netBytesRecebidos = netBytesRecebidos;
        this.freqCPU = freqCPU;
        this.tempCPU = tempCPU;
        this.uptime = uptime;
        this.processo = processo;
        this.pid = pid;
        this.usuario = usuario;
        this.cpuProc = cpuProc;
        this.memProc = memProc;
        this.threads = threads;
        this.rss = rss;
        this.ioLeitura = ioLeitura;
        this.ioEscrita = ioEscrita;
        this.quandoFoiIniciado = quandoFoiIniciado;
        this.status = status;
    }

    public String getNomeDaMaquina() {
        return nomeDaMaquina;
    }

    public void setNomeDaMaquina(String nomeDaMaquina) {
        this.nomeDaMaquina = nomeDaMaquina;
    }

    public String getDataDaColeta() {
        return dataDaColeta;
    }

    public void setDataDaColeta(String dataDaColeta) {
        this.dataDaColeta = dataDaColeta;
    }

    public Double getUsoDeCPU() {
        return usoDeCPU;
    }

    public void setUsoDeCPU(Double usoDeCPU) {
        this.usoDeCPU = usoDeCPU;
    }

    public Double getLoad1() {
        return load1;
    }

    public void setLoad1(Double load1) {
        this.load1 = load1;
    }

    public Double getLoad5() {
        return load5;
    }

    public void setLoad5(Double load5) {
        this.load5 = load5;
    }

    public Double getLoad15() {
        return load15;
    }

    public void setLoad15(Double load15) {
        this.load15 = load15;
    }

    public Double getUsoDeRAM() {
        return usoDeRAM;
    }

    public void setUsoDeRAM(Double usoDeRAM) {
        this.usoDeRAM = usoDeRAM;
    }

    public Long getRamTotal() {
        return ramTotal;
    }

    public void setRamTotal(Long ramTotal) {
        this.ramTotal = ramTotal;
    }

    public Long getRamUsada() {
        return ramUsada;
    }

    public void setRamUsada(Long ramUsada) {
        this.ramUsada = ramUsada;
    }

    public Double getUsoDeSwap() {
        return usoDeSwap;
    }

    public void setUsoDeSwap(Double usoDeSwap) {
        this.usoDeSwap = usoDeSwap;
    }

    public Long getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(Long swapTotal) {
        this.swapTotal = swapTotal;
    }

    public Long getSwapUsada() {
        return swapUsada;
    }

    public void setSwapUsada(Long swapUsada) {
        this.swapUsada = swapUsada;
    }

    public Double getUsoDeDisco() {
        return usoDeDisco;
    }

    public void setUsoDeDisco(Double usoDeDisco) {
        this.usoDeDisco = usoDeDisco;
    }

    public Long getDiscoTotal() {
        return discoTotal;
    }

    public void setDiscoTotal(Long discoTotal) {
        this.discoTotal = discoTotal;
    }

    public Long getDiscoUsado() {
        return discoUsado;
    }

    public void setDiscoUsado(Long discoUsado) {
        this.discoUsado = discoUsado;
    }

    public Long getDiscoLivre() {
        return discoLivre;
    }

    public void setDiscoLivre(Long discoLivre) {
        this.discoLivre = discoLivre;
    }

    public Long getNetBytesEnviados() {
        return netBytesEnviados;
    }

    public void setNetBytesEnviados(Long netBytesEnviados) {
        this.netBytesEnviados = netBytesEnviados;
    }

    public Long getNetBytesRecebidos() {
        return netBytesRecebidos;
    }

    public void setNetBytesRecebidos(Long netBytesRecebidos) {
        this.netBytesRecebidos = netBytesRecebidos;
    }

    public Double getFreqCPU() {
        return freqCPU;
    }

    public void setFreqCPU(Double freqCPU) {
        this.freqCPU = freqCPU;
    }

    public Double getTempCPU() {
        return tempCPU;
    }

    public void setTempCPU(Double tempCPU) {
        this.tempCPU = tempCPU;
    }

    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Double getCpuProc() {
        return cpuProc;
    }

    public void setCpuProc(Double cpuProc) {
        this.cpuProc = cpuProc;
    }

    public Double getMemProc() {
        return memProc;
    }

    public void setMemProc(Double memProc) {
        this.memProc = memProc;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Long getRss() {
        return rss;
    }

    public void setRss(Long rss) {
        this.rss = rss;
    }

    public Long getIoLeitura() {
        return ioLeitura;
    }

    public void setIoLeitura(Long ioLeitura) {
        this.ioLeitura = ioLeitura;
    }

    public Long getIoEscrita() {
        return ioEscrita;
    }

    public void setIoEscrita(Long ioEscrita) {
        this.ioEscrita = ioEscrita;
    }

    public String getQuandoFoiIniciado() {
        return quandoFoiIniciado;
    }

    public void setQuandoFoiIniciado(String quandoFoiIniciado) {
        this.quandoFoiIniciado = quandoFoiIniciado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void leImportaArquivoCsv (String nomeArq) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        System.out.println("Pegando informações de hostname, limite do componente de cada componente...");
        String sqlSelect = "select s.hostname, c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = 'srv1'";
        List<ServidorComponente> capturas = template.query(sqlSelect, new BeanPropertyRowMapper<>(ServidorComponente.class));


        Reader arq = null;
        BufferedReader entrada = null;
        BufferedWriter saida = null;

        // Bloco try-catch para abrir o arquivo
        try {
            arq = new InputStreamReader(new FileInputStream(nomeArq), "UTF-8");
            saida = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("saida.csv"), "UTF-8"));

            entrada = new BufferedReader(arq);
        } catch (IOException erro) {
            System.out.println("Erro na abertura do arquivo");
            System.exit(1);
        }

        System.out.println("Lendo o csv e escrevendo a saída");

        try {
            String[] registro;      // registro eh um vetor que armazenara cada parte da linha do arquivo
            // readLine() eh usado   para ler uma linha inteira do arquivo
            // Le a primeira linha do arquivo, que eh o cabecalho
            String linha = entrada.readLine(); // linha eh a primeira linha do arquivo
            String cabecalho = linha + ";alertaCpu;alertaRam;alertaDisco";
            saida.write(cabecalho);
            saida.newLine();

            // separa cada item da linha usando o delimitador ;
            registro = linha.split(";");

            // Le a segunda linha do arquivo (1a linha de dados)
            linha = entrada.readLine();

            while (linha != null) { // enquanto nao chegou ao final do arquivo
                registro = linha.split(";");
                // converte de String para Integer usando Integer.valueOf
                // Se fosse converter de String para int usa-se Integer.parseInt
                String nomeDaMaquina = registro[0];
                String dataDaColeta = registro[1];
                Double usoCPU = Double.valueOf(registro[2]);
                Double load1 = Double.valueOf(registro[3]);
                Double load5 = Double.valueOf(registro[4]);
                Double load15 = Double.valueOf(registro[5]);
                Double usoRAM = Double.valueOf(registro[6]);
                Long ramTotal = Long.valueOf(registro[7]);
                Long ramUsada = Long.valueOf(registro[8]);
                Double usoSwap = Double.valueOf(registro[9]);
                Long swapTotal = Long.valueOf(registro[10]);
                Long swapUsada = Long.valueOf(registro[11]);
                Double usoDisco = Double.valueOf(registro[12]);
                Long discoTotal = Long.valueOf(registro[13]);
                Long discoUsado = Long.valueOf(registro[14]);
                Long discoLivre = Long.valueOf(registro[15]);
                Long netBytesEnviados = Long.valueOf(registro[16]);
                Long netBytesRecebidos = Long.valueOf(registro[17]);
                Double freqCPU = Double.valueOf(registro[18]);
                Double tempCPU = Double.valueOf(registro[19]);
                Long uptime = Long.valueOf(registro[20]);
                String processo = registro[21];
                Integer pid = Integer.valueOf(registro[22]);
                String usuario = registro[23];
                Double cpuProc = Double.valueOf(registro[24]);
                Double memProc = Double.valueOf(registro[25]);
                Integer threads = Integer.valueOf(registro[26]);
                Long rss = Long.valueOf(registro[27]);
                Long ioLeitura = Long.valueOf(registro[28]);
                Long ioEscrita = Long.valueOf(registro[29]);
                String quandoFoiIniciado = registro[30];
                String status = registro[31];


                String alertaCpu = "não";
                String alertaRam = "não";
                String alertaDisco = "não";


                for (ServidorComponente c : capturas) {
                    String tipoComponente = c.getNome();
                    Double limite = c.getLimite();

                    if(tipoComponente.equalsIgnoreCase("cpu") && usoCPU > limite){
                        alertaCpu = "sim";
                    } else if(tipoComponente.equalsIgnoreCase("memória") && usoRAM > limite){
                        alertaRam = "sim";
                    } else if(tipoComponente.equalsIgnoreCase("disco") && usoDisco > limite){
                        alertaDisco = "sim";
                    }

                }
                // escreve a linha de dados + alertas
                String novaLinha = linha + ";" + alertaCpu + ";" + alertaRam + ";" + alertaDisco;
                saida.write(novaLinha);
                saida.newLine();

                // Le a proxima linha do arquivo
                linha = entrada.readLine();
            } // final do while
        } // final do try
        catch (IOException erro) {
            System.out.println("Erro ao ler o arquivo");
            erro.printStackTrace();
        } finally {
            try {
                entrada.close();
                arq.close();
            } catch (IOException erro) {
                System.out.println("Erro ao fechar o arquivo");
            }
        }

        System.out.println("Processo finalizado");

    }
}
