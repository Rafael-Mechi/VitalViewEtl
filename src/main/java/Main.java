import org.springframework.jdbc.core.JdbcTemplate;
import java.io.File;
import java.io.IOException;

public class Main {

    private static String extrairHostname(String nomeArquivo) {
        return nomeArquivo
                .replace("captura_", "")
                .replace("id_servidor_", "")
                .replace(".csv", "")
                .trim();
    }

    public static String pegarNomeArquivo(){
        File raiz = new File(".");
        for (File f : raiz.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".csv")) {
                return f.getName();
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        String nomeArquivo = pegarNomeArquivo();

        if(nomeArquivo.contains("imagens")){
            GerImagens gi = new GerImagens();
            String arquivoJson = gi.converterParaJson(nomeArquivo);

            gi.gerarRelatorioJson(arquivoJson);
        }

        else if(nomeArquivo.contains("processos")){
            //...
        }

    }
}
