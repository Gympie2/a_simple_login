package gympie.springboot.a_simple_login.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "herbs")
public class Herb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String botanicalName;

    @Column(nullable = false, length = 1200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private HerbCategory category;

    @OneToMany(mappedBy = "herb", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HerbNote> notes = new ArrayList<>();

    protected Herb() {
    }

    public Herb(String name, String botanicalName, String description, HerbCategory category) {
        this.name = name;
        this.botanicalName = botanicalName;
        this.description = description;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBotanicalName() {
        return botanicalName;
    }

    public String getDescription() {
        return description;
    }

    public HerbCategory getCategory() {
        return category;
    }

    public void update(String name, String botanicalName, String description, HerbCategory category) {
        this.name = name;
        this.botanicalName = botanicalName;
        this.description = description;
        this.category = category;
    }
}
