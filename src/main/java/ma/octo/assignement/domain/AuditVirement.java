package ma.octo.assignement.domain;

import lombok.Getter;
import lombok.Setter;
import ma.octo.assignement.domain.util.EventType;

import javax.persistence.*;

@Entity
@Table(name = "AUDIT")
@Getter
@Setter
public class AuditVirement {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 100)
  private String message;

  @Enumerated(EnumType.STRING)
  private EventType eventType;


}
