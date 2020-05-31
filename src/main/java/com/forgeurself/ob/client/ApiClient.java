package com.forgeurself.ob.client;

import com.forgeurself.ob.entities.User;
import com.forgeurself.ob.integration.RestClient;
import com.forgeurself.ob.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author madhusudhan.gr
 */
@Controller
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

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password) {

        LOGGER.info("Inside login() and the email is: " + email);
        //boolean loginResponse = securityService.login(email, password);
        User userDet = userRepository.login(email, password);
        if (userDet != null) {
            return new ResponseEntity<>("User logged in successfully ", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid user name or password .Please try again.", HttpStatus.UNAUTHORIZED);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/registerUser", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?>  register(@RequestBody User user) {
        LOGGER.info("Creating User : {}" + user);
        User userDet = userRepository.findByEmail(user.getEmail());
        if (userDet != null) {
            LOGGER.error("Unable to create. A User with name already exist", userDet.getFirstName());
            return new ResponseEntity<>("Unable to create. A User with name " +
                    user.getFirstName() + " already exist.", HttpStatus.CONFLICT);
        }else{
            //user.setPassword(encoder.encode(user.getPassword()));
            user.setPassword(user.getPassword());
            userRepository.save(user);
            return new ResponseEntity<>("User registered successfully " +
                    user.getFirstName() , HttpStatus.CREATED);
        }
    }

    // TODO : call only once
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseEntity<String> retrieveAccessToken() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Retrieve Access Token");
        return restClient.retrieveAccessToken();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/accountaccessconsent", method = RequestMethod.POST)
    public ResponseEntity<String> submitAccountAccessConsent() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        LOGGER.info("Submit Account Access Consent");
        return restClient.submitAccountAccessConsent();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/accounts/{AccountId}",
            produces = {"application/json; charset=utf-8"},
            method = RequestMethod.GET)
    public String getAccount(@PathVariable("AccountId") String accountId
            , @RequestParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true) @RequestHeader(value = "Authorization", required = true) String authorization
            , @RequestParam(value = "The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC") @RequestHeader(value = "x-fapi-auth-date", required = false) String xFapiAuthDate
            , @RequestParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.") @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress
            , @RequestParam(value = "An RFC4122 UID used as a correlation id.") @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId
    ) {


        return accountId;
    }
}
