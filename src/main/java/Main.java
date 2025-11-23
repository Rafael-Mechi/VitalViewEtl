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
    private static final String DESTINATION_BUCKET = "trusted-2011";

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


            return "Processado com sucesso: " + sourceKey;

        } catch (Exception ex) {
            context.getLogger().log("ERRO:" + ex.getMessage());
            return "Falha ao processar o arquivo.";
        }
    }
}
