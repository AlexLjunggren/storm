## Storm - Object-Relational Mapper ##

Interface

```java
    @Connection(context = "H2")
    private interface UserRepository {
        
        @Select(sql = "select * from users order by id")
        public List<User> fetchAll();
        
        @Select(sql = "select * from users where id = ?")
        public User findById(int id);
        
    }
```

Use

```java
    UserRepository repository = StormRepository.newInstance(UserRepository.class);
    
    List<User> users = repository.fetchAll();
    User user = repository.findById(12);

```