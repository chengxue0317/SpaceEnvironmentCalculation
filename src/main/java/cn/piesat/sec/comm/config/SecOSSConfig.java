package cn.piesat.sec.comm.config;

import cn.piesat.sec.comm.properties.SecMinioProperties;
import cn.piesat.sec.comm.properties.SecS3Properties;
import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class SecOSSConfig {

    @Getter
    @Value("${oss.type}")
    private String ossType;

    @Autowired
    private SecMinioProperties secMinioProperties;

    @Autowired
    private SecS3Properties secS3Properties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(secMinioProperties.getEndpoint())
                .credentials(secMinioProperties.getAccesskey(), secMinioProperties.getSecretkey())
                .build();
        return minioClient;
    }

    @Bean
    public S3Client s3Client() {
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(secS3Properties.getAccesskey(), secS3Properties.getSecretkey())))
                .endpointOverride(URI.create(secS3Properties.getEndpoint()))
                .serviceConfiguration(item -> item.pathStyleAccessEnabled(true).checksumValidationEnabled(false))
                .region(Region.US_EAST_1)
                .build();
        return s3Client;
    }
}
