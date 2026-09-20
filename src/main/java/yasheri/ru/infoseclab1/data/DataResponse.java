package yasheri.ru.infoseclab1.data;

import org.springframework.web.util.HtmlUtils;

public record DataResponse(Long id, String text) {

    static DataResponse from(Data data) {
        return new DataResponse(
                data.getId(),
                HtmlUtils.htmlEscape(data.getText())
        );
    }
}
