package cat.copernic.easytraza_backend.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuració `TomcatHttpConnectorConfig` del projecte EasyTraza.
 */
@Configuration(proxyBeanMethods = false)
public class TomcatHttpConnectorConfig {

    @Value("${server.http.port:8080}")
    private int httpPort;

    /**
     * Executa l'operació `connectorCustomizer`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> connectorCustomizer() {
        return tomcat -> tomcat.addAdditionalConnectors(createHttpConnector());
    }

    /**
     * Executa l'operació `createHttpConnector`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private Connector createHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(httpPort);
        return connector;
    }
}
