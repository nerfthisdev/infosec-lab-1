package yasheri.ru.infoseclab1.data;

import jakarta.persistence.*;

@Entity
@Table(name = "data")
public class Data {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 1000, nullable = false)
    private String text;

    protected Data() {
    }

    public Data(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
