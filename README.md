## Storm - Object-Relational Mapper ##

Interface

```java
    @Database(context = "H2")
    private interface UserRepository {
        
        @Select(sql = "select * from users")
        public List<User> fetchAll();
        
        @Select(sql = "select * from users where id = ?")
        public User findById(int id);
        
        @Select(sql = "select firstname || ' ' || lastname from users")
        public List<String> fullNames();

    }
```

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

Use

```java
    UserRepository repository = StormRepository.newInstance(UserRepository.class);
    
    List<User> users = repository.fetchAll();
    User user = repository.findById(12);
    List<String> fullNames = repository.fullNames();

```

Mapping

```java
    @ColumnProperty(name = "employee_id")
    private int employeeID;
```