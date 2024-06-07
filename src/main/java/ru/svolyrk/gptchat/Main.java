package ru.svolyrk.gptchat;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;

@Slf4j
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        loggingAppInfo();
        SpringApplication.run(Main.class, args);
    }

    private static void loggingAppInfo() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            log.info("-------------------------------------");
            log.info(model.getId());
            log.info(model.getGroupId());
            log.info(model.getArtifactId());
            log.info(model.getVersion());
            log.info("-------------------------------------");
        } catch (IOException | XmlPullParserException e) {
            log.error("Error reading pom.xml", e);
        }
    }
}