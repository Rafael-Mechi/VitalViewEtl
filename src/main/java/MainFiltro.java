import java.io.File;
import java.util.List;

public class MainFiltro {

    public static void main(String[] args) {

        List<String> camposRede = List.of(
                "Net_Down_(Mbps)",
                "Net_Up_(Mbps)",
                "Pacotes_IN_(intervalo)",
                "Pacotes_OUT_(intervalo)",
                "Perda_de_Pacotes_(%)",
                "Conexões_TCP_ESTABLISHED",
                "Latencia_(ms)"
        );

        // quantidade de registros desejados
        int quantidadeUltimos = 1;

        // pasta onde estão separados por servidor
        File pasta = new File("saida_por_servidor");

        for (File json : pasta.listFiles()) {

            if (json.getName().startsWith("saida_") && json.getName().endsWith(".json")) {

                String hostname = json.getName()
                        .replace("saida_", "")
                        .replace(".json", "");

                System.out.println("Filtrando servidor: " + hostname);

                FiltroJsonETL.filtrarCamposUltimosPorMaquina(
                        json.getAbsolutePath(),
                        "rede",
                        camposRede,
                        quantidadeUltimos
                );
            }
        }
    }

}
