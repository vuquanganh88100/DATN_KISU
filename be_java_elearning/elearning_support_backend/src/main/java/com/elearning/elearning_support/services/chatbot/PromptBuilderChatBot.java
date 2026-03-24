package com.elearning.elearning_support.services.chatbot;

import com.elearning.elearning_support.dtos.chatbot.ModeChatBot;
import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilderChatBot {
    public String build(
            ModeChatBot mode,
            ProblemContest problem,
            SubmissionContest submissionContest,
            String userMessage
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append(role(mode));
        sb.append(task(userMessage));
        sb.append(context(problem, submissionContest));
        sb.append(constraints(mode));
        sb.append(outputFormat(mode));
        System.out.println(sb);
        return sb.toString();
    }


    private String context(ProblemContest p, SubmissionContest s) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n[ĐỀ BÀI]\n")
                .append(p.getDescription()).append("\n");

        if (s != null) {
            sb.append("\n[CODE SINH VIÊN]\n")
                    .append(s.getSourceCode()).append("\n");

            sb.append("\n[KẾT QUẢ]\n")
                    .append(s.getVerdict()).append("\n");
        }

        return sb.toString();
    }



    private String role(ModeChatBot mode) {
        switch (mode) {
            case HINT:
                return "Bạn là trợ lý học lập trình cho sinh viên.\n";
            case DEBUG:
                return "Bạn là trợ lý debug code cho sinh viên.\n";
            case EXPLAIN:
                return "Bạn là trợ lý giảng dạy lập trình.\n";
            default:
                return "";
        }
    }
    private String task(String userMessage) {
        String result = userMessage.replaceFirst("^/\\w+\\s*", "");
        return result;

    }
    private String constraints(ModeChatBot mode) {
        switch (mode) {
            case HINT:
                return "\n[RÀNG BUỘC]\n- Không cung cấp code\n- Không giải thuật chi tiết\n";
            case DEBUG:
                return "\n[RÀNG BUỘC]\n- Không sửa code\n- Không viết lại lời giải\n";
            case EXPLAIN:
                return "\n[RÀNG BUỘC]\n- Không cung cấp lời giải thay thế hoàn chỉnh\n";
            default:
                return "";
        }
    }
    private String outputFormat(ModeChatBot mode) {
        switch (mode) {
            case HINT:
                return "\n[ĐỊNH DẠNG TRẢ LỜI]\n- Gạch đầu dòng\n- Dễ hiểu\n";
            case DEBUG:
                return "\n[ĐỊNH DẠNG TRẢ LỜI]\n- Chỉ ra lỗi → Gợi ý sửa\n";
            case EXPLAIN:
                return "\n[ĐỊNH DẠNG TRẢ LỜI]\n- Thuật toán → Big-O → Tối ưu\n";
            default:
                return "";
        }
    }


}
