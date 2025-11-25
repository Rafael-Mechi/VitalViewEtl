import okhttp3.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

//importações slack
//import com.slack.api.Slack;
//import com.slack.api.methods.SlackApiException;
//import com.slack.api.methods.request.chat.ChatPostMessageRequest;
//import com.slack.api.methods.response.chat.ChatPostMessageResponse;

//imports jira
import java.io.IOException;
//import java.util.Base64;
//import java.io.IOException;

public class Alerta {
    private LocalDateTime dataAlerta;
    private Integer registro;
    private Integer fkComponente;

    // Códigos das métricas de rede (1 a 7)
    private Integer codigoDown = 1;
    private Integer codigoUp = 2;
    private Integer codigoIn = 3;
    private Integer codigoOut = 4;
    private Integer codigoConexao = 5;
    private Integer codigoLatencia = 6;
    private Integer codigoPerda = 7;

    public Alerta(LocalDateTime dataAlerta, Integer registro, Integer fkComponente, Integer codigoDown, Integer codigoUp, Integer codigoIn, Integer codigoOut, Integer codigoConexao, Integer codigoLatencia, Integer codigoPerda) {
        this.dataAlerta = dataAlerta;
        this.registro = registro;
        this.fkComponente = fkComponente;
        this.codigoDown = codigoDown;
        this.codigoUp = codigoUp;
        this.codigoIn = codigoIn;
        this.codigoOut = codigoOut;
        this.codigoConexao = codigoConexao;
        this.codigoLatencia = codigoLatencia;
        this.codigoPerda = codigoPerda;
    }

    public Alerta(LocalDateTime dataAlerta, Integer registro, Integer fkComponente) {
        this.dataAlerta = dataAlerta;
        this.registro = registro;
        this.fkComponente = fkComponente;
    }

    public Alerta() {
    }

    public LocalDateTime getDataAlerta() {
        return dataAlerta;
    }

    public void setDataAlerta(LocalDateTime dataAlerta) {
        this.dataAlerta = dataAlerta;
    }

    public Integer getRegistro() {
        return registro;
    }

    public void setRegistro(Integer registro) {
        this.registro = registro;
    }

    public Integer getFkComponente() {
        return fkComponente;
    }

    public void setFkComponente(Integer fkComponente) {
        this.fkComponente = fkComponente;
    }

