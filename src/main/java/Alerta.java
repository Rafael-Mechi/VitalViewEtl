import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

//importações slack
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.SlackConfig;


import java.io.IOException;

public class Alerta {
    private LocalDateTime dataAlerta;
    private Integer registro;
    private Integer fkComponente;

    //Variaveis slack
    private static final String SLACK_TOKEN = "";
    private static final String CHANNEL = "#suporte-slack";

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

    public void salvaTabelaAlerta(String nomeArq) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        System.out.println("Pegando informações dos limites do servidor 'srv1'");
        String sqlSelectCpu = "select c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = 'srv1' and  t.nome = 'Cpu'";

        String sqlSelectRam = "select c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = 'srv1' and t.nome = 'Memória'";

        String sqlSelectDisco = "select c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = 'srv1' and t.nome = 'Disco'";

        List<ServidorComponente> capturaLimiteCpu = template.query(sqlSelectCpu, new BeanPropertyRowMapper<>(ServidorComponente.class));
        List<ServidorComponente> capturaLimiteRam = template.query(sqlSelectRam, new BeanPropertyRowMapper<>(ServidorComponente.class));
        List<ServidorComponente> capturaLimiteDisco = template.query(sqlSelectDisco, new BeanPropertyRowMapper<>(ServidorComponente.class));

        Double limiteCpu = capturaLimiteCpu.get(0).getLimite();
        Double limiteRam = capturaLimiteRam.get(0).getLimite();
        Double limiteDisco = capturaLimiteDisco.get(0).getLimite();

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

            // separa cada item da linha usando o delimitador ;
            registro = linha.split(";");

            String cabecalho = linha;

            // Le a segunda linha do arquivo (1a linha de dados)
            linha = entrada.readLine();
            registro = linha.split(";");

            Double primeiroRegistroCpu = Double.valueOf(registro[2]);
            Double primeiroRegistroRam = Double.valueOf(registro[6]);
            Double primeiroRegistroDisco = Double.valueOf(registro[12]);

            Boolean podeRegistrarCpu = false;
            Boolean podeRegistrarRam = false;
            Boolean podeRegistrarDisco = false;

            Integer contCpu = 0;
            Integer contRam = 0;
            Integer contDisco = 0;


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

            while (linha != null) { // enquanto nao chegou ao final do arquivo
                registro = linha.split(";");
                // converte de String para Integer usando Integer.valueOf
                // Se fosse converter de String para int usa-se Integer.parseInt
                String nomeDaMaquina = registro[0];
                String dataHoraColeta = registro[1];
                Double usoCPU = Double.valueOf(registro[2]);
                Double usoRAM = Double.valueOf(registro[6]);
                Double usoDisco = Double.valueOf(registro[12]);

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

                if (contCpu == 3) {
                    podeRegistrarCpu = true;
                }

                if (contRam == 3) {
                    podeRegistrarRam = true;
                }

                if (contDisco == 3) {
                    podeRegistrarDisco = true;
                }

                for (ServidorComponente sc : capturaLimiteCpu) {
                    String tipoComponente = sc.getNome();
                    Double limite = sc.getLimite();

                    if (tipoComponente.equalsIgnoreCase("cpu") && usoCPU > limite && podeRegistrarCpu) {
                        String sqlInsertAlertaCpu = "insert into alerta (data_alerta, registro, fkComponente) values" +
                                "(?, ?, ?)";
                        template.update(sqlInsertAlertaCpu, dataHoraColeta, usoCPU, 1);
                        podeRegistrarCpu = false;

                        enviarAlertaSlack(usoCPU, usoRAM, usoDisco, dataHoraColeta, nomeDaMaquina, limiteCpu, limiteRam, limiteDisco);
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

                        enviarAlertaSlack(usoCPU, usoRAM, usoDisco, dataHoraColeta, nomeDaMaquina, limiteCpu, limiteRam, limiteDisco);
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

                        enviarAlertaSlack(usoCPU, usoRAM, usoDisco, dataHoraColeta, nomeDaMaquina, limiteCpu, limiteRam, limiteDisco);
                    }

                }
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
    }

        public static void enviarAlertaSlack ( double cpuPercent, double memPercent, double diskPercent, String
        timestamp, String hostname, Double limiteCpu, Double limiteRam, Double limiteDisco){

            if (cpuPercent > limiteCpu || memPercent > limiteRam || diskPercent > limiteDisco) {
                String alerta = String.format(
                        "⚠️ *Alerta de uso elevado detectado!*\n" +
                                "🕒 %s\n" +
                                "👤 Servidor: %s\n" +
                                "💻 CPU: %.2f%%\n" +
                                "🧠 RAM: %.2f%%\n" +
                                "💾 Disco: %.2f%%",
                        timestamp, hostname, cpuPercent, memPercent, diskPercent
                );

                // Cria uma configuração personalizada sem listeners
                Slack slack = Slack.getInstance();

                try {
                    ChatPostMessageResponse response = slack.methods(SLACK_TOKEN).chatPostMessage(ChatPostMessageRequest.builder()
                            .channel(CHANNEL)
                            .text(alerta)
                            .build());

                    if (response.isOk()) {
                        System.out.println("Alerta enviado para o Slack.");
                    } else {
                        System.out.println("Erro ao enviar alerta: " + response.getError());
                    }
                } catch (IOException | SlackApiException e) {
                    System.out.println("Exceção ao enviar alerta: " + e.getMessage());
                }
            }
        };
}