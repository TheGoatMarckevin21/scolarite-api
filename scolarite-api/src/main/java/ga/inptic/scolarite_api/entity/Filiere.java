package ga.inptic.scolarite_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "filiere")
public class Filiere {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , unique = true , length = 20 )
    private String code ;

    @Column(nullable = false, length = 100)
    private String libelle;

    public Filiere(){

    }

    public Filiere(String code,String libelle){
        this.code = code ;
        this.libelle = libelle ;
    }


public long getId() {return id;}    
public void setID(Long id){this.id = id;}


public String getcode() {return code;}
public void setcode(String code){this.code = code;}




}
