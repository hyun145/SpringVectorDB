package kopo.poly.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "documents")
public class Documents {

    @Id
    private String id;

    private String content;

    private List<Double> embedding;


    @Transient
    @JsonIgnore
    private double similarityScore;

    @Builder
    public Documents(String id, String content, List<Double> embedding) {
        this.id = id;
        this.content = content;
        this.embedding = embedding;
    }

}
