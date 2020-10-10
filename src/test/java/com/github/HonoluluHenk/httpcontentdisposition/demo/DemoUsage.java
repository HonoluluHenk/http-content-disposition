package com.github.HonoluluHenk.httpcontentdisposition.demo;

import com.github.HonoluluHenk.httpcontentdisposition.Disposition;
import com.github.HonoluluHenk.httpcontentdisposition.HttpContentDisposition;
import com.github.HonoluluHenk.httpcontentdisposition.isofallback.OverrideIsoFallback;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;

public class DemoUsage {

    void addHeader(HttpServletResponse response) {
        HttpContentDisposition header = HttpContentDisposition.builder()
                //.disposition(Disposition.INLINE)
                .disposition(Disposition.ATTACHMENT)
                .filename("I ❤ special characters")
                .build();

        response.addHeader(header.headerName(), header.headerValue());
    }

    void withIsoFallback() {
        HttpContentDisposition header = HttpContentDisposition.builder()
                //.disposition(Disposition.INLINE)
                .disposition(Disposition.ATTACHMENT)
                .filename("I ❤ special characters")
                .build();
    }

    void withIsoFallbackConvenience() {
        HttpContentDisposition header = HttpContentDisposition.builder()
                .disposition(Disposition.ATTACHMENT)
                .filename("I ❤ special characters")
                .isoFallback(new OverrideIsoFallback("I (heart) special characters"))
                // convenience:
                //.isoFallbackValue("I (heart) special characters")
                .build();
    }

    @Test
    void should_print_addHeader() {
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);

        addHeader(mockResponse);

        Mockito.verify(mockResponse)
                .addHeader(
                        "Content-Disposition",
                        "attachment; filename=\"I ? special characters\"; filename*=UTF-8''I%20%E2%9D%A4%20special%20characters");
    }
}
