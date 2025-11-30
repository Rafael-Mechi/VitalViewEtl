import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class PrevisaoAlertas {

    private JdbcTemplate template;

    public PrevisaoAlertas() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        this.template = databaseConfiguration.getTemplate();
    }

    public String gerarPrevisoes(String hostname) {
        System.out.println("Iniciando geração de previsões para: " + hostname);

        List<DadoAlerta> historico = buscarHistoricoAlertas(hostname);

        if (historico.isEmpty()) {
            System.out.println("Nenhum histórico encontrado para: " + hostname);
            return "[]";
        }

        Map<String, Map<String, List<DadoAlerta>>> agrupamento = agruparPorServidorComponente(historico);

        // previsões para cada combinação servidor-componente
        JSONArray previsoesArray = new JSONArray();

        for (Map.Entry<String, Map<String, List<DadoAlerta>>> entryServidor : agrupamento.entrySet()) {
            String nomeServidor = entryServidor.getKey();

            for (Map.Entry<String, List<DadoAlerta>> entryComponente : entryServidor.getValue().entrySet()) {
                String nomeComponente = entryComponente.getKey();
                List<DadoAlerta> alertasComponente = entryComponente.getValue();

                // Calcular previsões
                PrevisaoResultado previsao = calcularPrevisao(alertasComponente, nomeServidor, nomeComponente);

                if (previsao != null) {
                    previsoesArray.put(previsao.toJSON());
                }
            }
        }

        System.out.println("Previsões geradas com sucesso: " + previsoesArray.length() + " registros");
        return previsoesArray.toString(2);
    }

    // Pega histórico de alertas dos últimos 30 dias
    private List<DadoAlerta> buscarHistoricoAlertas(String hostname) {
        String sql = """
            SELECT 
                a.data_alerta,
                a.registro,
                s.hostname,
                COALESCE(tc.nome, 'Rede') as componente,
                CASE 
                    WHEN tc.nome IS NULL THEN 
                        CASE a.registro
                            WHEN 1 THEN 'Download'
                            WHEN 2 THEN 'Upload'
                            WHEN 3 THEN 'PacoteIn'
                            WHEN 4 THEN 'PacoteOut'
                            WHEN 5 THEN 'Conexao'
                            WHEN 6 THEN 'Latencia'
                            WHEN 7 THEN 'PerdaPacote'
                            ELSE 'Rede'
                        END
                    ELSE tc.nome
                END as metrica_especifica
            FROM alerta a
            INNER JOIN componentes c ON a.fkComponente = c.idComponente
            INNER JOIN servidores s ON c.fkServidor = s.idServidor
            LEFT JOIN tipoComponente tc ON c.fkTipo = tc.idTipo
            WHERE s.hostname = ?
                AND a.data_alerta >= DATE_SUB(NOW(), INTERVAL 30 DAY)
            ORDER BY a.data_alerta ASC
        """;

        List<Map<String, Object>> rows = template.queryForList(sql, hostname);
        List<DadoAlerta> dados = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            DadoAlerta dado = new DadoAlerta();
            dado.dataAlerta = ((LocalDateTime) row.get("data_alerta"));
            dado.registro = ((Number) row.get("registro")).doubleValue();
            dado.hostname = (String) row.get("hostname");
            dado.componente = (String) row.get("componente");
            dado.metricaEspecifica = (String) row.get("metrica_especifica");
            dados.add(dado);
        }

        return dados;
    }

    private Map<String, Map<String, List<DadoAlerta>>> agruparPorServidorComponente(List<DadoAlerta> historico) {
        Map<String, Map<String, List<DadoAlerta>>> agrupamento = new HashMap<>();

        for (DadoAlerta dado : historico) {
            agrupamento
                    .computeIfAbsent(dado.hostname, k -> new HashMap<>())
                    .computeIfAbsent(dado.metricaEspecifica, k -> new ArrayList<>())
                    .add(dado);
        }

        return agrupamento;
    }

    // Calcula previsão usando regressão linear e análise de tendência
    private PrevisaoResultado calcularPrevisao(List<DadoAlerta> alertas, String servidor, String componente) {
        if (alertas.size() < 3) {
            return null;
        }
        LocalDateTime dataInicio = alertas.get(0).dataAlerta;
        List<Double> x = new ArrayList<>(); // dias desde o início
        List<Double> y = new ArrayList<>(); // qtd de alertas por dia

        Map<LocalDate, Long> alertasPorDia = alertas.stream()
                .collect(Collectors.groupingBy(
                        a -> a.dataAlerta.toLocalDate(),
                        Collectors.counting()
                ));

        for (Map.Entry<LocalDate, Long> entry : alertasPorDia.entrySet()) {
            long diasDesdeInicio = ChronoUnit.DAYS.between(dataInicio.toLocalDate(), entry.getKey());
            x.add((double) diasDesdeInicio);
            y.add(entry.getValue().doubleValue());
        }

        // Calcular regressão linear
        RegressaoLinear regressao = calcularRegressaoLinear(x, y);

        // próximos 7 dias
        double diasFuturos = 7.0;
        double diasAtual = x.get(x.size() - 1);
        double previsaoAlertas = regressao.a + regressao.b * (diasAtual + diasFuturos);

        // dia de maior risco
        Map<DayOfWeek, Long> alertasPorDiaSemana = alertas.stream()
                .collect(Collectors.groupingBy(
                        a -> a.dataAlerta.getDayOfWeek(),
                        Collectors.counting()
                ));

        DayOfWeek diaMaiorRisco = alertasPorDiaSemana.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(DayOfWeek.MONDAY);

        // Calcular próxima ocorrência do dia de risco
        LocalDate proximoDiaRisco = calcularProximaOcorrencia(diaMaiorRisco);

        // Análise de tendência
        String tendencia = regressao.b > 0.5 ? "Crescente" :
                regressao.b < -0.5 ? "Decrescente" : "Estável";

        // probabilidade baseada em R²
        double probabilidade = Math.min(95.0, Math.max(50.0, regressao.r2 * 100));

        // nível de risco
        String nivelRisco = determinarNivelRisco(previsaoAlertas, regressao.b);

        PrevisaoResultado resultado = new PrevisaoResultado();
        resultado.servidor = servidor;
        resultado.componente = componente;
        resultado.diaRisco = proximoDiaRisco.toString();
        resultado.periodo = "Próximos 7 dias";
        resultado.alertasPrevistos = Math.max(0, (int) Math.round(previsaoAlertas));
        resultado.probabilidade = probabilidade;
        resultado.tendencia = tendencia;
        resultado.nivelRisco = nivelRisco;

        return resultado;
    }

    // regressão linear
    private RegressaoLinear calcularRegressaoLinear(List<Double> x, List<Double> y) {
        int n = x.size();
        double somaX = 0, somaY = 0, somaXY = 0, somaX2 = 0, somaY2 = 0;

        for (int i = 0; i < n; i++) {
            somaX += x.get(i);
            somaY += y.get(i);
            somaXY += x.get(i) * y.get(i);
            somaX2 += x.get(i) * x.get(i);
            somaY2 += y.get(i) * y.get(i);
        }

        double mediaX = somaX / n;
        double mediaY = somaY / n;

        // Coeficiente angular
        double b = (somaXY - n * mediaX * mediaY) / (somaX2 - n * mediaX * mediaX);

        // Coeficiente linear
        double a = mediaY - b * mediaX;

        // Calcular R²
        double sqTotal = somaY2 - n * mediaY * mediaY;
        double sqRes = 0;
        for (int i = 0; i < n; i++) {
            double yPred = a + b * x.get(i);
            sqRes += Math.pow(y.get(i) - yPred, 2);
        }
        double r2 = 1 - (sqRes / sqTotal);

        RegressaoLinear resultado = new RegressaoLinear();
        resultado.a = a;
        resultado.b = b;
        resultado.r2 = Math.max(0, Math.min(1, r2)); // entre 0 e 1

        return resultado;
    }

    // Calcula próxima ocorrência de um dia da semana
    private LocalDate calcularProximaOcorrencia(DayOfWeek diaSemana) {
        LocalDate hoje = LocalDate.now();
        LocalDate proxima = hoje;

        while (proxima.getDayOfWeek() != diaSemana || proxima.equals(hoje)) {
            proxima = proxima.plusDays(1);
        }

        return proxima;
    }

    // Determina nível de risco baseado na quantidade prevista e tendência
    private String determinarNivelRisco(double alertasPrevistos, double tendencia) {
        if (alertasPrevistos >= 15 || tendencia > 1.0) {
            return "Alto";
        } else if (alertasPrevistos >= 8 || tendencia > 0.5) {
            return "Médio";
        } else {
            return "Baixo";
        }
    }

    // Classes auxiliares
    private static class DadoAlerta {
        LocalDateTime dataAlerta;
        Double registro;
        String hostname;
        String componente;
        String metricaEspecifica;
    }

    private static class RegressaoLinear {
        Double a; // Coeficiente linear
        Double b; // Coeficiente angular
        Double r2; // Coeficiente de determinação
    }

    private static class PrevisaoResultado {
        String servidor;
        String componente;
        String diaRisco;
        String periodo;
        Integer alertasPrevistos;
        Double probabilidade;
        String tendencia;
        String nivelRisco;

        JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            obj.put("servidor", servidor);
            obj.put("componente", componente);
            obj.put("diaRisco", diaRisco);
            obj.put("periodo", periodo);
            obj.put("alertasPrevistos", alertasPrevistos);
            obj.put("probabilidade", probabilidade);
            obj.put("tendencia", tendencia);
            obj.put("nivelRisco", nivelRisco);
            return obj;
        }
    }
}