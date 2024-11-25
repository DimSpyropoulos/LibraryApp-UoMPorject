package gr.uom.it1566.Library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class UserEntity {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
