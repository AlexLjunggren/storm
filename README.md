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
@Database("H2")
private interface UserRepository {
        
    @Select(sql = "select * from users")
    public List<User> fetchAll();
        
    @Delete(sql = "delete from users where id = #{id}")
    public int delete(@Param("id") int id);
        
}
```

Use

```java
UserRepository repository = StormRepository.newInstance(UserRepository.class);
List<User> users = repository.fetchAll();
```

Entity Mapping

```java
@Table("users")
public class User {

    @Id(generated = Generated.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    
    @ColumnProperty(name = "employee_id")
    private int employeeID;
    
    @Transient
    private boolean hourly;
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
        
@Delete(sql = "delete from users where id = #{id}")
public int deleteById(@Param("id") int id);
```

Update

```java
@Update
public int update(User user);

@Update
public int updateAll(User... users);

@Update(sql = "update users set firstname = #{firstname} where id = #{id}")
public int updateFirstName(@Param("firstname") String firstname, @Param("id") int id);
```

Insert

```java
@Insert
public int insert(User user);
        
@Insert
public int insertAll(User... users);
        
@Insert(sql = "insert into users (firstname, lastname, employee_id) values (#{firstname}, #{lastname}, #{employeeID})")
public int insert(@Param("firstname") String firstname, @Param("lastname") String lastname, @Param("employeeID") int employeeID);
```

Insert Batch

```java
@InsertBatch
public int insert(User... users);
        
@InsertBatch
public int insert(List<User> users);
```

## Peek ##

Peek at SQL statement

```java
@Database("H2")
private interface UserRepository extends Peek<UserRepository> {
    ...
}

Consumer<String> print = e -> System.out.println(e);
UserRepository repository = StormRepository.newInstance(UserRepository.class).peek(print);
repository.count();
```

```
Output: select count(*) from users
```

## Paging ##

Add Paging to @Select

```java
new Paging(page, rows);
```

- page: Page to query (index starting at 1)
- rows: Number or rows / results to return

```java
@Database("H2")
private interface UserRepository  {

    @Select(sql = "select * from users order by id")
    public User[] fetchAllOrdered(Paging paging);

}

Paging paging = new Paging(2, 2);
User[] users = repository.fetchAllOrdered(paging);

```

** Note: ** Paging can be passed in at any point in the argument list

```java
@Select(sql = "select * from users where lastname = #{lastname}")
public findByLastName(Paging paging, @Param("lastname") String lastname);
    // or
public findByLastName(@Param("lastname") String lastname, Paging paging);

```

## TODO ##

1. Remove requirement for @Param if only one parameter exists
1. Handle embedded objects & parameterized objects on insert, delete, update

## Dependencies ##

- reflection-utils
