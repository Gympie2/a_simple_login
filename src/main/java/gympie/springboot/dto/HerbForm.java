package gympie.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HerbForm {

    @NotBlank(message = "Herb name is required.")
    @Size(max = 100, message = "Herb name must be 100 characters or fewer.")
    private String name;

    @NotBlank(message = "Botanical name is required.")
    @Size(max = 150, message = "Botanical name must be 150 characters or fewer.")
    private String botanicalName;

    @NotBlank(message = "Description is required.")
    @Size(max = 1200, message = "Description must be 1200 characters or fewer.")
    private String description;

    @NotNull(message = "Choose a category.")
    private Long categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBotanicalName() {
        return botanicalName;
    }

    public void setBotanicalName(String botanicalName) {
        this.botanicalName = botanicalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
