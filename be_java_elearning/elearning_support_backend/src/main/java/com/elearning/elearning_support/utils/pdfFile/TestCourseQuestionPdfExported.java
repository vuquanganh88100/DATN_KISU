package com.elearning.elearning_support.utils.pdfFile;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import com.elearning.elearning_support.dtos.question.QuestionListDTO;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDetailResDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseResDto;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TestCourseQuestionPdfExported {
    public static void exportTestCourseToPdf(ResultTestCourseResDto resultTestCourseResDto, OutputStream outputStream) {
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            PdfFont font = PdfFontFactory.createFont("D:\\download\\multiple_exam_2024_early_release\\be_java_elearning\\elearning_support_backend\\src\\main\\resources\\font\\arial-unicode-ms.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

            document.setFont(font);

            document.add(new Paragraph("Danh sách Câu Hỏi")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
            );
            List<ResultTestCourseDetailResDto>resultDetail=resultTestCourseResDto.getResultTestCourseDetailResDtos();
            for (int i = 0; i < resultDetail.size(); i++) {
                document.add(new Paragraph("Câu " + (i + 1) + ": " +
                        stripHtmlTags(resultDetail.get(i).getQuestionContent()))
                        .setFont(font)
                );

                List<AnswerResDTO> answers = resultDetail.get(i).getAnswerResDTOS();
                for (int j = 0; j < answers.size(); j++) {
                    AnswerResDTO answer = answers.get(j);
                    String prefix = (char) ('A' + j) + ". ";
                    if(answer.getIsCorrect()){
                        document.add(new Paragraph(prefix + stripHtmlTags(answer.getContent()))
                                .setMarginLeft(20)
                                .setFont(font).setBold());
                    }else{
                        document.add(new Paragraph(prefix + stripHtmlTags(answer.getContent()))
                                .setMarginLeft(20)
                                .setFont(font)

                        );
                    }
                }

                document.add(new Paragraph("\n"));
            }
            document.close();

            System.out.println("PDF created successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String stripHtmlTags(String htmlContent) {
        if (htmlContent == null) {
            return "";
        }
        String decodedContent = decodeHtmlEntities(htmlContent);
        return decodedContent.replaceAll("<[^>]*>", "").trim();
    }

    private static String decodeHtmlEntities(String htmlContent) {
        if (htmlContent == null) {
            return "";
        }
        // Giải mã các ký tự HTML entities
        return StringEscapeUtils.unescapeHtml4(htmlContent).trim();
    }
}
