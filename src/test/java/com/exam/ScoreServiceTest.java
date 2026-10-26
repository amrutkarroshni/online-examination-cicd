package com.exam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScoreServiceTest {
    private final ScoreService service = new ScoreService();

    @Test void allCorrectAnswersGiveFullMarks() {
        assertEquals(4, service.score(QuestionBank.all(), Map.of(1, 0, 2, 1, 3, 2, 4, 3)));
    }
    @Test void allWrongAnswersGiveZero() {
        assertEquals(0, service.score(QuestionBank.all(), Map.of(1, 3, 2, 0, 3, 0, 4, 0)));
    }
    @Test void unansweredQuestionsReceiveNoMarks() {
        assertEquals(1, service.score(QuestionBank.all(), Map.of(1, 0)));
    }
    @Test void questionBankContainsFourQuestions() {
        assertEquals(4, QuestionBank.all().size());
    }
}
