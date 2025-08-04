package com.circlerate.circle_rate.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CommonUtility {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeResponse(HttpServletResponse response, Object message, int httpStatus) throws IOException {
        String messageJson = objectMapper.writeValueAsString(message);
        response.setStatus(httpStatus);
        response.setContentType("application/json");
        response.getWriter().write(messageJson);
        response.getWriter().flush();
    }
}
