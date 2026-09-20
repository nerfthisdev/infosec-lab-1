package yasheri.ru.infoseclab1.data;

import jakarta.persistence.*;

@Entity
@Table(name = "data")
public class Data {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 50, nullable = false)
    String something;
}
