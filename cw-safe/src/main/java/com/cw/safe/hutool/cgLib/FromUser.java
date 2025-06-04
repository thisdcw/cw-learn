package com.cw.safe.hutool.cgLib;

/**
 * @author thisdcw
 * @date 2025年05月31日 9:58
 */
public class FromUser {

    private String name;

    private int age;

    public FromUser(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public FromUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
