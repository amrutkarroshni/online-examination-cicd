package com.exam;
import java.util.List;
public final class QuestionBank {
    private QuestionBank() {}
    public static List<Question> all() {
        return List.of(
            new Question(1, "Which file defines a Jenkins pipeline as code?", new String[]{"Jenkinsfile", "pom.xml", "web.xml", "Dockerfile"}, 0),
            new Question(2, "Which Maven phase runs unit tests?", new String[]{"compile", "test", "clean", "validate"}, 1),
            new Question(3, "Which package format is commonly deployed to Tomcat?", new String[]{"JAR", "ZIP", "WAR", "EXE"}, 2),
            new Question(4, "Which HTTP status code usually indicates success?", new String[]{"404", "500", "302", "200"}, 3)
        );
    }
}
