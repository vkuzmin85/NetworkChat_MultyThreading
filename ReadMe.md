 Курсовая работа "Сетевой чат"
## Схема работы программы
### Серверная часть
![Схема работы серверной части](server.png)

Класс Server:
- считывает номер порта для подключения клиентов через файл настроек settings.txt
- внутри фунции main создает экземпляр класса ServerSocket, который передается в конструктор класса ServeClient предназначенного для обслуживания соединения с клиентом. Экземпляры объекта ServeClient добавляются в список serverList. Каждое соединение обрабатывается в отдельном потоке.

Класс Logger с помощью метода logging() записывает все полученные сообщения в file.log.

### Клиентская часть
![Схема работы клиентской части](client.png)

Класс Client:
- внутри фунции main считывает номер порта и ip-адрес через файл настроек settings.txt, создает сокет, поток для чтения из сокета, поток для записи в сокет и экземпляр класса Client
- создает поток ReadMessage для получения сообщений
- создает поток WriteMessage для отправки сообщений.
