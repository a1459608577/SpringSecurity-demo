package com.ksn.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.http.converter.jaxb.JaxbOAuth2ExceptionMessageConverter;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author rjk
 * @E-mail ruanjiangke@mxftech.com
 * @Data 19-4-30 下午2:24
 * @Description
 **/

@Slf4j
public class AgileOAuth2ExceptionRenderer implements OAuth2ExceptionRenderer {
    private List<HttpMessageConverter<?>> messageConverters = geDefaultMessageConverters();

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public void handleHttpEntityResponse(HttpEntity<?> responseEntity, ServletWebRequest webRequest) throws Exception {
        if (responseEntity == null) {
            return;
        }
        HttpInputMessage inputMessage = createHttpInputMessage(webRequest);
        HttpOutputMessage outputMessage = createHttpOutputMessage(webRequest);
        if (responseEntity instanceof ResponseEntity && outputMessage instanceof ServerHttpResponse) {
            ((ServerHttpResponse) outputMessage).setStatusCode(((ResponseEntity<?>) responseEntity).getStatusCode());
        }
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            outputMessage.getHeaders().putAll(entityHeaders);
        }
        Object body = responseEntity.getBody();
        if (body != null) {
            writeWithMessageConverters(body, inputMessage, outputMessage);
//            writeWithMessageConverters(WebResponseBuilder.fail(body), inputMessage, outputMessage);
        }
        else {
            // flush headers
            outputMessage.getBody();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void writeWithMessageConverters(Object returnValue, HttpInputMessage inputMessage,
                                            HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
        List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
        if (acceptedMediaTypes.isEmpty()) {
            acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
        }
        MediaType.sortByQualityValue(acceptedMediaTypes);
        Class<?> returnValueType = returnValue.getClass();
        List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
        for (MediaType acceptedMediaType : acceptedMediaTypes) {
            for (HttpMessageConverter messageConverter : messageConverters) {
                if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
                    messageConverter.write(returnValue, acceptedMediaType, outputMessage);
                    if (log.isDebugEnabled()) {
                        MediaType contentType = outputMessage.getHeaders().getContentType();
                        if (contentType == null) {
                            contentType = acceptedMediaType;
                        }
                        log.debug("Written [" + returnValue + "] as \"" + contentType + "\" using ["
                                + messageConverter + "]");
                    }
                    return;
                }
            }
        }
        for (HttpMessageConverter messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
    }

    private List<HttpMessageConverter<?>> geDefaultMessageConverters() {
        List<HttpMessageConverter<?>> result = new ArrayList<>();
        result.addAll(new RestTemplate().getMessageConverters());
        result.add(new JaxbOAuth2ExceptionMessageConverter());
        return result;
    }

    private HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    private HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
        return new ServletServerHttpResponse(servletResponse);
    }
}
