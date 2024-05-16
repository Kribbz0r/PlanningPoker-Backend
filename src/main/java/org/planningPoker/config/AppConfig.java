package org.planningPoker.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface AppConfig {

    String privateKey();

    String jwtIssuer();

    String publicKey();
    
}
