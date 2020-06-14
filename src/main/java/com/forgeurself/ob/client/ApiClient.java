package com.forgeurself.ob.client;

import com.forgeurself.ob.entities.HomeLoan;
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
import org.springframework.ui.ModelMap;
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

  //  @Autowired
   // private SecurityService securityService;

    //@Autowired
    //private BCryptPasswordEncoder encoder;

    @RequestMapping(value = "/login", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody String input) {
        LOGGER.info("Inside login() and the email is: " + input);
        return restClient.validateUser(input);
    }

    @RequestMapping(value = "/registerUser", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User user) {
        LOGGER.info("Creating User : {}" + user);
        return restClient.registerUser(user);
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

    @RequestMapping(value = "/submitDetails", produces = {"application/json; charset=utf-8"}, method = RequestMethod.POST)
    public ResponseEntity<String>  completeReservation(@RequestBody HomeLoan input) {
        LOGGER.info("Fetching Loan Application details " + input);
        return restClient.loanApplnDetails(input);
    }

}
