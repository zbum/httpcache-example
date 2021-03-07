package kr.co.manty.example.httpcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {

        return new HttpComponentsClientHttpRequestFactory(
                CachingHttpClients.custom()
                        .setCacheConfig(
                                CacheConfig.custom()
                                        .setMaxObjectSize(1024*1024)
                                        .build()
                        )
                        .setHttpCacheStorage(
                                new EhcacheHttpCacheStorage( getEhcache() )
                        )
                .build()
        );
    }

    private Ehcache getEhcache() {
        try (InputStream inputStream = new ClassPathResource("/ehcache.xml").getInputStream()) {
            return CacheManager.newInstance(inputStream).getEhcache("httpClientCache");
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
