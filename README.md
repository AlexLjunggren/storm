## Storm - Object-Relational Mapper ##

Context

- src/main/resources/context.json

```json
[
    {
        "name" : "H2",
        "driver" : "org.h2.Driver",
        "url" : "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        "username" : "",
        "password" : ""
    }
]
```

Interface

```java
@Database(context = "H2")
private interface UserRepository {
        
    @Select(sql = "select * from users")
    public List<User> fetchAll();
        
    @Delete(sql = "delete from users where id = ?")
    public int delete(int id);
        
}
```

Use

```java
UserRepository repository = StormRepository.newInstance(UserRepository.class);
List<User> users = repository.fetchAll();
```

Mapping

```java
@ColumnProperty(name = "employee_id")
private int employeeID;
```

## Annotations ##

Select

```java
@Select(sql = "select * from users")
```

Delete

```java
@Delete(sql = "delete from users where id = ?")
```

Update

```java
@Update(sql = "update users set firstname = ? where id = ?")
```

Insert

```java
@Insert(sql = "insert into users (firstname, lastname, employee_id) values (?, ?, ?)")
```
