package de.beosign.jpatest.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String            login;
    private String            firstName;
    private String            lastName;

    protected User()
    {
    }

    public User(String login, String firstName, String lastName)
    {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NotNull
    @Id
    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    @NotNull
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    @NotNull
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

}
