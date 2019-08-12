package com.auth.api.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
	private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);
	
	private static final String OAUTH_SECURITY_SCHEME_NAME = "oauth2";
	private static final AuthorizationScope ADMIN_AUTH_SCOPE = new AuthorizationScope("role_admin",/*CLIENT_USER_APP*/
			"Admin Authorization Code");

	
	private Docket createNewDocket(String groupName, Predicate<String> paths) {
		Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName(groupName).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.any()).paths(paths).build()
				.securitySchemes(Collections.singletonList(oAuthScheme()))
				.securityContexts(Collections.singletonList(securityContext(paths)))
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class);

		setGeneralResponseMessages(docket);

		return docket;
	}

	private ApiInfo apiInfo() {

		Contact contact = new Contact("Demo", "http://www.demo.com", "support@demo.com");

		return new ApiInfoBuilder().title("REST API").description(
				"REST API for integrating with  Middle Tier. All API endpoint will return a response object which will contain 3 parameters "
						+ "'data': it will have actual response data (API Endpoint specific response model shown in API documentation will be available in 'data' parameter of original repsonse object), "
						+ "'meta': it will have some metadata information, 'error': error information if api response have any error.")
				.termsOfServiceUrl("http://www.demo.com").contact(contact).license("Demo Proprietary API")
				.licenseUrl("http://www.demo.com").version("1.0").build();
	}
	
	private OAuth oAuthScheme() {
		ResourceOwnerPasswordCredentialsGrant grant = new ResourceOwnerPasswordCredentialsGrant("./oauth/token");

		OAuth oAuth = new OAuthBuilder().name(OAUTH_SECURITY_SCHEME_NAME)
				.scopes(Collections.singletonList(ADMIN_AUTH_SCOPE))
				.grantTypes(Collections.singletonList(grant)).build();

		return oAuth;
	}

	private SecurityContext securityContext(Predicate<String> paths) {
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[] { ADMIN_AUTH_SCOPE };
		return SecurityContext.builder()
				.securityReferences(Collections
						.singletonList(new SecurityReference(OAUTH_SECURITY_SCHEME_NAME, authorizationScopes)))
				.forPaths(paths).build();
	}
	
	
	private void setGeneralResponseMessages(Docket docket) {
		docket.globalResponseMessage(RequestMethod.GET, getReqsResMessages())
				.globalResponseMessage(RequestMethod.POST, postOrPutReqsResMessages())
				.globalResponseMessage(RequestMethod.PUT, postOrPutReqsResMessages())
				.globalResponseMessage(RequestMethod.DELETE, deleteReqsResMessages());
	}
	
	private List<ResponseMessage> getReqsResMessages() {
		List<ResponseMessage> responseMessagesList = new ArrayList<ResponseMessage>();
		responseMessagesList.add(new ResponseMessageBuilder().code(200).message("Successfully get the data").build());
		responseMessagesList.addAll(commonResponseMessages());
		return responseMessagesList;
	}

	private List<ResponseMessage> postOrPutReqsResMessages() {
		List<ResponseMessage> responseMessagesList = new ArrayList<ResponseMessage>();
		responseMessagesList
				.add(new ResponseMessageBuilder().code(200).message("Successfully created/updated").build());
		responseMessagesList.addAll(commonResponseMessages());
		return responseMessagesList;
	}

	private List<ResponseMessage> deleteReqsResMessages() {
		List<ResponseMessage> responseMessagesList = new ArrayList<ResponseMessage>();
		responseMessagesList.add(new ResponseMessageBuilder().code(204).message("No content").build());
		responseMessagesList.addAll(commonResponseMessages());
		return responseMessagesList;
	}
	
	private List<ResponseMessage> commonResponseMessages() {
		List<ResponseMessage> crm = new ArrayList<ResponseMessage>();
		crm.add(new ResponseMessageBuilder().code(400).message("Client cause of error").build());
		crm.add(new ResponseMessageBuilder().code(401).message("Unauthorized").build());
		crm.add(new ResponseMessageBuilder().code(403).message("Forbidden").build());
		crm.add(new ResponseMessageBuilder().code(404).message("Not Found").build());
		crm.add(new ResponseMessageBuilder().code(500).message("Internal server error").build());
		return crm;
	}
	
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
	
	@Bean
	public Docket userApi() {
		Docket docket = createNewDocket("User", userPaths());
		addUserTags(docket);
		return docket;
	}
	
	@Bean
	public Docket loginApi() {
		Docket docket = createNewDocket("Login", loginPaths());
		addLoginTags(docket);
		return docket;
	}
	
	private void addLoginTags(Docket docket) {
		Tag loginTag = new Tag("Login", "Login API");
		docket.tags(loginTag, loginTag);
	}
	
	private void addUserTags(Docket docket) {
		Tag loginTag = new Tag("User", "User API");
		docket.tags(loginTag, loginTag);
	}
	
	private Predicate<String> loginPaths() {
		return or(regex("/oauth/token.*"), regex("./logout"));
	}
	
	@SuppressWarnings("unchecked")
	private Predicate<String> userPaths() {
		return or(regex("/user.*"));
	}

	@PostConstruct
	public void initComplete() {
		logger.info("Swagger initialization complete!");
	}
}