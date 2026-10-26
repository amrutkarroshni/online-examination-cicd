package com.exam;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/exam")
public class ExamServlet extends HttpServlet {
    private final ScoreService scorer = new ScoreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html lang='en'><head><meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Online Examination</title><style>");
        out.println("body{font-family:Arial;background:#f3f6fb;color:#172033;margin:0;padding:32px}");
        out.println("main{max-width:760px;margin:auto;background:white;padding:28px;border-radius:16px;box-shadow:0 8px 28px #17203318}");
        out.println("h1{color:#244a86}.question{padding:16px 0;border-bottom:1px solid #e5eaf2}");
        out.println("label{display:block;padding:7px 0;cursor:pointer}button{background:#244a86;color:white;border:0;border-radius:8px;padding:12px 20px;font-size:16px;cursor:pointer}");
        out.println("</style></head><body><main><h1>Online Examination System</h1>");
        out.println("<p>Answer the four multiple-choice questions and submit your exam.</p><form method='post' action='exam'>");
        for (Question question : QuestionBank.all()) {
            out.printf("<section class='question'><strong>Q%d. %s</strong><br>%n", question.id(), escapeHtml(question.text()));
            for (int i = 0; i < question.options().length; i++) {
                out.printf("<label><input type='radio' name='q%d' value='%d'> %s</label>%n",
                    question.id(), i, escapeHtml(question.options()[i]));
            }
            out.println("</section>");
        }
        out.println("<p><button type='submit'>Submit Examination</button></p></form></main></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Question> questions = QuestionBank.all();
        Map<Integer, Integer> answers = new HashMap<>();
        for (Question question : questions) {
            String value = request.getParameter("q" + question.id());
            if (value != null) {
                try {
                    int selected = Integer.parseInt(value);
                    if (selected >= 0 && selected < question.options().length) answers.put(question.id(), selected);
                } catch (NumberFormatException ignored) { }
            }
        }
        int score = scorer.score(questions, answers);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().printf("<!doctype html><html><head><meta charset='UTF-8'><title>Exam Result</title></head><body style='font-family:Arial;background:#f3f6fb;padding:40px'>"
            + "<main style='max-width:600px;margin:auto;background:white;padding:28px;border-radius:16px'>"
            + "<h1>Examination Result</h1><p>Your score: <strong>%d / %d</strong></p>"
            + "<p>Answered questions: %d / %d</p><a href='exam'>Retake exam</a></main></body></html>",
            score, questions.size(), answers.size(), questions.size());
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
