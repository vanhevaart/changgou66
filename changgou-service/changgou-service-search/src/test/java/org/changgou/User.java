package org.changgou;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/16  17:15
 */
@Document(indexName = "user",type = "user")
public class User {

    public User() {
    }

    public User(Long id, String name, int age, String city, String description) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.city = city;
        this.description = description;
    }

    @Id
    private Long id;

    @Field(type = FieldType.Text,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Double)
    private int age;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    private String city;
    @Field(type = FieldType.Text,analyzer = "ik_smart")
    private String description;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
