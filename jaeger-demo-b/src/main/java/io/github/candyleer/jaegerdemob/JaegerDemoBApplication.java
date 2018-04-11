package io.github.candyleer.jaegerdemob;

import com.uber.jaeger.Tracer;
import com.uber.jaeger.reporters.RemoteReporter;
import com.uber.jaeger.samplers.ConstSampler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("b")
@SpringBootApplication
public class JaegerDemoBApplication {

    public static Tracer tracer;

    public static Tracer jedisTracer;

    public static void main(String[] args) {
        tracer = new Tracer.Builder("jaeger-demo-b")
                .withReporter(new RemoteReporter.Builder().build())
                .withSampler(new ConstSampler(true))
                .build();
        jedisTracer = new Tracer.Builder("jaeger-demo-redis")
                .withReporter(new RemoteReporter.Builder().build())
                .withSampler(new ConstSampler(true))
                .build();
        SpringApplication.run(JaegerDemoBApplication.class, args);
    }

    @GetMapping("hello")
    public Object hello() throws IOException {
        try {
            jedisTracer.buildSpan("redis")
                    .asChildOf(tracer.activeSpan())
                    .startActive(true);
            Jedis jedis = new Jedis();
            jedis.set("test_redis", "value");
            jedis.close();
        } finally {
            jedisTracer.scopeManager().active().close();
        }
        return Collections.singletonMap("hello", "b");
    }
}
