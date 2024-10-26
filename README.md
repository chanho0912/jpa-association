# jpa-association

## 요구사항 1 - Select Join Query 만들기 (EAGER)
- 임의의 데이터를 넣어서 데이터를 가져와 보자
> Sql 쿼리 문을 수정해 보자

```java
public class CustomSelect {

}
```
[ ] Custom Select Query Builder 구현
- [ ] join table이 없는 경우 구현
- [ ] join table이 있는 경우 구현

## 요구사항 2 - Join Query 를 만들어 Entity 화 해보기
> FetchType.EAGER 인 경우

```java
public class SimplePersistenceContext implements PersistenceContext {


}
```

## 요구사항 3 - Save 시 Insert Query
> 연관 관계에 대한 데이터 저장 시 쿼리 만들어 보기

부모 데이터가 있는 경우, 부모 데이터가 없는 경우 나누어서 구현
```java
// order 가 있다면?
Order order = new Order();

OrderItem orderItem1 = new OrderItem();
OrderItem orderItem2 = new OrderItem();

order.getOrderItems().add(orderItem1);
order.getOrderItems().add(orderItem2);
```
