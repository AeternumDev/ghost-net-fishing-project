package ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bergende_person")
public class RecoveryDiver implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String telefonnummer;    public RecoveryDiver() {
    }

    public RecoveryDiver(String name, String email, String telefonnummer) {
        this.name = name;
        this.email = email;
        this.telefonnummer = telefonnummer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecoveryDiver that = (RecoveryDiver) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RecoveryDiver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + (email != null ? email : "N/A") + '\'' +
                ", telefonnummer='" + telefonnummer + '\'' +
                '}';
    }
}
