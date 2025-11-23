import org.springframework.jdbc.core.JdbcTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrevisaoAlertas {

    private static final Integer JANELA_ANALISE = 10;

    private DatabaseConfiguration databaseConfiguration;
    private JdbcTemplate template;

    private static class HistoricoMetrica {
        Queue<Double> valores;
        Queue<LocalDateTime> timestamps;

        public HistoricoMetrica() {
            this.valores = new LinkedList<>();
            this.timestamps = new LinkedList<>();
        }

        public void addValor(Double valor, LocalDateTime timestamp) {
            if (valores.size() >= JANELA_ANALISE) {
                valores.poll();
                timestamps.poll();
            }
            valores.add(valor);
            timestamps.add(timestamp);
        }

        public Double calcularTendencia() {
            if (valores.size() < 3) return 0.0;

            List<Double> lista = new ArrayList<>(valores);
            Double somaIncremento = 0.0;
            Integer count = 0;

            for (Integer i = 1; i < lista.size(); i++) {
                somaIncremento += lista.get(i) - lista.get(i - 1);
                count++;
            }

            return count > 0 ? somaIncremento / count : 0.0;
        }

        public Double calcularMedia() {
            return valores.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
        }

        public Double calcularDesvioPadrao() {
            Double media = calcularMedia();
            return Math.sqrt(valores.stream()
                    .mapToDouble(v -> Math.pow(v - media, 2))
                    .average()
                    .orElse(0.0));
        }

        public LocalDateTime getUltimoTimestamp() {
            return timestamps.isEmpty() ? LocalDateTime.now() :
                    new ArrayList<>(timestamps).get(timestamps.size() - 1);
        }
    }

    private Map<String, Map<String, HistoricoMetrica>> historicoServidor;

    public PrevisaoAlertas() {
        this.databaseConfiguration = new DatabaseConfiguration();
        this.template = databaseConfiguration.getTemplate();
        this.historicoServidor = new HashMap<>();
    }

    public String gerarPrevisaoJson(InputStream inputStream, String hostname) throws IOException {

        Map<String, Double> limites = obterLimitesServidor(hostname);

        if (!historicoServidor.containsKey(hostname)) {
            historicoServidor.put(hostname, new HashMap<>());
        }

        Map<String, HistoricoMetrica> historico = historicoServidor.get(hostname);

        Reader arq = null;
        BufferedReader entrada = null;

        try {
            arq = new InputStreamReader(inputStream, "UTF-8");
            entrada = new BufferedReader(arq);

            String linha = entrada.readLine();
            linha = entrada.readLine();

            while (linha != null) {
                String[] registro = linha.split(";");

                if (registro.length > 22) {
                    try {
                        String dataDaColeta = registro[0];
                        Double usoCPU = parseDouble(registro[2]);
                        Double usoRAM = parseDouble(registro[3]);
                        Double usoDisco = parseDouble(registro[6]);
                        Double netDown = parseDouble(registro[16]);
                        Double netUp = parseDouble(registro[17]);
                        Long pacotesIn = parseLong(registro[18]);
                        Long pacotesOut = parseLong(registro[19]);
                        Integer conexoes = parseInteger(registro[20]);
                        Double latencia = parseDouble(registro[21]);
                        Double perdaPacote;

                        if (registro.length > 22 && registro[22] != null && !registro[22].isEmpty()) {
                            perdaPacote = parseDouble(registro[22]);
                        } else {
                            perdaPacote = 0.0;
                        }

                        LocalDateTime dataHoraColeta = LocalDateTime.parse(
                                dataDaColeta,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        );

                        processarMetrica(historico, "CPU", usoCPU, limites.get("CPU"), dataHoraColeta);
                        processarMetrica(historico, "RAM", usoRAM, limites.get("RAM"), dataHoraColeta);
                        processarMetrica(historico, "DISCO", usoDisco, limites.get("DISCO"), dataHoraColeta);
                        processarMetrica(historico, "NET_DOWN", netDown, limites.get("NET_DOWN"), dataHoraColeta);
                        processarMetrica(historico, "NET_UP", netUp, limites.get("NET_UP"), dataHoraColeta);
                        processarMetrica(historico, "PACOTE_IN", pacotesIn.doubleValue(),
                                limites.get("PACOTE_IN") * 1000, dataHoraColeta);
                        processarMetrica(historico, "PACOTE_OUT", pacotesOut.doubleValue(),
                                limites.get("PACOTE_OUT") * 1000, dataHoraColeta);
                        processarMetrica(historico, "CONEXAO", conexoes.doubleValue(),
                                limites.get("CONEXAO"), dataHoraColeta);
                        processarMetrica(historico, "LATENCIA", latencia, limites.get("LATENCIA"), dataHoraColeta);
                        processarMetrica(historico, "PERDA_PACOTE", perdaPacote,
                                limites.get("PERDA_PACOTE"), dataHoraColeta);

                    } catch (Exception e) {

                    }
                }

                linha = entrada.readLine();
            }

            return gerarJSONPrevisoes(hostname, historico, limites);

        } catch (IOException erro) {
            System.out.println("Erro ao ler o arquivo: " + erro.getMessage());
            erro.printStackTrace();
            return "[]";
        } finally {
            try {
                if (entrada != null) entrada.close();
                if (arq != null) arq.close();
            } catch (IOException erro) {
                System.out.println("Erro ao fechar o arquivo");
            }
        }
    }

    private void processarMetrica(Map<String, HistoricoMetrica> historico, String nomeMetrica,
                                  Double valor, Double limite, LocalDateTime timestamp) {
        if (!historico.containsKey(nomeMetrica)) {
            historico.put(nomeMetrica, new HistoricoMetrica());
        }

        historico.get(nomeMetrica).addValor(valor, timestamp);
    }

    private String gerarJSONPrevisoes(String hostname, Map<String, HistoricoMetrica> historico,
                                      Map<String, Double> limites) {
        JSONArray previsoes = new JSONArray();

        for (Map.Entry<String, HistoricoMetrica> entry : historico.entrySet()) {
            String metrica = entry.getKey();
            HistoricoMetrica hist = entry.getValue();
            Double limite = limites.get(metrica);

            if (limite == null || hist.valores.size() < 3) continue;

            Double media = hist.calcularMedia();
            Double tendencia = hist.calcularTendencia();
            Double desvioPadrao = hist.calcularDesvioPadrao();

            Double probabilidade = calcularProbabilidadeAlerta(media, tendencia, desvioPadrao, limite);

            if (probabilidade > 0.5) {
                LocalDateTime dataPrevisao = estimarDataAlerta(hist.getUltimoTimestamp(),
                        media, tendencia, limite);

                JSONObject previsao = new JSONObject();
                previsao.put("servidor", hostname);
                previsao.put("componente", formatarNomeComponente(metrica));
                previsao.put("dia_alerta", dataPrevisao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                previsao.put("periodo", obterPeriodoDoDia(dataPrevisao));
                previsao.put("probabilidade", Math.round(probabilidade * 100));
                previsao.put("nivel_risco", classificarNivelRisco(probabilidade));
                previsao.put("timestamp", dataPrevisao.toString());

                previsoes.put(previsao);
            }
        }

        System.out.println("JSON de previsoes gerado para " + hostname);
        return previsoes.toString(2);
    }

    private Double calcularProbabilidadeAlerta(Double media, Double tendencia,
                                               Double desvioPadrao, Double limite) {
        Double proximidade = media / limite;
        Double fatorTendencia = tendencia > 0 ? Math.min(1.0, tendencia / 10.0) : 0.0;
        Double fatorVolatilidade = Math.min(1.0, desvioPadrao / limite);

        Double probabilidade = (proximidade * 0.5) + (fatorTendencia * 0.3) + (fatorVolatilidade * 0.2);

        return Math.min(1.0, probabilidade);
    }

    private LocalDateTime estimarDataAlerta(LocalDateTime ultimoTimestamp, Double media,
                                            Double tendencia, Double limite) {
        if (tendencia <= 0 || media >= limite) {
            return ultimoTimestamp.plusHours(1);
        }

        Double diferenca = limite - media;
        Integer minutosAteAlerta = (int) Math.ceil(diferenca / tendencia);

        if (minutosAteAlerta < 1) minutosAteAlerta = 1;
        if (minutosAteAlerta > 1440) minutosAteAlerta = 1440;

        return ultimoTimestamp.plusMinutes(minutosAteAlerta);
    }

    private String obterPeriodoDoDia(LocalDateTime dataHora) {
        Integer hora = dataHora.getHour();

        if (hora >= 0 && hora < 6) {
            return "Madrugada";
        } else if (hora >= 6 && hora < 12) {
            return "Manha";
        } else if (hora >= 12 && hora < 18) {
            return "Tarde";
        } else {
            return "Noite";
        }
    }

    private String classificarNivelRisco(Double probabilidade) {
        if (probabilidade >= 0.8) return "Critico";
        if (probabilidade >= 0.65) return "Alto";
        if (probabilidade >= 0.5) return "Medio";
        return "Baixo";
    }

    private String formatarNomeComponente(String metrica) {
        switch(metrica) {
            case "NET_DOWN": return "Rede - Download";
            case "NET_UP": return "Rede - Upload";
            case "PACOTE_IN": return "Rede - Pacotes IN";
            case "PACOTE_OUT": return "Rede - Pacotes OUT";
            case "CONEXAO": return "Rede - Conexoes";
            case "LATENCIA": return "Rede - Latencia";
            case "PERDA_PACOTE": return "Rede - Perda de Pacotes";
            default: return metrica;
        }
    }

    private Map<String, Double> obterLimitesServidor(String hostname) {
        Map<String, Double> limites = new HashMap<>();

        try {
            limites.put("CPU", queryLimite("Cpu", hostname));
            limites.put("RAM", queryLimite("Memória", hostname));
            limites.put("DISCO", queryLimite("Disco", hostname));
            limites.put("NET_DOWN", queryLimiteRede("Download", hostname));
            limites.put("NET_UP", queryLimiteRede("Upload", hostname));
            limites.put("PACOTE_IN", queryLimiteRede("PacoteIn", hostname));
            limites.put("PACOTE_OUT", queryLimiteRede("PacoteOut", hostname));
            limites.put("CONEXAO", queryLimiteRede("Conexao", hostname));
            limites.put("LATENCIA", queryLimiteRede("Latencia", hostname));
            limites.put("PERDA_PACOTE", queryLimiteRede("PerdaPacote", hostname));
        } catch (Exception e) {
            System.out.println("Erro ao obter limites: " + e.getMessage());
        }

        return limites;
    }

    private Double queryLimite(String tipoComponente, String hostname) {
        String sql = "SELECT c.limite FROM componentes c " +
                "INNER JOIN servidores s ON c.fkServidor = s.idServidor " +
                "INNER JOIN tipoComponente t ON t.idTipo = c.fkTipo " +
                "WHERE hostname = ? AND t.nome = ?";

        try {
            return template.queryForObject(sql, Double.class, hostname, tipoComponente);
        } catch (Exception e) {
            return 100.0;
        }
    }

    private Double queryLimiteRede(String metrica, String hostname) {
        String sql = "SELECT lr.limite FROM limiteMetrica lr " +
                "INNER JOIN servidores s ON lr.fkServidor = s.idServidor " +
                "INNER JOIN metrica r ON r.idMetrica = lr.fkMetrica " +
                "WHERE s.hostname = ? AND r.metrica = ?";

        try {
            return template.queryForObject(sql, Double.class, hostname, metrica);
        } catch (Exception e) {
            return 100.0;
        }
    }

    private Double parseDouble(String valor) {
        try {
            return Double.valueOf(valor);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Long parseLong(String valor) {
        try {
            return Long.valueOf(valor);
        } catch (Exception e) {
            return 0L;
        }
    }

    private Integer parseInteger(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return 0;
        }
    }
}