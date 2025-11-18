import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FiltroJsonETL {

    public static void filtrarCampos(
            String origem,
            String pastaDestino,
            String arquivoDestino,
            List<String> camposDesejados
    ) {

        try {

            String jsonStr = Files.readString(Paths.get(origem));
            JSONArray jsonArrayOriginal = new JSONArray(jsonStr);

            JSONArray jsonFiltrado = new JSONArray();

            for (Object obj : jsonArrayOriginal) {
                JSONObject original = (JSONObject) obj;
                JSONObject novo = new JSONObject();


                novo.put("Nome_da_Maquina", original.get("Nome_da_Maquina"));
                novo.put("Data_da_Coleta", original.get("Data_da_Coleta"));


                for (String campo : camposDesejados) {
                    if (original.has(campo)) {
                        novo.put(campo, original.get(campo));
                    }
                }

                jsonFiltrado.put(novo);
            }


            Path destino = Paths.get(pastaDestino, arquivoDestino);
            Files.createDirectories(destino.getParent());
            Files.writeString(destino, jsonFiltrado.toString(2));

            System.out.println("Arquivo gerado em: " + destino.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Erro ao processar JSON: " + e.getMessage());
        }
    }
}
