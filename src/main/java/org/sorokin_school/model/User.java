package org.sorokin_school.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String login;
    private List<Account> accountList = new ArrayList<>();

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", accountList=" + accountList +
                '}';
    }
}
