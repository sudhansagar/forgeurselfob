package com.forgeurself.ob.integration;

import com.forgeurself.ob.client.ApiClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


@Component
public class RestClientImpl implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientImpl.class);

    @Value("${com.forgeurself.ob.tokenUrl}")
    private String TOKEN_URL;

    @Value("${com.forgeurself.ob.clientId}")
    private String CLIENT_ID;

    @Value("${com.forgeurself.ob.clientSecret}")
    private String CLIENT_SECRET;

    @Value("${com.forgeurself.ob.account-access-consents}")
    private String ACCOUNT_ACCESS_CONSENTS;

    @Value("${com.forgeurself.ob.x-fapi-financial-id}")
    private String FINANCIAL_ID;


    @Override
    public ResponseEntity<String> retrieveAccessToken() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", CLIENT_ID);
        map.add("client_secret", CLIENT_SECRET);
        map.add("scope", "accounts");

        final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
                headers);
        ResponseEntity<String> res = restTemplate.postForEntity(TOKEN_URL, entity, String.class);
        return res;
    }

    @Override
    public ResponseEntity<String> submitAccountAccessConsent() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        JsonParser springParser = JsonParserFactory.getJsonParser();
        String stringResponseEntity = retrieveAccessToken().getBody();
        Map<String, Object> map = springParser.parseMap(stringResponseEntity);

        String mapArray[] = new String[map.size()];
        LOGGER.info("Items found: " + mapArray.length);

        String accessToken = map.get("access_token").toString();

        String requestJson = "{\"Data\": {\"Permissions\": [\"ReadAccountsDetail\",\"ReadBalances\",\"ReadTransactionsCredits\",\"ReadTransactionsDebits\",\"ReadTransactionsDetail\"]},\"Risk\": {}}";

        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer "+accessToken);
        headers.add("x-fapi-financial-id", FINANCIAL_ID);

        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<String> res = restTemplate.postForEntity(ACCOUNT_ACCESS_CONSENTS, entity, String.class);
        return res;
    }

    private HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        };

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

}
