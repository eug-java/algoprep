package com.algoprep.course.api;

import com.algoprep.course.i18n.CourseLocale;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class LocaleResolverSupport {

    public CourseLocale resolve(HttpServletRequest request, String langParam) {
        if (langParam != null && !langParam.isBlank()) {
            return CourseLocale.from(langParam);
        }
        String header = request.getHeader("Accept-Language");
        if (header != null && !header.isBlank()) {
            // take first language tag
            String first = header.split(",")[0].trim();
            return CourseLocale.from(first);
        }
        return CourseLocale.EN;
    }
}
