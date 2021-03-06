FROM openzipkin/jre-full:1.8.0_152
VOLUME /tmp
ADD jaeger-demo-a/target/jaeger-demo-a-*.jar a.jar
ADD jaeger-demo-b/target/jaeger-demo-b-*.jar b.jar
COPY ./start.sh /start.sh
RUN chmod +x /start.sh
EXPOSE 8080 8081
CMD ["/start.sh"]
