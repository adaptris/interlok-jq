package com.adaptris.core.json.jq;

import java.util.EnumSet;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.ServiceCase;
import com.adaptris.core.common.ConstantDataInputParameter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class JsonJqTransformTest extends ServiceCase {

  private static final String SAMPLE_QUERY = "{\n" + " \"status-id\": .id,\n" + " \"status-code\": .status.agreementStatusCd,\n"
      + " \"status-description\": .status.agreementStatusDesc\n" + "}";

  private static final String SAMPLE_DATA = "{\n" + "  \"id\": 15809259,\n" + "  \"status\": {\n"
      + "    \"agreementStatusCd\": \"4\",\n" + "    \"agreementStatusDesc\": \"Completed\"\n" + "  }\n" + "}";

  private Configuration jsonConfig = new Configuration.ConfigurationBuilder().jsonProvider(new JsonSmartJsonProvider())
      .mappingProvider(new JacksonMappingProvider()).options(EnumSet.noneOf(Option.class)).build();

  public JsonJqTransformTest(String name) {
    super(name);
  }

  public void testService() throws Exception {
    JsonJqTransform service = new JsonJqTransform();
    service.setQuerySource(new ConstantDataInputParameter(SAMPLE_QUERY));

    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance()
        .newMessage(SAMPLE_DATA);
    execute(service, msg);
    assertNotNull(msg.getContent());
    System.err.println(msg.getContent());
    ReadContext ctx = parse(msg);
    assertNotNull(ctx.read("$.status-description"));
    assertNotNull(ctx.read("$.status-code"));
    assertNotNull(ctx.read("$.status-id"));
  }


  @Override
  protected JsonJqTransform retrieveObjectForSampleConfig() {
    JsonJqTransform service = new JsonJqTransform();
    service.setQuerySource(new ConstantDataInputParameter(SAMPLE_QUERY));
    return service;
  }

  protected ReadContext parse(String content) {
    return JsonPath.parse(content, jsonConfig);
  }

  protected ReadContext parse(AdaptrisMessage content) {
    return parse(content.getContent());
  }
}
