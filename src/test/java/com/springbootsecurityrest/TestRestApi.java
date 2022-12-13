package com.springbootsecurityrest;

import com.springbootsecurityrest.model.User;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRestApi {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private String getUrl(String path){
		return "http://localhost:" + port + path;
	}

	private String getBaseAuthAnton(String auth){
		return new String(Base64.getEncoder().encode( auth.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	public void testLogin(){
		String url = getUrl("/auth/login");
		String auth = getBaseAuthAnton("anton:1234");
		RequestEntity<Void> request = RequestEntity.post(URI.create(url))
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
				.build();
		ResponseEntity<User> response = restTemplate.exchange(request, User.class);
		Assertions.assertEquals(200, response.getStatusCode().value());
	}

	@Test
	public void testIncorrectLogin(){
		String url = getUrl("/auth/login");
		String auth = getBaseAuthAnton("anton:1234556");
		RequestEntity<Void> request = RequestEntity.post(URI.create(url))
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
				.build();
		ResponseEntity<User> response = restTemplate.exchange(request, User.class);
		Assertions.assertEquals(401, response.getStatusCode().value());
	}

	@Test
	public void testUnauthorizedUserRequest(){
		String url = getUrl("/users");
		RequestEntity<Void> voidRequest = RequestEntity.get(URI.create(url)).build();
		ResponseEntity<?> users = restTemplate.exchange(voidRequest, Object.class);
		Assertions.assertEquals(401, users.getStatusCode().value());
	}

	@Test
	public void testUsers(){
		String url = getUrl("/auth/login");
		String auth = getBaseAuthAnton("anton:1234");
		RequestEntity<Void> voidRequest = RequestEntity.post(URI.create(url))
				.header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
				.build();
		ResponseEntity<User> userResponse = restTemplate.exchange(voidRequest, User.class);
		url = getUrl("/users");
		String token = Objects.requireNonNull(userResponse.getHeaders().get("x-csrf-token")).get(0);
		voidRequest = RequestEntity.get(URI.create(url)).header("x-csrf-token", token).build();
		ResponseEntity<User[]> users = restTemplate.exchange(voidRequest, User[].class);
		Assertions.assertEquals(200, users.getStatusCode().value());
	}
}
