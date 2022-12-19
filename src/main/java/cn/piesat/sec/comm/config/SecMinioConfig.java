package cn.piesat.sec.comm.config;

import cn.piesat.sec.comm.properties.SecMinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecMinioConfig {
    @Autowired
    private SecMinioProperties secMinioProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(secMinioProperties.getEndpoint())
                .credentials(secMinioProperties.getAccesskey(), secMinioProperties.getSecretkey())
                .build();
        return minioClient;
    }
}
