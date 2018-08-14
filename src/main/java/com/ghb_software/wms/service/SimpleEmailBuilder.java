package com.ghb_software.wms.service;


import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SimpleEmailBuilder {
  private final MultiValueMap<String, Object> formData;


  public SimpleEmailBuilder() {
    formData = new LinkedMultiValueMap<>();
  }

  public SimpleEmailBuilder from(String name, String fromAddress) {
    formData.add("from", name + " <" + fromAddress + ">");
    return this;
  }

  public SimpleEmailBuilder to(String toAddress) {
    formData.add("to", toAddress);
    return this;
  }

  public SimpleEmailBuilder cc(String ccAddress) {
    formData.add("cc", ccAddress);
    return this;
  }

  public SimpleEmailBuilder subject(String subject) {
    formData.add("subject", subject);
    return this;
  }

  public MultiValueMap<String, Object> build() {
    return formData;
  }

  public SimpleEmailBuilder body(String body) {
    formData.add("text", body);
    return this;
  }

  public SimpleEmailBuilder htmlBody(String html) {
    formData.add("html", html);
    return this;
  }
}