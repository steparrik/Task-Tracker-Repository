package steparrik.code.crazytasktracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDTO {

    @NonNull
    Long id;

    @NonNull
    String name;

    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;


    List<TaskStateDTO> taskStates;


}

