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

        /*
        FiltroJsonETL.filtrarCampos(
                "saida.json",
                "rede",
                "rede_completo.json",
                camposRede
        );
        */

        // Só os último registro por máquina
        int quantidadeUltimos = 1;

        FiltroJsonETL.filtrarCamposUltimosPorMaquina(
                "saida.json",
                "rede",
                camposRede,
                quantidadeUltimos
        );
    }
}
