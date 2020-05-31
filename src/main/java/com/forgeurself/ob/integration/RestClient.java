package com.forgeurself.ob.integration;

import org.springframework.http.ResponseEntity;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public interface RestClient {

	public ResponseEntity<String> retrieveAccessToken() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException;

	public ResponseEntity<String> submitAccountAccessConsent() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException;

}
