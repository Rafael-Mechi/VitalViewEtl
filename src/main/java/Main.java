import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {

        //OBS: (Antes de rodar arrume as config do bd, coloque os tokens do jira, slack)

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

//        Alerta alerta = new Alerta();
//        LeituraCsv leituraCsv = new LeituraCsv();

        //leituraCsv.leImportaArquivoCsv("captura_srv1.csv");
        //alerta.salvaTabelaAlerta("captura_srv1.csv");
        //leituraCsv.converterParaJson();

        ControleSistema cs = new ControleSistema();

        cs.calcularProdutividade();
    }
}