    public void salvaTabelaAlerta(InputStream csvStream, String hostname) {

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        System.out.println("Pegando informações dos limites do servidor");

        String sqlSelectCpu = "select c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = ? and  t.nome = 'Cpu'";

        String sqlSelectRam = "select c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = ? and t.nome = 'Memória'";

        String sqlSelectDisco = "select c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = ? and t.nome = 'Disco'";

        String sqlSelectDown =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica  lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'Download'";

        String sqlSelectUpload =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'Upload'";

        String sqlSelectPacoteIn =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'PacoteIn'";

        String sqlSelectPacoteOut =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'PacoteOut'";

        String sqlSelectConexao =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'Conexao'";

        String sqlSelectLatencia =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'Latencia'";

        String sqlSelectPerdaPacote =
                "select lr.limite, r.metrica as nome " +
                        "from limiteMetrica lr " +
                        "inner join servidores s on lr.fkServidor = s.idServidor " +
                        "inner join metrica r on r.idMetrica = lr.fkMetrica " +
                        "where s.hostname = ? and r.metrica = 'PerdaPacote'";


        List<ServidorComponente> capturaLimiteCpu =
                template.query(sqlSelectCpu,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimiteRam =
                template.query(sqlSelectRam,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimiteDisco =
                template.query(sqlSelectDisco,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimiteDown =
                template.query(sqlSelectDown,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimiteUpload =
                template.query(sqlSelectUpload,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimitePacoteIn =
                template.query(sqlSelectPacoteIn,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimitePacoteOut =
                template.query(sqlSelectPacoteOut,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimiteConexao =
                template.query(sqlSelectConexao,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimiteLatencia =
                template.query(sqlSelectLatencia,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );
        List<ServidorComponente> capturaLimitePerdaPacote =
                template.query(sqlSelectPerdaPacote,
                        new BeanPropertyRowMapper<>(ServidorComponente.class),
                        hostname
                );

        Double limiteCpu = capturaLimiteCpu.get(0).getLimite();
        Double limiteRam = capturaLimiteRam.get(0).getLimite();
        Double limiteDisco = capturaLimiteDisco.get(0).getLimite();
        Double limiteDown = capturaLimiteDown.get(0).getLimite();
        Double limiteUp = capturaLimiteUpload.get(0).getLimite();
        Double limiteIn = capturaLimitePacoteIn.get(0).getLimite();
        Double limiteOut = capturaLimitePacoteOut.get(0).getLimite();
        Double limiteConexao = capturaLimiteConexao.get(0).getLimite();
        Double limiteLatencia = capturaLimiteLatencia.get(0).getLimite();
        Double limitePerdaPacote = capturaLimitePerdaPacote.get(0).getLimite();

        String sqlSelectCompRede =
                "select c.idComponente " +
                        "from componentes c " +
                        "join servidores s on c.fkServidor = s.idServidor " +
                        "join tipoComponente t on t.idTipo = c.fkTipo " +
                        "where s.hostname = ? and t.nome = 'Rede'";

        Integer fkComponenteRede =
                template.queryForObject(sqlSelectCompRede, Integer.class, hostname);

        BufferedReader entrada = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8));

        System.out.println("Lendo o csv e escrevendo a saída");

        try {
            String[] registro;      // registro eh um vetor que armazenara cada parte da linha do arquivo
            // readLine() eh usado   para ler uma linha inteira do arquivo
            // Le a primeira linha do arquivo, que eh o cabecalho
            String linha = entrada.readLine(); // linha eh a primeira linha do arquivo

            // separa cada item da linha usando o delimitador ;
            registro = linha.split(";");

            // Le a segunda linha do arquivo (1a linha de dados)
            linha = entrada.readLine();
            registro = linha.split(";");

            Double primeiroRegistroCpu = Double.valueOf(registro[2]);
            Double primeiroRegistroRam = Double.valueOf(registro[3]);
            Double primeiroRegistroDisco = Double.valueOf(registro[6]);
            Double primeiroRegistroDown = Double.valueOf(registro[16]);
            Double primeiroRegistroUp = Double.valueOf(registro[17]);
            Long primeiroRegistroIn = Long.valueOf(registro[18]);
            Long primeiroRegistroOut = Long.valueOf(registro[19]);
            Integer primeiroRegistroConexao = Integer.valueOf(registro[20]);
            Double primeiroRegistroLatencia = Double.valueOf(registro[21]);
            Double primeiroRegistroPerdaPacote;
            if (registro.length > 22 && registro[22] != null && !registro[22].isEmpty()) {
                primeiroRegistroPerdaPacote = Double.valueOf(registro[22]);
            } else {
                primeiroRegistroPerdaPacote = 0.0;
            }

            Boolean podeRegistrarCpu = false;
            Boolean podeRegistrarRam = false;
            Boolean podeRegistrarDisco = false;
            Boolean podeRegistrarDown = false;
            Boolean podeRegistrarUp = false;
            Boolean podeRegistrarIn = false;
            Boolean podeRegistrarOut = false;
            Boolean podeRegistrarConexao = false;
            Boolean podeRegistrarLatencia = false;
            Boolean podeRegistrarPerdaPacote = false;

            Integer contCpu = 0;
            Integer contRam = 0;
            Integer contDisco = 0;
            Integer contDown = 0;
            Integer contUp = 0;
            Integer contIn = 0;
            Integer contOut = 0;
            Integer contConexao = 0;
            Integer contLatencia = 0;
            Integer contPerdaPacote = 0;

            // verificando se o uso de cpu da primeira linha ultrapassa o limite
            if (primeiroRegistroCpu > limiteCpu) {
                contCpu++;
            }

            // ram...
            if (primeiroRegistroRam > limiteRam) {
                contRam++;
            }

            // disco...
            if (primeiroRegistroDisco > limiteDisco) {
                contDisco++;
            }

            if (primeiroRegistroDown > limiteDown) {
                contDown++;
            }

            if (primeiroRegistroUp > limiteUp) {
                contUp++;
            }

            if (primeiroRegistroIn > (limiteIn * 1000)) {
                contIn++;
            }

            if (primeiroRegistroOut > (limiteOut * 1000)) {
                contOut++;
            }

            if (primeiroRegistroConexao > limiteConexao) {
                contConexao++;
            }

            if (primeiroRegistroLatencia > limiteLatencia) {
                contLatencia++;
            }

            if (primeiroRegistroPerdaPacote > limitePerdaPacote) {
                contPerdaPacote++;
            }

            while (linha != null) { // enquanto nao chegou ao final do arquivo
                registro = linha.split(";");
                // converte de String para Integer usando Integer.valueOf
                // Se fosse converter de String para int usa-se Integer.parseInt
                String nomeDaMaquina = registro[1];
                String dataHoraColeta = registro[0];
                Double usoCPU = Double.valueOf(registro[2]);
                Double usoRAM = Double.valueOf(registro[3]);
                Double usoDisco = Double.valueOf(registro[6]);
                Double netDown = Double.valueOf(registro[16]);
                Double netUp = Double.valueOf(registro[17]);
                Long pacotesIn = Long.valueOf(registro[18]);
                Long pacotesOut = Long.valueOf(registro[19]);
                Integer conexoes = Integer.valueOf(registro[20]);
                Double latencia = Double.valueOf(registro[21]);
                Double perdaPacote;
                if (registro.length > 22 && registro[22] != null && !registro[22].isEmpty()) {
                    perdaPacote = Double.valueOf(registro[22]);
                } else {
                    perdaPacote = 0.0;
                }


                //Contagem de registros possíveis alertas.
                if (usoCPU < limiteCpu) {
                    contCpu = 0;
                } else {
                    contCpu++;
                }

                if (usoRAM < limiteRam) {
                    contRam = 0;
                } else {
                    contRam++;
                }

                if (usoDisco < limiteDisco) {
                    contDisco = 0;
                } else {
                    contDisco++;
                }

                if (netDown < limiteDown) {
                    contDown = 0;
                } else {
                    contDown++;
                }

                if (netUp < limiteUp) {
                    contUp = 0;
                } else {
                    contUp++;
                }

                if (pacotesIn < (limiteIn * 1000)) {
                    contIn = 0;
                } else {
                    contIn++;
                }

                if (pacotesOut < (limiteOut * 1000)) {
                    contOut = 0;
                } else {
                    contOut++;
                }

                if (conexoes < limiteConexao) {
                    contConexao = 0;
                } else {
                    contConexao++;
                }

                if (latencia < limiteLatencia) {
                    contLatencia = 0;
                } else {
                    contLatencia++;
                }

                if (perdaPacote < limitePerdaPacote) {
                    contPerdaPacote = 0;
                } else {
                    contPerdaPacote++;
                }

                //Verificando veracidade de alerta, mais que 3 registros.

                if (contCpu == 3) {
                    podeRegistrarCpu = true;
                }

                if (contRam == 3) {
                    podeRegistrarRam = true;
                }

                if (contDisco == 3) {
                    podeRegistrarDisco = true;
                }

                if (contDown == 3) {
                    podeRegistrarDown = true;
                }

                if (contUp == 3) {
                    podeRegistrarUp = true;
                }

                if (contIn == 3) {
                    podeRegistrarIn = true;
                }

                if (contOut == 3) {
                    podeRegistrarOut = true;
                }

                if (contConexao == 3) {
                    podeRegistrarConexao = true;
                }

                if (contLatencia == 3) {
                    podeRegistrarLatencia = true;
                }

                if (contPerdaPacote == 3) {
                    podeRegistrarPerdaPacote = true;
                }

                for (ServidorComponente sc : capturaLimiteCpu) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("cpu") && usoCPU > limite && podeRegistrarCpu) {
                        String sqlInsertAlertaCpu = "insert into alerta (data_alerta, registro, fkComponente) values" +
                                "(?, ?, ?)";
                        template.update(sqlInsertAlertaCpu, dataHoraColeta, usoCPU, 1);
                        podeRegistrarCpu = false;

//                        enviarAlertaSlack(usoCPU, usoRAM, usoDisco, dataHoraColeta, nomeDaMaquina, limiteCpu, limiteRam, limiteDisco);

                        String msgJira = "Alerta CPU: " + usoCPU + "%" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimiteRam) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("memória") && usoRAM > limite && podeRegistrarRam && contRam >= 3) {
                        String sqlInsertAlertaRam = "insert into alerta (data_alerta, registro, fkComponente) values" +
                                "(?, ?, ?)";
                        template.update(sqlInsertAlertaRam, dataHoraColeta, usoRAM, 2);
                        podeRegistrarRam = false;

//                        enviarAlertaSlack(usoCPU, usoRAM, usoDisco, dataHoraColeta, nomeDaMaquina, limiteCpu, limiteRam, limiteDisco);

                        String msgJira = "Alerta RAM: " + usoRAM + "%" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimiteDisco) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("disco") && usoDisco > limite && podeRegistrarDisco && contDisco >= 3) {
                        String sqlInsertAlertaDisco = "insert into alerta (data_alerta, registro, fkComponente) values" +
                                "(?, ?, ?)";
                        template.update(sqlInsertAlertaDisco, dataHoraColeta, usoDisco, 3);
                        podeRegistrarDisco = false;

//                        enviarAlertaSlack(usoCPU, usoRAM, usoDisco, dataHoraColeta, nomeDaMaquina, limiteCpu, limiteRam, limiteDisco);

                        String msgJira = "Alerta DISCO: " + usoDisco + "%" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimiteDown) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("download") && netDown > limite && podeRegistrarDown && contDown >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        // registro = 1 (Download), fkComponente = Rede
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoDown, fkComponenteRede);
                        podeRegistrarDown = false;

                        // Detalhe no Jira
                        String msgJira = "Alerta REDE - VELOCIDADE DOWNLOAD: " + netDown + "Mbps" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimiteUpload) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("upload") && netUp > limite && podeRegistrarUp && contUp >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoUp, fkComponenteRede);
                        podeRegistrarUp = false;

                        String msgJira = "Alerta REDE - VELOCIDADE UPLOAD: " + netUp + "Mbps" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimitePacoteIn) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("pacotein") && pacotesIn > (limite * 1000) && podeRegistrarIn && contIn >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoIn, fkComponenteRede);
                        podeRegistrarIn = false;

                        String msgJira = "Alerta REDE - ENTRADA DE PACOTES: " + pacotesIn + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimitePacoteOut) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("pacoteout") && pacotesOut > (limite * 1000) && podeRegistrarOut && contOut >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoOut, fkComponenteRede);
                        podeRegistrarOut = false;

                        String msgJira = "Alerta REDE - SAÍDA DE PACOTES: " + pacotesOut + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimiteConexao) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("conexao") && conexoes > limite && podeRegistrarConexao && contConexao >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoConexao, fkComponenteRede);
                        podeRegistrarConexao = false;

                        String msgJira = "Alerta REDE - CONEXÕES NA REDE: " + conexoes + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimiteLatencia) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("latencia") && latencia > limite && podeRegistrarLatencia && contLatencia >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoLatencia, fkComponenteRede);
                        podeRegistrarLatencia = false;

                        String msgJira = "Alerta REDE - LATÊNCIA: " + latencia + "ms (milissegundos)" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }

                for (ServidorComponente sc : capturaLimitePerdaPacote) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("perdapacote") && perdaPacote > limite && podeRegistrarPerdaPacote && contPerdaPacote >= 3) {
                        String sqlInsertAlertaRede = "insert into alerta (data_alerta, registro, fkComponente) values (?, ?, ?)";
                        template.update(sqlInsertAlertaRede, dataHoraColeta, codigoPerda, fkComponenteRede);
                        podeRegistrarPerdaPacote = false;

                        String msgJira = "Alerta REDE - PERDA DE PACOTES: " + perdaPacote + "%" + "  -  " + dataHoraColeta;
                        abrirChamadoJira(msgJira);
                    }

                }
                // Le a proxima linha do arquivo
                linha = entrada.readLine();
            } // final do while
        } // final do try
     catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            entrada.close();
        } catch (Exception ignore) {}
        }
    }

    //Não utilizaremos mais essa função

