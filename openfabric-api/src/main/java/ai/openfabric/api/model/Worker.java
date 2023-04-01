package ai.openfabric.api.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Worker extends Datable implements Serializable {

    @Id
    @Getter
    @Setter
    public String id;
    public String name;
    public String state;
    public String status;
    public String containerId;
    public String image;
    public String imageId;

}
