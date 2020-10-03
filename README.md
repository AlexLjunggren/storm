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

Entity Mapping

```java
@Table(name = "users")
public class User {

    @Id(generated = Generated.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    
    @ColumnProperty(name = "employee_id")
    private int employeeID;
    
}
```

## Annotations ##

Select

```java
@Select(sql = "select * from users")
List<User> fetchAll();
```

Delete

```java
@Delete
public int delete(User user);
        
@Delete
public int deleteAll(User... users);
        
@Delete(sql = "delete from users where id = ?")
public int deleteById(int id);
```

Update

```java
@Update
public int update(User user);

@Update
public int updateAll(User... users);

@Update(sql = "update users set firstname = ? where id = ?")
public int updateFirstName(String name, int id);
```

Insert

```java
@Insert
public int insert(User user);
        
@Insert
public int insertAll(User... users);
        
@Insert(sql = "insert into users (firstname, lastname, employee_id) values (?, ?, ?)")
public int insert(String firstName, String lastName, int employeeID);
```

## Peek ##

Peek at SQL statement

```java
Consumer<String> peek = e -> System.out.println(e);
UserRepository repository = StormRepository.newInstance(UserRepository.class, peek);
repository.count();
```

```
Output: select count(*) from users
```


