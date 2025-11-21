import org.springframework.jdbc.core.JdbcTemplate;
import java.io.File;

public class Main {

    private static String extrairHostname(String nomeArquivo) {
        return nomeArquivo
                .replace("captura_", "")
                .replace("id_servidor_", "")
                .replace(".csv", "")
                .trim();
    }

    public static void main(String[] args) {

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        Alerta alerta = new Alerta();
        LeituraCsv leituraCsv = new LeituraCsv();

        File pasta = new File("capturas/");

        for (File f : pasta.listFiles()) {
            if (f.getName().endsWith(".csv")) {

                String hostname = extrairHostname(f.getName());

                System.out.println("Processando CSV do servidor: " + hostname);
                System.out.println("Arquivo: " + f.getName());

                leituraCsv.leImportaArquivoCsv(f.getAbsolutePath(), hostname);
                alerta.salvaTabelaAlerta(f.getAbsolutePath(), hostname);

                leituraCsv.converterParaJson(hostname);
            }
        }

        ControleSistema cs = new ControleSistema();
        cs.calcularProdutividade();
    }
}
