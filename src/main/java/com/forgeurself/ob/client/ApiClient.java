package com.forgeurself.ob.client;

import com.forgeurself.ob.entities.User;
import com.forgeurself.ob.integration.RestClient;
import com.forgeurself.ob.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author madhusudhan.gr
 */
@Controller
@CrossOrigin(origins = {"http://localhost:4200", "http://forgeurself.eastasia.cloudapp.azure.com:4200"})
public class ApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiClient.class);

    @Autowired
    RestClient restClient;

    @Autowired
    private UserRepository userRepository;

  //  @Autowired
   // private SecurityService securityService;

    //@Autowired
    //private BCryptPasswordEncoder encoder;

    @RequestMapping(value = "/login", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody String input) {
        LOGGER.info("Inside login() and the email is: " + input);

        JsonParser springParser = JsonParserFactory.getJsonParser();
        String stringResponseEntity = input;
        Map<String, Object> map = springParser.parseMap(stringResponseEntity);

        //boolean loginResponse = securityService.login(email, password);
        User userDet = userRepository.login(map.get("email").toString(), map.get("password").toString());
        if (userDet != null) {
            return new ResponseEntity<>("{\"response\"" +":"+"\"User logged in successfully \"}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"response\"" +":"+"\"Invalid user name or password.Please try again. \"}", HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/registerUser", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User user) {
        LOGGER.info("Creating User : {}" + user);
        User userDet = userRepository.findByEmail(user.getEmail());

        if (userDet != null) {
            LOGGER.error("Unable to create. A User with name already exist", userDet.getFirstName());
            return new ResponseEntity<>("{\"response\"" +":"+"\"Unable to create. A User with name " +
                    user.getFirstName() + "already exist.\"}", HttpStatus.CONFLICT);
        }else{
            //user.setPassword(encoder.encode(user.getPassword()));
            user.setPassword(user.getPassword());
            userRepository.save(user);
            return new ResponseEntity<>("{\"response\"" +":"+"\"User registered successfully " +
                    user.getFirstName() + "\"}", HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/token", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<String> retrieveAccessToken() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Retrieve Access Token");
        return restClient.retrieveAccessToken();
    }

    @RequestMapping(value = "/exchangetoken", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<String> exchangeCodeAccessToken(@RequestBody String input) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Retrieve Access Token");
        return restClient.exchangeCodeAccessToken(input);
    }

    @RequestMapping(value = "/accountaccessconsent", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<String> submitAccountAccessConsent() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Submit Account Access Consent");
        return restClient.submitAccountAccessConsent();
    }

    @RequestMapping(value = "/accounts", produces = {"application/json; charset=utf-8"}, method = RequestMethod.GET)
    public ResponseEntity<String> getAccountDetails(@RequestHeader(value = "Authorization") String authCode) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Get account details");
        return restClient.getAccountDetails(authCode);
    }

    @RequestMapping(value = "/accounts/transactions", produces = {"application/json; charset=utf-8"}, method = RequestMethod.POST)
    public ResponseEntity<String> getAccountTransactions(@RequestBody String input, @RequestHeader(value = "Authorization") String authCode) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Get Account Transaction Details");
        return restClient.getAccountTransactionDetails(input, authCode);
    }

    @RequestMapping(value = "/accounts/balances", produces = {"application/json; charset=utf-8"}, method = RequestMethod.POST)
    public ResponseEntity<String> getAccountBalances(@RequestBody String input, @RequestHeader(value = "Authorization") String authCode) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Get Account Transaction Details");
        return restClient.getAcntBalances(input, authCode);
    }
}
