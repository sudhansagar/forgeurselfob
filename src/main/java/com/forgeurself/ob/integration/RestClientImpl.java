package com.forgeurself.ob.integration;

import com.forgeurself.ob.entities.HomeLoan;
import com.forgeurself.ob.entities.User;
import com.forgeurself.ob.repos.LoanRepository;
import com.forgeurself.ob.repos.UserRepository;
import com.forgeurself.ob.utils.AzureBlobStorageUtils;
import com.forgeurself.ob.utils.PDFGenerator;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.*;
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
import java.util.Random;


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

    @Value("${com.forgeurself.ob.redirectUrl}")
    private String REDIRECT_URL;

    @Value("${com.forgeurself.ob.accounts}")
    private String FETCH_ACCOUNTS;

    @Value("${com.forgeurself.ob.loanAppDirpath}")
    private String LOAN_APP_DIR;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    PDFGenerator pdfGenerator;

    @Autowired
    AzureBlobStorageUtils azureUtils;

    @Override
    public ResponseEntity<String> validateUser(String input) {
        JsonParser springParser = JsonParserFactory.getJsonParser();
        String stringResponseEntity = input;
        Map<String, Object> map = springParser.parseMap(stringResponseEntity);

        //boolean loginResponse = securityService.login(email, password);
        User userDet = userRepo.login(map.get("email").toString(), map.get("password").toString());
        if (userDet != null) {
            return new ResponseEntity<>("{\"response\"" +":"+"\"User logged in successfully \"}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"response\"" +":"+"\"Invalid user name or password.Please try again. \"}", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<String> registerUser(User user) {
        User userDet = userRepo.findByEmail(user.getEmail());

        if (userDet != null) {
            LOGGER.error("Unable to create. A User with name already exist", userDet.getFirstName());
            return new ResponseEntity<>("{\"response\"" +":"+"\"Unable to create. A User with name " +
                    user.getFirstName() + "already exist.\"}", HttpStatus.CONFLICT);
        }else{
            //user.setPassword(encoder.encode(user.getPassword()));
            user.setPassword(user.getPassword());
            userRepo.save(user);
            return new ResponseEntity<>("{\"response\"" +":"+"\"User registered successfully " +
                    user.getFirstName() + "\"}", HttpStatus.CREATED);
        }
    }

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
    public ResponseEntity<String> exchangeCodeAccessToken(String input) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        JsonParser springParser = JsonParserFactory.getJsonParser();
        String stringResponseEntity = input;
        Map<String, Object> reqMap = springParser.parseMap(stringResponseEntity);

        LOGGER.debug("Code for exchangeCodeAccessToken : " + reqMap.get("code").toString());

        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", CLIENT_ID);
        map.add("client_secret", CLIENT_SECRET);
        map.add("redirect_uri", REDIRECT_URL);
        map.add("grant_type", "authorization_code");
        map.add("code", reqMap.get("code").toString());

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

    @Override
    public ResponseEntity<String> getAccountDetails(String authCode) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();
        LOGGER.debug("authCode from client " + authCode);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+authCode);
        headers.add("x-fapi-financial-id", FINANCIAL_ID);

        //request entity is created with request headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                FETCH_ACCOUNTS,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        return responseEntity;
    }

    @Override
    public ResponseEntity<String> getAccountTransactionDetails(String input, String authCode) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+authCode);

        JsonParser springParser = JsonParserFactory.getJsonParser();
        String stringResponseEntity = input;
        Map<String, Object> map = springParser.parseMap(stringResponseEntity);
        LOGGER.debug("Account Id : " + map.get("AccountId").toString());

        //request entity is created with request headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String finalUrl = FETCH_ACCOUNTS + "/" + map.get("AccountId").toString() + "/transactions";
        //LOGGER.debug("*** finalUrl : " + finalUrl);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );
        return responseEntity;
    }

    @Override
    public ResponseEntity<String> getAcntBalances(String input, String authCode) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+authCode);

        JsonParser springParser = JsonParserFactory.getJsonParser();
        String stringResponseEntity = input;
        Map<String, Object> map = springParser.parseMap(stringResponseEntity);
        LOGGER.debug("Account Id : " + map.get("AccountId").toString());

        //request entity is created with request headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String finalUrl = FETCH_ACCOUNTS + "/" + map.get("AccountId").toString() + "/balances";

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        return responseEntity;
    }

    @Override
    public ResponseEntity<String> loanApplnDetails(HomeLoan userLoanDet) {

        String resMsg = null;
        Random rand = new Random();
        int rand_int = rand.nextInt(100000);
        String fileName =  "LoanApp_"+userLoanDet.getFullName()+"_"+rand_int + ".pdf";
        String filePath = LOAN_APP_DIR + fileName;

        LOGGER.debug("Persisting the Loan Application");
        userLoanDet.setApplnName(fileName);
        userLoanDet.setMailStatus("P");
        loanRepo.save(userLoanDet);

        LOGGER.debug("Generating the Loan Application");
        boolean genStatus = pdfGenerator.generateAppln(userLoanDet, filePath);

        boolean uploadStatus = false;

        if(genStatus){
            resMsg = "generated successfully ";
            uploadStatus = azureUtils.blobFileStorage(LOAN_APP_DIR, fileName);
        }else{
            resMsg = "generated failed ";
        }
        if(uploadStatus){
            resMsg = resMsg.concat(" and uploaded successfully");
        }else
            resMsg = resMsg.concat(" and uploaded failed");

        return new ResponseEntity<>("{\"response\"" +":"+"\"Loan Application "  + resMsg
                + "\"}", HttpStatus.OK);
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
