package com.ghb_software.wms.service;

import com.ghb_software.wms.model.VerificationToken;
import com.ghb_software.wms.util.EmailConstants;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.concurrent.Future;


@Service
public class EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  @Value("${mailgun.api.url}")
  private String mailgunApiBaseUrl;

  @Value("${mailgun.admin.mail}")
  private String adminMail;

  @Value("${mailgun.api.credentials}")
  private String plainCreds;

  @Value("${mail.service.sendEmail}")
  private boolean sendEmail;

  @Value("${application.address}")
  private String applicationUrl;

  private static final HttpHeaders headers = new HttpHeaders();

  private RestTemplate template = new RestTemplate();


  @PostConstruct
  public void init() {
    byte[] plainCredsBytes = plainCreds.getBytes();
    String base64Creds = Base64Utils.encodeToString(plainCredsBytes).trim();

    headers.put(HttpHeaders.AUTHORIZATION, Arrays.asList("Basic " + base64Creds));
    headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED.toString()));

  }

  @Async
  public Future<Boolean> sendNewCustomerNotification(VerificationToken verificationToken, String oneTimePassword) {
    return sendEmail(EmailConstants.APPLICATION_NAME, EmailConstants.APPLICATION_EMAIL,
            verificationToken.getUser().getEmail(), "WMS Registration - Verify account", createNewUserNotificationMailBody(verificationToken, oneTimePassword));
  }

  @Async
  public Future<Boolean> sendEmailFromApp(String to, String subject, String body) {
    return sendEmail(EmailConstants.APPLICATION_NAME, EmailConstants.APPLICATION_EMAIL, to, subject, body);
  }

  @Async
  public Future<Boolean> sendEmail(String fromName, String fromAddr, String to, String subject, String body) {
    if (sendEmail) {
      SimpleEmailBuilder emailBuilder = new SimpleEmailBuilder();
      MultiValueMap<String, Object> email = emailBuilder
              .from(fromName, fromAddr)
              .to(to).subject(subject)
              .htmlBody(body)
              .build();
      template.exchange(mailgunApiBaseUrl + "/messages", HttpMethod.POST,
              new HttpEntity<>(email, headers), String.class);

    } else {
      logger.info("Send e-mail from {} <{}> to {}", fromName, fromAddr, to);
    }
    return new AsyncResult<>(false);
  }


  private String createNewUserNotificationMailBody(VerificationToken token, String oneTimePassword) {
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    ve.setProperty("classpath.resource.loader.class",
            ClasspathResourceLoader.class.getName());
    ve.init();
    VelocityContext context = new VelocityContext();
    // add params to Velocity context

    context.put(EmailConstants.TOKEN, token);
    context.put(EmailConstants.APP_URL, applicationUrl);
    context.put(EmailConstants.PASSWORD, oneTimePassword);
    // generate template
    Template template = ve.getTemplate("newUser.vm");
    StringWriter writer = new StringWriter();

    template.merge(context, writer);
    return writer.toString();
  }


}
