import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.lambda.runtime.events.S3Event;

public class Main implements RequestHandler<S3Event, String>{
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private static final String DESTINATION_BUCKET = "bucket-trusted-vw";

    public static void main(String[] args) throws IOException {

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        JdbcTemplate template = databaseConfiguration.getTemplate();

    }

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        // arquivo que chegou
        String sourceBucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String sourceKey = s3Event.getRecords().get(0).getS3().getObject().getKey();

        context.getLogger().log("Processando arquivo: " + sourceBucket + "/" + sourceKey);

        try {

            // 1. pegar CSV do bucket de origem
            S3Object s3Object = s3Client.getObject(sourceBucket, sourceKey);
            InputStream csvStream = s3Object.getObjectContent();

            if(sourceKey.contains("imagens")){
                context.getLogger().log("Entrei no if que contém imagens");
                // 2. converter CSV → JSON
                GerImagens ger = new GerImagens();
                String json = ger.converterParaJson(csvStream);

                // 3. gerar relatório final
                String relatorioJson = ger.gerarRelatorioJson(json);

                // 4. enviar pro bucket trusted
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/json");

                context.getLogger().log("Enviando para o bucket trusted");

                s3Client.putObject(
                        DESTINATION_BUCKET,
                        sourceKey.replace(".csv", ".json"),
                        new ByteArrayInputStream(relatorioJson.getBytes(StandardCharsets.UTF_8)),
                        metadata
                );
            }

            else if(sourceKey.contains("processos")){
                context.getLogger().log("Entrei no if que contém processos!!");
                // 2. converter CSV → JSON
                Conversor conversor = new Conversor();
                String processosJson = conversor.converterParaJson(csvStream);

                // 4. enviar pro bucket trusted
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/json");

                context.getLogger().log("Enviando para o bucket trusted");

                s3Client.putObject(
                        DESTINATION_BUCKET,
                        sourceKey.replace(".csv", ".json"),
                        new ByteArrayInputStream(processosJson.getBytes(StandardCharsets.UTF_8)),
                        metadata
                );
            }

            else if (sourceKey.contains("principal")) {


                context.getLogger().log("Entrei no if que contém o principal!");

                // Extrair o hostname do nome do arquivo
                // Formato: idServidor_nomeServidor_nomeHospital_principal.csv
                String[] partes = sourceKey.split("_");
                String hostname = partes[1];

                context.getLogger().log("Hostname extraído: " + hostname);

                S3Object s3ObjectAlertas = s3Client.getObject(sourceBucket, sourceKey);
                InputStream csvStreamAlertas = s3ObjectAlertas.getObjectContent();

                // Gerar alertas lendo o CSV direto do bucket
                Alerta alerta = new Alerta();
                alerta.salvaTabelaAlerta(csvStreamAlertas, hostname);

                S3Object s3ObjectJson = s3Client.getObject(sourceBucket, sourceKey);
                InputStream csvStreamJson = s3ObjectJson.getObjectContent();

                LeituraCsv leitorCsv = new LeituraCsv();
                InputStream csvTratado =  leitorCsv.leImportaArquivoCsv(csvStreamJson, sourceKey);

                // Converter CSV to JSON
                Conversor conversor = new Conversor();
                String processosJson = conversor.converterParaJson(csvTratado);

                // Enviar JSON para o bucket trusted
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/json");

                context.getLogger().log("Enviando para o bucket trusted");

                s3Client.putObject(
                        DESTINATION_BUCKET,
                        sourceKey.replace(".csv", ".json"),
                        new ByteArrayInputStream(processosJson.getBytes(StandardCharsets.UTF_8)),
                        metadata
                );

                context.getLogger().log("Processo principal finalizado com sucesso!");

//              NÃO TA FUNCIONANDO, ENTÃO COMENTEI PRA NÃO DAR PROBLEMA
//                context.getLogger().log("Gerando previsões de alertas para: " + hostname);
//
//                try {
//                    PrevisaoAlertas previsaoAlertas = new PrevisaoAlertas();
//                    String previsoesJson = previsaoAlertas.gerarPrevisoes(hostname);
//
//                    // Enviar JSON para o bucket trusted
//                    ObjectMetadata metadataPrevisoes = new ObjectMetadata();
//                    metadataPrevisoes.setContentType("application/json");
//
//                    String previsaoKey = sourceKey.replace("_principal.csv", "_previsoes.json");
//
//                    s3Client.putObject(
//                            DESTINATION_BUCKET,
//                            previsaoKey,
//                            new ByteArrayInputStream(previsoesJson.getBytes(StandardCharsets.UTF_8)),
//                            metadataPrevisoes
//                    );
//
//                    context.getLogger().log("Previsões enviadas com sucesso: " + previsaoKey);
//
//                } catch (Exception ex) {
//                    context.getLogger().log("ERRO ao gerar previsões: " + ex.getMessage());
//                    ex.printStackTrace();
//                }

                context.getLogger().log("Processo principal finalizado com sucesso!");
            }

            return "Processado com sucesso: " + sourceKey;

        } catch (Exception ex) {
            context.getLogger().log("ERRO:" + ex.getMessage());
            return "Falha ao processar o arquivo.";
        }
    }
}
