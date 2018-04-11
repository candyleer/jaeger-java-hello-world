package io.github.candyleer.jaegerdemob;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

import static io.github.candyleer.jaegerdemob.JaegerDemoBApplication.tracer;
import static io.opentracing.propagation.Format.Builtin.HTTP_HEADERS;

/**
 * @author lican
 * @date 2018/4/11
 */
@Component
public class CustomInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> stringStringMap = Collections.singletonMap("uber-trace-id", request.getHeader("uber-trace-id"));
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(request.getRequestURI())
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);
        if (request.getHeader("uber-trace-id") != null) {
            SpanContext extract = tracer.extract(HTTP_HEADERS, new TextMapExtractAdapter(stringStringMap));
            spanBuilder = spanBuilder.asChildOf(extract);
        }
        spanBuilder.startActive(true);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        tracer.scopeManager().active().close();
    }
}
