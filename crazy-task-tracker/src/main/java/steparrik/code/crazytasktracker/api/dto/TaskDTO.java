package steparrik.code.crazytasktracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDTO {

    @NonNull
    Long id;

    @NonNull
    String name;

    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;

    @NonNull
    String description;

    @NonNull
    int ordinal;

}
