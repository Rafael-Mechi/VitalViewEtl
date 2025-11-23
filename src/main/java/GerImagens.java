import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class GerImagens extends Conversor{

    public GerImagens(){}

    public String gerarRelatorioJson(String json) throws IOException {
        Double somaTamanho = 0.0;

        JSONArray arr = new JSONArray(json);

        JSONObject resultado = new JSONObject();

        // for para descobrir a imagem mais pesada
        JSONObject imagemMaisPesada = arr.getJSONObject(0);

        for(int i = 1; i < arr.length(); i++){
            JSONObject atual = arr.getJSONObject(i);
            if(atual.getDouble("tamanho") > imagemMaisPesada.getDouble("tamanho")){
                imagemMaisPesada = atual;
            }
        }

        resultado.put("imagem_mais_pesada", imagemMaisPesada);

        // for para descobrir as imagens que podem ser removidas
        JSONArray imagensRemoviveis = new JSONArray();

        for(int i = 0; i < arr.length(); i++){
            JSONObject img = arr.getJSONObject(i);

            LocalDate data = LocalDate.parse(img.getString("data_geracao"));

            if(data.plusYears(5).isBefore(LocalDate.now())){
                imagensRemoviveis.put(img);
            }
        }

        resultado.put("imagens_removiveis", imagensRemoviveis);

        // calculo para descobrir o tamanho médio das imagens
        for(int i = 0; i < arr.length(); i++){
            somaTamanho += arr.getJSONObject(i).getDouble("tamanho");
        }

        Double tamanhoMedio = somaTamanho / arr.length();

        JSONObject tamanhoMedioImagens = new JSONObject();
        tamanhoMedioImagens.put("tamanho_medio", tamanhoMedio);

        resultado.put("tamanho_medio_imagens", tamanhoMedioImagens);

        // calculo para descobrir a média de imagens por mês
        Integer mesAtual = LocalDate.now().getMonthValue(); // número do mês (1..12)

        Double mediaPorMes = (double) arr.length() / mesAtual;

        JSONObject mediaImagensMes = new JSONObject();
        mediaImagensMes.put("media_imagens_por_mes", mediaPorMes);

        resultado.put("crescimento_medio_mes", mediaImagensMes);

        JSONObject quantMeses = new JSONObject();

        int[] meses = new int[12];
        Integer anoAtual = LocalDate.now().getYear();

        for(int i = 0; i < arr.length(); i++){
            LocalDate dataArquivo = LocalDate.parse(arr.getJSONObject(i).getString("data_geracao"));

            if(dataArquivo.getYear() == anoAtual){
                Integer mesArquivo = dataArquivo.getMonthValue();

                meses[mesArquivo - 1]++;
            }
        }

        quantMeses.put("jan", meses[0]);
        quantMeses.put("fev", meses[1]);
        quantMeses.put("mar", meses[2]);
        quantMeses.put("abr", meses[3]);
        quantMeses.put("mai", meses[4]);
        quantMeses.put("jun", meses[5]);
        quantMeses.put("jul", meses[6]);
        quantMeses.put("ago", meses[7]);
        quantMeses.put("set", meses[8]);
        quantMeses.put("out", meses[9]);
        quantMeses.put("nov", meses[10]);
        quantMeses.put("dez", meses[11]);

        resultado.put("crescimento_mensal", quantMeses);

        System.out.println("Relatório JSON de imagens finalizado");
        return resultado.toString();
    }
}