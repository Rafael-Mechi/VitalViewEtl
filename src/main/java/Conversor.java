import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;

public class Conversor {
    public String converterParaJson(String nomeArq) {
        String caminhoJson = nomeArq.replace(".csv", "") + ".json";

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(nomeArq))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            String[] headers = reader.readNext();

            if (headers == null) {
                System.out.println("Arquivo CSV vazio ou inválido.");
                return null;
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

            caminhoJson = caminhoJson.replace(".csv", "");

            // Agora sim, salva como JSON MESMO:
            try (FileWriter file = new FileWriter(caminhoJson)) {
                file.write(jsonArray.toString(2)); // 2 = indentação bonitinha
            }

            System.out.println("Arquivo JSON gerado com sucesso: " + caminhoJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return caminhoJson;
    }
}
