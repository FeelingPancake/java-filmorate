# java-filmorate

![ER-diagram for DB this app](https://github.com/FeelingPancake/java-filmorate/blob/main/Filmorate_DB.png)

## CRUD-операции для базы данных такого типа:
* Получить список всех фильмов
  `SELECT * FROM Film`
* Получить список фильмов отсортированный по полулярности
  `SELECT * FROM Film ORDER BY rating desc`
* Получить всех пользователей
  `Select * From User`  
* Получить всех друзей пользоваьеля
  `SELECT * FROM User JOIN Frienship User.id = Friendship.userId and FriendShip.is_Confirmed = true`
и т.д. 
