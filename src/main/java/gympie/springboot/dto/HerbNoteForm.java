package gympie.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HerbNoteForm {

    @NotNull(message = "Choose an herb.")
    private Long herbId;

    @NotBlank(message = "A note title is required.")
    @Size(max = 100, message = "Title must be 100 characters or fewer.")
    private String title;

    @NotBlank(message = "Note text is required.")
    @Size(max = 1200, message = "Note text must be 1200 characters or fewer.")
    private String body;

    public Long getHerbId() {
        return herbId;
    }

    public void setHerbId(Long herbId) {
        this.herbId = herbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
