package com.exam;
import java.util.List;
import java.util.Map;
public class ScoreService {
    public int score(List<Question> questions, Map<Integer, Integer> answers) {
        int total = 0;
        for (Question question : questions) {
            Integer selected = answers.get(question.id());
            if (selected != null && selected == question.correct()) total++;
        }
        return total;
    }
}
