import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        leImportaArquivoCsv("captura_total.csv");
    }

    public static void leImportaArquivoCsv (String nomeArq) {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

        System.out.println("Pegando informações de hostname, limite do componente de cada componente...");
        String sqlSelect = "select s.hostname, c.limite, t.nome from componentes c\n" +
                "inner join servidores s on c.fkServidor = s.idServidor\n" +
                "inner join tipoComponente t on t.idTipo = c.fkTipo\n" +
                "where hostname = 'LucasRodrigues'";
        List<ServidorComponente> capturas = template.query(sqlSelect, new BeanPropertyRowMapper<>(ServidorComponente.class));


        Reader arq = null;
        BufferedReader entrada = null;
        BufferedWriter saida = null;

        List<Captura> listaLida = new ArrayList<>();

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
            String cabecalho = linha + ",alertaCpu,alertaRam,alertaDisco";
            saida.write(cabecalho);
            saida.newLine();

            // separa cada item da linha usando o delimitador ;
            registro = linha.split(",");

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


                String alertaCpu = "não";
                String alertaRam = "não";
                String alertaDisco = "não";


                for (ServidorComponente c : capturas) {
                    String tipoComponente = c.getNome();
                    Double limite = c.getLimite();

                    if(tipoComponente.equalsIgnoreCase("cpu") && usoCPU > limite){
                        alertaCpu = "sim";
                    } else if(tipoComponente.equalsIgnoreCase("memória") && usoRAM > limite){
                        alertaRam = "sim";
                    } else if(tipoComponente.equalsIgnoreCase("disco") && usoDisco > limite){
                        alertaDisco = "sim";
                    }

                }
                // escreve a linha de dados + alertas
                String novaLinha = linha + "," + alertaCpu + "," + alertaRam + "," + alertaDisco;
                saida.write(novaLinha);
                saida.newLine();

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

        System.out.println("Processo finalizado");

    }

}
