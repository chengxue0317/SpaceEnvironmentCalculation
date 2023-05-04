FROM 36.138.8.9:30002/dlxjs/python:v4.0
RUN mkdir -p /dataTest
COPY ./CMS-SDC-SEC /CMS-SDC-SEC
COPY target/sec-1.0.0.jar /
RUN ldconfig

EXPOSE 8998
ENTRYPOINT ["java","-jar","sec-1.0.0.jar"]