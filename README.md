## Storm - Object-Relational Mapper ##

Interface

```java
    @Connection(context = "H2")
    private interface UserRepository {
        
        @Select(sql = "select * from users")
        public List<User> fetchAll();
        
        @Select(sql = "select * from users where id = ?")
        public User findById(int id);
        
        @Select(sql = "select firstname || ' ' || lastname from users")
        public List<String> fullNames();

    }
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