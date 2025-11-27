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
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LeituraCsv {
    private String dataDaColeta;
    private String nomeDaMaquina;
    private Double usoDeCPU;
    private Double usoDeRAM;
    private Double ramTotal;
    private Double ramUsada;
    private Double usoDeDisco;
    private Double discoTotal;
    private Double discoUsado;
    private Double discoLivre;
    private Double taxaLeitura;
    private Double taxaEscrita;
    private Double latenciaLeitura;
    private Double latenciaEscrita;
    private Double netBytesEnviados;
    private Double netBytesRecebidos;
    private Double netDown;
    private Double netUp;
    private Double pacotesIn;
    private Double pacotesOut;
    private Integer conexoes;
    private Double latencia;
    private Double perdaPacote;
    private Long uptime;

    public LeituraCsv() {
    }

    public LeituraCsv(String dataDaColeta, String nomeDaMaquina, Double usoDeCPU, Double usoDeRAM, Double ramTotal, Double ramUsada, Double usoDeDisco, Double discoTotal, Double discoUsado, Double discoLivre, Double taxaLeitura, Double taxaEscrita, Double latenciaLeitura, Double latenciaEscrita, Double netBytesEnviados, Double netBytesRecebidos, Double netDown, Double netUp, Double pacotesIn, Double pacotesOut, Integer conexoes, Double latencia, Double perdaPacote, Long uptime) {
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

    public Double getRamTotal() {
        return ramTotal;
    }

    public void setRamTotal(Double ramTotal) {
        this.ramTotal = ramTotal;
    }

    public Double getRamUsada() {
        return ramUsada;
    }

    public void setRamUsada(Double ramUsada) {
        this.ramUsada = ramUsada;
    }

    public void setNetBytesEnviados(Double netBytesEnviados) {
        this.netBytesEnviados = netBytesEnviados;
    }

    public void setNetBytesRecebidos(Double netBytesRecebidos) {
        this.netBytesRecebidos = netBytesRecebidos;
    }

    public void setPacotesIn(Double pacotesIn) {
        this.pacotesIn = pacotesIn;
    }

    public void setPacotesOut(Double pacotesOut) {
        this.pacotesOut = pacotesOut;
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


    public InputStream leImportaArquivoCsv(InputStream csvStream, String hostname) {

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        String sqlSelect =
                "select s.hostname, c.limite, t.nome " +
                        "from componentes c " +
                        "inner join servidores s on c.fkServidor = s.idServidor " +
                        "inner join tipoComponente t on t.idTipo = c.fkTipo " +
                        "where s.hostname = ? " +
                        "union all " +
                        "select s.hostname, lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ?";

        List<ServidorComponente> capturas =
                template.query(sqlSelect,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname, hostname
                );

        try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedWriter saida = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))
        ) {

            System.out.println("Lendo o csv e escrevendo a saída...");

            String linha = entrada.readLine();
            if (linha == null) {
                throw new RuntimeException("CSV vazio");
            }

            // Cabeçalho
            String cabecalho = linha + ";alertaCpu;alertaRam;alertaDisco;alertaNetDown;alertaNetUp;alertaPacotesIn;alertaPacotesOut;alertaConexoes;alertaLatencia;alertaperdaPacote;saudeServidor;scoreSaudeServidor";
            saida.write(cabecalho);
            saida.newLine();

            // Lê primeira linha de dados
            linha = entrada.readLine();

            while (linha != null) {
                String[] registro = linha.split(";");

                String dataDaColeta = registro[0];
                String nomeDaMaquina = registro[1];
                Double usoCPU = Double.valueOf(registro[2]);
                Double usoRAM = Double.valueOf(registro[3]);
                Double ramTotal = Double.valueOf(registro[4]);
                Double ramUsada = Double.valueOf(registro[5]);
                Double usoDisco = Double.valueOf(registro[6]);
                Double discoTotal = Double.valueOf(registro[7]);
                Double discoUsado = Double.valueOf(registro[8]);
                Double discoLivre = Double.valueOf(registro[9]);
                Double taxaLeitura = Double.valueOf(registro[10]);
                Double taxaEscrita = Double.valueOf(registro[11]);
                Double latenciaLeitura = Double.valueOf(registro[12]);
                Double latenciaEscrita = Double.valueOf(registro[13]);
                Double netBytesEnviados = Double.valueOf(registro[14]);
                Double netBytesRecebidos = Double.valueOf(registro[15]);
                Double netDown = Double.valueOf(registro[16]);
                Double netUp = Double.valueOf(registro[17]);
                Double pacotesIn = Double.valueOf(registro[18]);
                Double pacotesOut = Double.valueOf(registro[19]);
                Integer conexoes = Integer.valueOf(registro[20]);
                Double latencia = Double.valueOf(registro[21]);

                Double perdaPacote =
                        (registro.length > 22 && !registro[22].isEmpty())
                                ? Double.valueOf(registro[22])
                                : 0.0;

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
                String saudeServidor = "Saudavel";
                Integer scoreSaudeServidor = 100;

                for (ServidorComponente c : capturas) {
                    String tipo = c.getNome();
                    Double limite = c.getLimite();

                    if (tipo.equalsIgnoreCase("cpu") && usoCPU > limite) {
                        alertaCpu = "sim";
                        scoreSaudeServidor -= 30;
                    } else if (tipo.equalsIgnoreCase("memória") && usoRAM > limite) {
                        alertaRam = "sim";
                        scoreSaudeServidor -= 30;
                    } else if (tipo.equalsIgnoreCase("disco") && usoDisco > limite) {
                        alertaDisco = "sim";
                        scoreSaudeServidor -= 40;
                    } else if (tipo.equalsIgnoreCase("download") && netDown > limite) {
                        alertaNetDown = "sim";
                    } else if (tipo.equalsIgnoreCase("upload") && netUp > limite) {
                        alertaNetUp = "sim";
                    } else if (tipo.equalsIgnoreCase("pacotein") && pacotesIn > limite * 1000) {
                        alertaPacotesIn = "sim";
                    } else if (tipo.equalsIgnoreCase("pacoteout") && pacotesOut > limite * 1000) {
                        alertaPacotesOut = "sim";
                    } else if (tipo.equalsIgnoreCase("conexao") && conexoes > limite) {
                        alertaConexoes = "sim";
                    } else if (tipo.equalsIgnoreCase("latencia") && latencia > limite) {
                        alertaLatencia = "sim";
                    } else if (tipo.equalsIgnoreCase("perdapacote") && perdaPacote > limite) {
                        alertaperdaPacote = "sim";
                    }
                }

                if (scoreSaudeServidor == 100) saudeServidor = "Saudável";
                else if (scoreSaudeServidor == 60 || scoreSaudeServidor == 70) saudeServidor = "Alerta";
                else if (scoreSaudeServidor <= 40) saudeServidor = "Crítico";

                // Monta linha final
                String novaLinha = linha + ";" + alertaCpu + ";" + alertaRam + ";" + alertaDisco + ";" +
                        alertaNetDown + ";" + alertaNetUp + ";" + alertaPacotesIn + ";" + alertaPacotesOut +
                        ";" + alertaConexoes + ";" + alertaLatencia + ";" + alertaperdaPacote + ";" +
                        saudeServidor + ";" + scoreSaudeServidor;

                saida.write(novaLinha);
                saida.newLine();

                linha = entrada.readLine();
            }

            saida.flush();

            // Retorna CSV final em memória
            return new ByteArrayInputStream(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar CSV", e);
        }
    }


    public void converterParaJson(String hostname) {

        String caminhoCsv = "saida.csv";
        String caminhoJson = "saida_" + hostname + ".json";

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(caminhoCsv))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            String[] headers = reader.readNext();

            if (headers == null) {
                System.out.println("Arquivo CSV vazio ou inválido.");
                return;
            }

            JSONArray jsonArray = new JSONArray();
            String[] line;

            while ((line = reader.readNext()) != null) {
                JSONObject obj = new JSONObject();

                for (int i = 0; i < headers.length && i < line.length; i++) {
                    String chave = headers[i].trim();
                    String valor = line[i].trim();

                    if (valor.matches("^-?\\d+(\\.\\d+)?$")) {
                        obj.put(chave, Double.parseDouble(valor));
                    } else if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                        obj.put(chave, Boolean.parseBoolean(valor));
                    } else {
                        obj.put(chave, valor);
                    }
                }

                jsonArray.put(obj);
            }

            try (FileWriter file = new FileWriter(caminhoJson)) {
                file.write(jsonArray.toString(2));
            }

            System.out.println("JSON gerado: " + caminhoJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

