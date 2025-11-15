import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.*;
import java.util.List;

public class LeituraCsv {
    private String dataDaColeta;
    private String nomeDaMaquina;
    private Double usoDeCPU;
    private Double usoDeRAM;
    private Long ramTotal;
    private Long ramUsada;
    private Double usoDeDisco;
    private Double discoTotal;
    private Double discoUsado;
    private Double discoLivre;
    private Double taxaLeitura;
    private Double taxaEscrita;
    private Double latenciaLeitura;
    private Double latenciaEscrita;
    private Long netBytesEnviados;
    private Long netBytesRecebidos;
    private Double netDown;
    private Double netUp;
    private Long pacotesIn;
    private Long pacotesOut;
    private Integer conexoes;
    private Double latencia;
    private Double perdaPacote;
    private Long uptime;

    public LeituraCsv() {
    }

    public LeituraCsv(String nomeDaMaquina, String dataDaColeta, Double usoDeCPU, Double usoDeRAM, Long ramTotal, Long ramUsada, Double usoDeDisco, Double discoTotal, Double discoUsado, Double discoLivre, Double taxaLeitura, Double taxaEscrita, Double latenciaLeitura, Double latenciaEscrita, Long netBytesEnviados, Long netBytesRecebidos, Double netDown, Double netUp, Long pacotesIn, Long pacotesOut, Integer conexoes, Double latencia, Double perdaPacote, Long uptime) {
        this.dataDaColeta = dataDaColeta;
        this.nomeDaMaquina = nomeDaMaquina;
        this.usoDeCPU = usoDeCPU;
        this.usoDeRAM = usoDeRAM;
        this.ramTotal = ramTotal;
        this.ramUsada = ramUsada;
        this.usoDeDisco = usoDeDisco;
        this.discoTotal = discoTotal;
        this.discoUsado = discoUsado;
        this.discoLivre = discoLivre;
        this.taxaLeitura = taxaLeitura;
        this.taxaEscrita = taxaEscrita;
        this.latenciaLeitura = latenciaLeitura;
        this.latenciaEscrita = latenciaEscrita;
        this.netBytesEnviados = netBytesEnviados;
        this.netBytesRecebidos = netBytesRecebidos;
        this.netDown = netDown;
        this.netUp = netUp;
        this.pacotesIn = pacotesIn;
        this.pacotesOut = pacotesOut;
        this.conexoes = conexoes;
        this.latencia = latencia;
        this.perdaPacote = perdaPacote;
        this.uptime = uptime;
    }


    public String getDataDaColeta() {
        return dataDaColeta;
    }

    public void setDataDaColeta(String dataDaColeta) {
        this.dataDaColeta = dataDaColeta;
    }

    public String getNomeDaMaquina() {
        return nomeDaMaquina;
    }

    public void setNomeDaMaquina(String nomeDaMaquina) {
        this.nomeDaMaquina = nomeDaMaquina;
    }

    public Double getUsoDeCPU() {
        return usoDeCPU;
    }

