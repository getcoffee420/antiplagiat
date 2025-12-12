package getcoffee.antiplagiat.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record GatewayConfig(String storageUrl, String analysisUrl) {}
