package com.RTDMPL.thymeleaf;

import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.HttpHeaders;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.RTDMPL.thymeleaf.repository")
@ComponentScan(basePackages = {"com.RTDMPL.thymeleaf"})
public class EsConfig extends AbstractElasticsearchConfiguration {
    // https://github.com/liliumbosniacum/elasticsearch
    // https://www.youtube.com/watch?v=IiZZAu2Qtp0

    //https://stackoverflow.com/questions/71478330/getting-exceptions-while-running-a-spring-boot-app-with-elasticsearch

    @Value("${elasticsearch.url}")
    public String elasticsearchUrl;

    @Value("${elasticsearch.port}")
    public String elasticsearchPort;


    @Value("${elasticsearch.user-name}")
    public String userName;

    @Value("${elasticsearch.password}")
    public String password;

    /**
     * SpringDataElasticSearch data provides us the flexibility to implement our custom {@link RestHighLevelClient} instance by implementing the abstract method {@link AbstractElasticsearchConfiguration#elasticsearchClient()},
     *
     * @return RestHighLevelClient. AWS ElasticService Https rest calls have to be signed with AWS credentials, hence an interceptor {@link HttpRequestInterceptor} is required to sign every
     * API calls with credentials. The signing is happening through the below snippet
     * <code>
     * signer.sign(signableRequest, awsCredentialsProvider.getCredentials());
     * </code>
     */
    /*@Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        AWS4Signer signer = new AWS4Signer();
        String serviceName = "es";
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);

        return new RestHighLevelClient(RestClient.builder(HttpHost.create(endpoint)).setHttpClientConfigCallback(e -> e.addInterceptorLast(interceptor)));
    }*/
    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {

        //Below 3 lines are added to make elastic client version 7.17.4 compatible with elastic server version 8x
        HttpHeaders compatibilityHeaders = new HttpHeaders();
        compatibilityHeaders.add("Accept", "application/vnd.elasticsearch+json;compatible-with=7");
//        compatibilityHeaders.add("Content-Type", "application/vnd.elasticsearch+json;" + "compatible-with=7");
        compatibilityHeaders.add("text_fields_limit", "2000");


        final ClientConfiguration clientConfiguration =
                ClientConfiguration
                        .builder()
                        .connectedTo(elasticsearchUrl + ":" + elasticsearchPort)
                        .usingSsl()
                        .withConnectTimeout(30000)
                        .withSocketTimeout(30000)
                        .withBasicAuth(userName,password)
                        .withDefaultHeaders(compatibilityHeaders)    // this variant for imperative code
                        .build();
//        final ClientConfiguration clientConfiguration =
//                ClientConfiguration
//                        .builder()
//                        .connectedTo("127.0.0.1:9200")
//                        .withDefaultHeaders(compatibilityHeaders)    // this variant for imperative code
//                        .build();

        return RestClients.create(clientConfiguration).rest();
    }

}