    public void setUsoDeCPU(Double usoDeCPU) {
        this.usoDeCPU = usoDeCPU;
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

    public Double getUsoDeDisco() {
        return usoDeDisco;
    }

    public void setUsoDeDisco(Double usoDeDisco) {
        this.usoDeDisco = usoDeDisco;
    }

    public Double getDiscoTotal() {
        return discoTotal;
    }

    public void setDiscoTotal(Double discoTotal) {
        this.discoTotal = discoTotal;
    }

    public Double getDiscoUsado() {
        return discoUsado;
    }

    public void setDiscoUsado(Double discoUsado) {
        this.discoUsado = discoUsado;
    }

    public Double getDiscoLivre() {
        return discoLivre;
    }

    public void setDiscoLivre(Double discoLivre) {
        this.discoLivre = discoLivre;
    }

    public Double getTaxaLeitura() {
        return taxaLeitura;
    }

    public void setTaxaLeitura(Double taxaLeitura) {
        this.taxaLeitura = taxaLeitura;
    }

    public Double getTaxaEscrita() {
        return taxaEscrita;
    }

    public void setTaxaEscrita(Double taxaEscrita) {
        this.taxaEscrita = taxaEscrita;
    }

    public Double getLatenciaLeitura() {
        return latenciaLeitura;
    }

    public void setLatenciaLeitura(Double latenciaLeitura) {
        this.latenciaLeitura = latenciaLeitura;
    }

    public Double getLatenciaEscrita() {
        return latenciaEscrita;
    }

    public void setLatenciaEscrita(Double latenciaEscrita) {
        this.latenciaEscrita = latenciaEscrita;
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

    public Double getNetDown() {
        return netDown;
    }

    public void setNetDown(Double netDown) {
        this.netDown = netDown;
    }

    public Double getNetUp() {
        return netUp;
    }

    public void setNetUp(Double netUp) {
        this.netUp = netUp;
    }

    public Long getPacotesIn() {
        return pacotesIn;
    }

    public void setPacotesIn(Long pacotesIn) {
        this.pacotesIn = pacotesIn;
    }

    public Long getPacotesOut() {
        return pacotesOut;
    }

    public void setPacotesOut(Long pacotesOut) {
        this.pacotesOut = pacotesOut;
    }

    public Integer getConexoes() {
        return conexoes;
    }

    public void setConexoes(Integer conexoes) {
        this.conexoes = conexoes;
    }

    public Double getLatencia() {
        return latencia;
    }

    public void setLatencia(Double latencia) {
        this.latencia = latencia;
    }

    public Double getPerdaPacote() {
        return perdaPacote;
    }

    public void setPerdaPacote(Double perdaPacote) {
        this.perdaPacote = perdaPacote;
    }

    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }


    public void leImportaArquivoCsv(String nomeArq) {
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
                String dataDaColeta = registro[0];
                String nomeDaMaquina = registro[1];
                Double usoCPU = Double.valueOf(registro[2]);
                Double usoRAM = Double.valueOf(registro[3]);
                Long ramTotal = Long.valueOf(registro[4]);
                Long ramUsada = Long.valueOf(registro[5]);
                Double usoDisco = Double.valueOf(registro[6]);
                Double discoTotal = Double.valueOf(registro[7]);
                Double discoUsado = Double.valueOf(registro[8]);
                Double discoLivre = Double.valueOf(registro[9]);
                Double taxaLeitura = Double.valueOf(registro[10]);
                Double taxaEscrita = Double.valueOf(registro[11]);
                Double latenciaLeitura = Double.valueOf(registro[12]);
                Double latenciaEscrita = Double.valueOf(registro[13]);
                Long netBytesEnviados = Long.valueOf(registro[14]);
                Long netBytesRecebidos = Long.valueOf(registro[15]);
                Double netDown = Double.valueOf(registro[16]);
                Double netUp = Double.valueOf(registro[17]);
                Long pacotesIn = Long.valueOf(registro[18]);
                Long pacotesOut = Long.valueOf(registro[19]);
                Integer conexoes = Integer.valueOf(registro[20]);
                Double latencia = Double.valueOf(registro[21]);
                if (registro[22] == null || registro[22].isEmpty()){
                    perdaPacote = 0.0;
                } else {
                    perdaPacote = Double.valueOf(registro[22]);
                }
                Long uptime = Long.valueOf(registro[23]);


                String alertaCpu = "não";
                String alertaRam = "não";
                String alertaDisco = "não";
                String alertaNetDown = "não";
                String alertaNetUp = "não";
                String alertaPacotesIn = "não";
                String alertaPacotesOut = "não";
                String alertaConexoes = "não";
                String alertaLatencia = "não";
                String alertaperdaPacote = "não";


                for (ServidorComponente c : capturas) {
                    String tipoComponente = c.getNome();
                    Double limite = c.getLimite();

                    if (tipoComponente.equalsIgnoreCase("cpu") && usoCPU > limite) {
                        alertaCpu = "sim";
                    } else if (tipoComponente.equalsIgnoreCase("memória") && usoRAM > limite) {
                        alertaRam = "sim";
                    } else if (tipoComponente.equalsIgnoreCase("disco") && usoDisco > limite) {
                        alertaDisco = "sim";
                    }

                }

                if (netDown >= 9.04) {
                    alertaNetDown = "sim";
                }

                if (netUp >= 0.13){
                    alertaNetUp = "sim";
                }

                if (pacotesIn >= 37136.60){
                    alertaPacotesIn = "sim";
                }

                if (pacotesOut >= 4695.00){
                    alertaPacotesOut = "sim";
                }

                if (conexoes >= 34){
                    alertaConexoes = "sim";
                }

                if (latencia >= 18.88){
                    alertaLatencia = "sim";
                }

                if (perdaPacote > 0.5){
                    alertaperdaPacote = "sim";
                }

                // escreve a linha de dados + alertas
                String novaLinha = linha + ";" + alertaCpu + ";" + alertaRam + ";" + alertaDisco + ";" + alertaNetDown + ";" + alertaNetUp + ";" + alertaPacotesIn + ";" + alertaPacotesOut + ";" + alertaConexoes + ";" + alertaLatencia + ";" + alertaperdaPacote;
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
                saida.close();
            } catch (IOException erro) {
                System.out.println("Erro ao fechar o arquivo");
            }
        }

        System.out.println("Processo finalizado");

    }

    public void converterParaJson() {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader("/Users/rr658/Downloads/Documentos VitalView/VitalViewEtl/saida.csv"))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build();) {
            // Lê a primeira linha como cabeçalho
            String[] headers = reader.readNext();

            if (headers == null) {
                System.out.println("Arquivo CSV vazio ou inválido.");
                return;
            }

            JSONArray jsonArray = new JSONArray();
            String[] line;

            //Lê cada linha e transforma em JSONObject
            while ((line = reader.readNext()) != null) {
                JSONObject obj = new JSONObject();

                for (int i = 0; i < headers.length && i < line.length; i++) {
                    String chave = headers[i].trim();
                    String valor = line[i].trim();

                    // tenta converter números automaticamente
                    if (valor.matches("^-?\\d+(\\.\\d+)?$")) {
                        // valor numérico
                        obj.put(chave, Double.parseDouble(valor));
                    } else if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                        // valor booleano
                        obj.put(chave, Boolean.parseBoolean(valor));
                    } else {
                        // valor texto
                        obj.put(chave, valor);
                    }
                }

                jsonArray.put(obj);
            }

            // Salva o JSON com indentação bonita
            try (FileWriter file = new FileWriter("/Users/rr658/Downloads/Documentos VitalView/VitalViewEtl/saida.json")) {
                file.write(jsonArray.toString(2)); // 2 = indentação
            }

            System.out.println("Arquivo JSON gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

