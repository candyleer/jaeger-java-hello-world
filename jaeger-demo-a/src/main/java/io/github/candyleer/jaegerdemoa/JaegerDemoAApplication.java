package io.github.candyleer.jaegerdemoa;

import com.uber.jaeger.Tracer;
import com.uber.jaeger.reporters.RemoteReporter;
import com.uber.jaeger.samplers.ConstSampler;
import io.opentracing.SpanContext;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.tag.Tags;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.opentracing.propagation.Format.Builtin.HTTP_HEADERS;

/**
 * @author lican
 */
@RestController
@RequestMapping("a")
@SpringBootApplication
public class JaegerDemoAApplication {

    public static Tracer tracer;

    public static Tracer mockMysqlTracer;

    public static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        tracer = new Tracer.Builder("jaeger-demo-a")
                .withReporter(new RemoteReporter.Builder().build())
                .withSampler(new ConstSampler(true))
                .build();
        mockMysqlTracer = new Tracer.Builder("jaeger-demo-mysql")
                .withReporter(new RemoteReporter.Builder().build())
                .withSampler(new ConstSampler(true))
                .build();
        SpringApplication.run(JaegerDemoAApplication.class, args);
    }

    @GetMapping("hello")
    public Object hello() throws IOException, InterruptedException {
        HashMap<String, String> map = new HashMap<>();
        Request.Builder builder = new Request
                .Builder()
                .url("http://localhost:8081/b/hello");
        tracer.buildSpan("okhttp")
                .startActive(true);
        SpanContext context = tracer.activeSpan().context();
        tracer.inject(context, HTTP_HEADERS, new TextMapInjectAdapter(map));
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {

        } finally {
            tracer.scopeManager().active().close();
        }
        mockMysqlTracer.buildSpan("mock-mysql")
                .asChildOf(tracer.activeSpan())
                .withTag(Tags.DB_TYPE.getKey(), "mysql")
                .startActive(true);
        //mock mysql invoke
        Thread.sleep(1000);
        mockMysqlTracer.scopeManager().active().close();
        return Collections.singletonMap("hello", "a");
    }


}