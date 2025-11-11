import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ControleSistema {
    private Integer id;
    private Integer qtdHospital;
    private Integer qtdAlertas;
    private Integer qtdAlertasResolvidos;
    private LocalDateTime dataHoraAlerta;
    private LocalDateTime dataHoraCorrecao;

    public ControleSistema() {}

    public ControleSistema(Integer id, Integer qtdHospital, Integer qtdAlertas, Integer qtdAlertasResolvidos, LocalDateTime dataHoraAlerta, LocalDateTime dataHoraCorrecao) {
        this.id = id;
        this.qtdHospital = qtdHospital;
        this.qtdAlertas = qtdAlertas;
        this.qtdAlertasResolvidos = qtdAlertasResolvidos;
        this.dataHoraAlerta = dataHoraAlerta;
        this.dataHoraCorrecao = dataHoraCorrecao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQtdHospital() {
        return qtdHospital;
    }

    public void setQtdHospital(Integer qtdHospital) {
        this.qtdHospital = qtdHospital;
    }

    public Integer getQtdAlertas() {
        return qtdAlertas;
    }

    public void setQtdAlertas(Integer qtdAlertas) {
        this.qtdAlertas = qtdAlertas;
    }

    public Integer getQtdAlertasResolvidos() {
        return qtdAlertasResolvidos;
    }

    public void setQtdAlertasResolvidos(Integer qtdAlertasResolvidos) {
        this.qtdAlertasResolvidos = qtdAlertasResolvidos;
    }

    public LocalDateTime getDataHoraAlerta() {
        return dataHoraAlerta;
    }

    public void setDataHoraAlerta(LocalDateTime dataHoraAlerta) {
        this.dataHoraAlerta = dataHoraAlerta;
    }

    public LocalDateTime getDataHoraCorrecao() {
        return dataHoraCorrecao;
    }

    public void setDataHoraCorrecao(LocalDateTime dataHoraCorrecao) {
        this.dataHoraCorrecao = dataHoraCorrecao;
    }

    public void calcularProdutividade() {
        System.out.println("Iniciando cálculo de produtividade...");

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        String selectQtdHospitais = "SELECT COUNT(*) AS qtdHospital FROM hospital;";
        List<ControleSistema> resultado = template.query(selectQtdHospitais, new BeanPropertyRowMapper<>(ControleSistema.class));

        Integer totalHospitais = resultado.get(0).getQtdHospital();

        for (int i = 1; i <= totalHospitais; i++) {
            System.out.println("Calculando a produtividade do hospital com id: " + i);

            String qtdAlertasPendentesResolvidos =
                    "SELECT \n" +
                            "    h.idHospital,\n" +
                            "    COUNT(DISTINCT a.id) AS qtdAlertas,\n" +
                            "    COUNT(DISTINCT ca.id) AS qtdAlertasResolvidos\n" +
                            "FROM hospital h\n" +
                            "JOIN servidores s ON s.fkHospital = h.idHospital\n" +
                            "JOIN componentes c ON c.fkServidor = s.idServidor\n" +
                            "JOIN alerta a ON a.fkComponente = c.idComponente\n" +
                            "LEFT JOIN correcao_alerta ca ON ca.fkAlerta = a.id\n" +
                            "WHERE h.idHospital = ?\n" +
                            "GROUP BY h.idHospital;\n";

            String tempoCorrecao =
                    "SELECT \n" +
                            "    a.id AS idAlerta,\n" +
                            "    a.data_alerta AS dataHoraAlerta,\n" +
                            "    ca.data_correcao AS dataHoraCorrecao\n" +
                            "FROM hospital h\n" +
                            "JOIN servidores s ON s.fkHospital = h.idHospital\n" +
                            "JOIN componentes c ON c.fkServidor = s.idServidor\n" +
                            "JOIN alerta a ON a.fkComponente = c.idComponente\n" +
                            "JOIN correcao_alerta ca ON ca.fkAlerta = a.id\n" +
                            "WHERE h.idHospital = ?;\n";

            List<ControleSistema> resultadoQtdAlertas = template.query(qtdAlertasPendentesResolvidos,
                    new BeanPropertyRowMapper<>(ControleSistema.class), i);
            List<ControleSistema> resultadoTempoCorrecao = template.query(tempoCorrecao,
                    new BeanPropertyRowMapper<>(ControleSistema.class), i);

            if (resultadoQtdAlertas.isEmpty()){
                continue;
            }

            ControleSistema dados = resultadoQtdAlertas.get(0);
            Integer totalAlertas = dados.getQtdAlertas();
            Integer totalResolvidos = dados.getQtdAlertasResolvidos();

            if (resultadoTempoCorrecao.isEmpty() || totalAlertas == 0) {
                System.out.println("Sem dados suficientes para calcular produtividade do hospital " + i);
                continue;
            }

            long totalHoras = 0;
            for (ControleSistema r : resultadoTempoCorrecao) {
                Duration diferenca = Duration.between(r.getDataHoraAlerta(), r.getDataHoraCorrecao());
                totalHoras += diferenca.toHours();
            }

            Double mediaMttr = (double) totalHoras / resultadoTempoCorrecao.size();
            Double produtividade = ((double) totalResolvidos / totalAlertas * 100) / mediaMttr;

            System.out.printf("Hospital %d: Produtividade: %.2f%% | MTTR médio: %.2f horas%n", i, produtividade, mediaMttr);

            //gerarJson(id, totalHoras, produtividade, mttr);
        }
    }
}
