package edu.stanford.protege.robot.service.storer;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    public MinioConfiguration(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }

    /**
     * Creates and configures a MinIO client bean.
     *
     * @return configured MinioClient instance
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndPoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}