//    public static void enviarAlertaSlack(double cpuPercent, double memPercent, double diskPercent, String
//            timestamp, String hostname, Double limiteCpu, Double limiteRam, Double limiteDisco) {
//
//        if (cpuPercent > limiteCpu || memPercent > limiteRam || diskPercent > limiteDisco) {
//            String alerta = String.format(
//                    "⚠️ *Alerta de uso elevado detectado!*\n" +
//                            "🕒 %s\n" +
//                            "👤 Servidor: %s\n" +
//                            "💻 CPU: %.2f%%\n" +
//                            "🧠 RAM: %.2f%%\n" +
//                            "💾 Disco: %.2f%%",
//                    timestamp, hostname, cpuPercent, memPercent, diskPercent
//            );
//
//            // Cria uma configuração personalizada sem listeners
//            Slack slack = Slack.getInstance();
//
//            try {
//                ChatPostMessageResponse response = slack.methods(SLACK_TOKEN).chatPostMessage(ChatPostMessageRequest.builder()
//                        .channel(CHANNEL)
//                        .text(alerta)
//                        .build());
//
//                if (response.isOk()) {
//                    System.out.println("Alerta enviado para o Slack.");
//                } else {
//                    System.out.println("Erro ao enviar alerta: " + response.getError());
//                }
//            } catch (IOException | SlackApiException e) {
//                System.out.println("Exceção ao enviar alerta: " + e.getMessage());
//            }
//        }
//    }

    public void abrirChamadoJira(String msgJira) {
        String jiraUrl = "https://vitalviewsptech.atlassian.net/rest/api/3/issue";
        String email = "vitalview.sptech@gmail.com";
        String apiToken = "JIRA_TOKEN_AQUI";

        String credentials = email + ":" + apiToken;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        String json = String.format("""
                {
                  "fields": {
                    "project": { "key": "SUP" },
                    "summary": "%s",
                    "description": {
                      "type": "doc",
                      "version": 1,
                      "content": [
                        {
                          "type": "paragraph",
                          "content": [
                            {
                              "text": "%s",
                              "type": "text"
                            }
                          ]
                        }
                      ]
                    },
                    "issuetype": { "name": "Reportar um Incidente" }
                  }
                }
                """, msgJira, msgJira);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(jiraUrl)
                .addHeader("Authorization", basicAuth)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println("Status: " + response.code());
            System.out.println("Body: " + response.body().string());
        } catch (IOException e) {
            System.out.println(e);
        }
    }


}