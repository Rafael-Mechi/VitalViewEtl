import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        System.out.println("ID DE CADA COMPONENTE");
        String sqlSelect = "SELECT * FROM componentes";
        List<Componente> capturas = template.query(sqlSelect, new BeanPropertyRowMapper<>(Componente.class));
        for (Componente c : capturas) {
            System.out.println(c.getIdComponente());
        }

        template.execute(sqlSelect);

        System.out.println("LENDO O CSV");

        leImportaArquivoCsv("captura_total.csv");
    }

    public static void leImportaArquivoCsv (String nomeArq) {
        Reader arq = null;
        BufferedReader entrada = null;
        List<Captura> listaLida = new ArrayList<>();

        // Bloco try-catch para abrir o arquivo
        try {
            arq = new InputStreamReader(new FileInputStream(nomeArq), "UTF-8");
            entrada = new BufferedReader(arq);
        } catch (IOException erro) {
            System.out.println("Erro na abertura do arquivo");
            System.exit(1);
        }

        try {
            String[] registro;      // registro eh um vetor que armazenara cada parte da linha do arquivo
            // readLine() eh usado   para ler uma linha inteira do arquivo
            // Le a primeira linha do arquivo, que eh o cabecalho
            String linha = entrada.readLine(); // linha eh a primeira linha do arquivo

            // separa cada item da linha usando o delimitador ;
            registro = linha.split(",");
            // printa os títulos das colunas
            System.out.printf(
                    "%-15s %-20s %-10s %-6s %-6s %-6s %-10s %-15s %-15s %-10s %-15s %-15s %-10s %-15s %-15s %-15s %-15s %-15s %-10s %-10s %-10s %-20s %-8s %-25s %-12s %-12s %-8s %-15s %-15s %-15s %-20s %-10s\n",
                    registro[0],  // Nome da Maquina
                    registro[1],  // Data da Coleta
                    registro[2],  // Uso de CPU
                    registro[3],  // Load1
                    registro[4],  // Load5
                    registro[5],  // Load15
                    registro[6],  // Usogit stat de RAM
                    registro[7],  // RAM total (bytes)
                    registro[8],  // RAM usada (bytes)
                    registro[9],  // Uso de Swap
                    registro[10], // Swap total (bytes)
                    registro[11], // Swap usada (bytes)
                    registro[12], // Uso de Disco
                    registro[13], // Disco total (bytes)
                    registro[14], // Disco usado (bytes)
                    registro[15], // Disco livre (bytes)
                    registro[16], // Net bytes enviados
                    registro[17], // Net bytes recebidos
                    registro[18], // Freq CPU (MHz)
                    registro[19], // Temp CPU (C)
                    registro[20], // Uptime (s)
                    registro[21], // Processo
                    registro[22], // PID
                    registro[23], // Usuario
                    registro[24], // CPU proc (%)
                    registro[25], // MEM proc (%)
                    registro[26], // Threads
                    registro[27], // RSS (bytes)
                    registro[28], // IO Leitura (bytes)
                    registro[29], // IO Escrita (bytes)
                    registro[30], // Quando foi iniciado
                    registro[31]  // Status
            );

            // Le a segunda linha do arquivo (1a linha de dados)
            linha = entrada.readLine();

            while (linha != null) { // enquanto nao chegou ao final do arquivo
                registro = linha.split(",");
                // converte de String para Integer usando Integer.valueOf
                // Se fosse converter de String para int usa-se Integer.parseInt
                String nomeDaMaquina = registro[0];
                String dataDaColeta = registro[1];
                Double usoCPU = Double.valueOf(registro[2]);
                Double load1 = Double.valueOf(registro[3]);
                Double load5 = Double.valueOf(registro[4]);
                Double load15 = Double.valueOf(registro[5]);
                Double usoRAM = Double.valueOf(registro[6]);
                Long ramTotal = Long.valueOf(registro[7]);
                Long ramUsada = Long.valueOf(registro[8]);
                Double usoSwap = Double.valueOf(registro[9]);
                Long swapTotal = Long.valueOf(registro[10]);
                Long swapUsada = Long.valueOf(registro[11]);
                Double usoDisco = Double.valueOf(registro[12]);
                Long discoTotal = Long.valueOf(registro[13]);
                Long discoUsado = Long.valueOf(registro[14]);
                Long discoLivre = Long.valueOf(registro[15]);
                Long netBytesEnviados = Long.valueOf(registro[16]);
                Long netBytesRecebidos = Long.valueOf(registro[17]);
                Double freqCPU = Double.valueOf(registro[18]);
                Double tempCPU = Double.valueOf(registro[19]);
                Long uptime = Long.valueOf(registro[20]);
                String processo = registro[21];
                Integer pid = Integer.valueOf(registro[22]);
                String usuario = registro[23];
                Double cpuProc = Double.valueOf(registro[24]);
                Double memProc = Double.valueOf(registro[25]);
                Integer threads = Integer.valueOf(registro[26]);
                Long rss = Long.valueOf(registro[27]);
                Long ioLeitura = Long.valueOf(registro[28]);
                Long ioEscrita = Long.valueOf(registro[29]);
                String quandoFoiIniciado = registro[30];
                String status = registro[31];


// Cria o objeto Captura usando a ordem de argumentos da classe
                Captura captura = new Captura(
                        nomeDaMaquina, dataDaColeta, usoCPU, load1, load5, load15,
                        usoRAM, ramTotal, ramUsada, usoSwap, swapTotal, swapUsada,
                        usoDisco, discoTotal, discoUsado, discoLivre,
                        netBytesEnviados, netBytesRecebidos, freqCPU, tempCPU, uptime,
                        processo, pid, usuario, cpuProc, memProc, threads, rss,
                        ioLeitura, ioEscrita, quandoFoiIniciado, status
                );

                // Adiciona o objeto criado na listaLida
                // Dessa forma, estamos "importando" os dados lidos do arquivo
                // Pode-se trabalhar com os dados na lista ou salvar o conteuda da lista
                // em um Banco de dados se for necessario
                listaLida.add(captura);

                // Exibe os dados lidos
                System.out.printf(
                        "%-19s %-10s %6.2f%% CPU %6.2f%% RAM %6.2f%% Disco\n",
                        nomeDaMaquina,
                        dataDaColeta,
                        usoCPU,
                        usoRAM,
                        usoDisco
                );


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

}
