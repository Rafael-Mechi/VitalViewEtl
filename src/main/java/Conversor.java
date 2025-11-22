import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class Conversor {
    public String converterParaJson(InputStream inputStream) throws IOException, CsvValidationException {

        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build();

        String[] headers = reader.readNext();

        if (headers == null) {
            return "[]";
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
                }
                else if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                    obj.put(chave, Boolean.parseBoolean(valor));
                }
                else {
                    obj.put(chave, valor);
                }
            }

            jsonArray.put(obj);
        }

        return jsonArray.toString(2);
    }

}
