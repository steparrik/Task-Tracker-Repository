package steparrik.code.crazytasktracker.api.dto;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NotFound;

import javax.persistence.Column;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDTO {
    @NonNull
    Long id;

    @NonNull
    String name;

    @NonNull
    int ordinal;

    @NonNull
    Instant createdAt;

    List<TaskDTO> tasks;
}
