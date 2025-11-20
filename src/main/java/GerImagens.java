import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class GerImagens {

    public GerImagens(){}


    public void converterParaJson(String nomeArq) {
        nomeArq = "1_srv1_hsl.csv";
        String caminhoJson = "1_srv1_hsl.json";

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(nomeArq))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            String[] headers = reader.readNext();

            if (headers == null) {
                System.out.println("Arquivo CSV vazio ou inválido.");
                return;
            }

            JSONArray jsonArray = new JSONArray();
            String[] line;

            while ((line = reader.readNext()) != null) {
                JSONObject obj = new JSONObject();

                for (int i = 0; i < headers.length && i < line.length; i++) {
                    String chave = headers[i].trim();
                    String valor = line[i].trim();

                    if (valor.matches("^-?\\d+(\\.\\d+)?$")) {
                        obj.put(chave, Double.parseDouble(valor));
                    } else if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                        obj.put(chave, Boolean.parseBoolean(valor));
                    } else {
                        obj.put(chave, valor);
                    }
                }

                jsonArray.put(obj);
            }

            // Agora sim, salva como JSON MESMO:
            try (FileWriter file = new FileWriter(caminhoJson)) {
                file.write(jsonArray.toString(2)); // 2 = indentação bonitinha
            }

            System.out.println("Arquivo JSON gerado com sucesso: " + caminhoJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gerarRelatorioJson(String nomeArq) throws IOException {
        Double somaMediaImagem = 0.0;
        Double somaMediaMes = 0.0;

        String json = Files.readString(Path.of(nomeArq));

        JSONArray arr = new JSONArray(json);

        JSONObject root = new JSONObject();
        JSONObject resultado = new JSONObject();

        JSONArray imagensRemoviveis = new JSONArray();

        JSONObject imagemMaisPesada = new JSONObject();
        imagemMaisPesada = arr.getJSONObject(0);

        JSONObject crescimentoMensal = new JSONObject();

        // for para descobrir a imagem mais pesada
        for(int i = 1; i < arr.length(); i++){
            if(arr.getJSONObject(i).getDouble("tamanho") < imagemMaisPesada.getDouble("tamanho")){
                imagemMaisPesada = arr.getJSONObject(i);
            }
        }

        resultado.put("imagem_mais_pesada", imagemMaisPesada);

        // for para adicionar imagem na lisa de imagens que pode ser deletadas
        for(int i = 0; i < arr.length(); i++){
            JSONObject img = new JSONObject();

            String dataStr = arr.getJSONObject(i).getString("data_geracao");
            LocalDate data = LocalDate.parse(dataStr);

            if(data.plusYears(5).isBefore(LocalDate.now())){
                imagensRemoviveis.put(img);
            }
        }
        resultado.put("imagens_removives", imagensRemoviveis);

        // for para calcular tamanho medio das imagens
        for(int i = 0; i < arr.length(); i++){
            somaMediaImagem = 0.0;

            somaMediaImagem += arr.getJSONObject(i).getDouble("tamanho");
        }

        Double tamandoMedioImagem = somaMediaImagem / arr.length();

        // calculo para 
    }
}