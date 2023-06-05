package com.mipt.producer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean isWrittenToDB = false;

    @Column
    private boolean isWrittenToBroker = false;

    @Column(unique = true)
    private String login;

    @Column
    private String password;

    public boolean getIsWrittenToDB() {
        return isWrittenToDB;
    }

    public void setIsWrittenToDB(boolean isWrittenToDB) {
        this.isWrittenToDB = isWrittenToDB;
    }

    public boolean getIsWrittenToBroker() {
        return isWrittenToBroker;
    }

    public void setIsWrittenToBroker(boolean isWrittenToBroker) {
        this.isWrittenToBroker = isWrittenToBroker;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
