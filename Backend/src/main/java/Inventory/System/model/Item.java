package Inventory.System.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Jina la item linahitajika")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Category inahitajika")
    @Column(nullable = false)
    private String category;

    @Min(value = 0, message = "Quantity haiwezi kuwa hasi")
    @Column(nullable = false)
    private Integer quantity;
}
