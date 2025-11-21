import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    // pegando só o último registro e sobrescrevendo o json de rede uiiirrr 🙃
    public static void filtrarCamposUltimosPorMaquina(
            String origem,
            String pastaDestino,
            List<String> camposDesejados,
            Integer quantidadeUltimos
    ) {
        try {
            String jsonStr = Files.readString(Paths.get(origem));
            JSONArray jsonArrayOriginal = new JSONArray(jsonStr);

            Set<String> maquinas = new HashSet<>();
            for (Object obj : jsonArrayOriginal) {
                JSONObject original = (JSONObject) obj;
                maquinas.add(original.getString("Nome_da_Maquina"));
            }

            for (String maquina : maquinas) {

                JSONArray jsonMaquina = new JSONArray();
                List<JSONObject> registrosMaquina = new ArrayList<>();

                for (Object obj : jsonArrayOriginal) {
                    JSONObject original = (JSONObject) obj;

                    if (original.getString("Nome_da_Maquina").equals(maquina)) {
                        JSONObject novo = new JSONObject();

                        novo.put("Nome_da_Maquina", maquina);
                        novo.put("Data_da_Coleta", original.get("Data_da_Coleta"));

                        for (String campo : camposDesejados) {
                            if (original.has(campo)) {
                                novo.put(campo, original.get(campo));
                            }
                        }

                        registrosMaquina.add(novo);
                    }
                }

                int tamanho = registrosMaquina.size();
                int inicio = Math.max(0, tamanho - quantidadeUltimos);

                for (int i = inicio; i < tamanho; i++) {
                    jsonMaquina.put(registrosMaquina.get(i));
                }

                Path dirMaquina = Paths.get(pastaDestino, maquina);
                Files.createDirectories(dirMaquina);

                Path destino = dirMaquina.resolve("rede_" + maquina + ".json");

                Files.writeString(destino, jsonMaquina.toString(2));

                System.out.println("Arquivo gerado para " + maquina);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar: " + e.getMessage());
        }
    }

}
