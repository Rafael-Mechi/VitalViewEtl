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
        String json = Files.readString(Path.of(nomeArq));

        JSONArray arr = new JSONArray(json);

        JSONObject root = new JSONObject();
        JSONObject resultado = new JSONObject();

        JSONArray imagensRemoviveis = new JSONArray();

        JSONObject imagemMaisPesada = new JSONObject();
        imagemMaisPesada = arr.getJSONObject(0);

        JSONObject crescimentoMensal = new JSONObject();

        for(int i = 1; i < arr.length(); i++){
            if(arr.getJSONObject(i).getDouble("tamanho") < imagemMaisPesada.getDouble("tamanho")){
                imagemMaisPesada = arr.getJSONObject(i);
            }
        }

        resultado.put("imagem_mais_pesada", imagemMaisPesada);


    }
}
