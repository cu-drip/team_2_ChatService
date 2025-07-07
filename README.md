# База Данных
```sql
create table chats
(
    id    UUID primary key,
    name  varchar(128) not null unique,
    owner UUID         not null
)

create table messages
(
    id         UUID primary key,
    chatId     UUID not null references chats (id),
    author     UUID not null,

    content    text not null,
    created_at timestamp default now()
)

create table chats_users
(
    chatId UUID references chats (id),
    userId UUID,

    primary key (chatId, userId)
)
```

# REST API
## User Scope
```
Authorization: Bearer JWT
```

### Список чатов
`GET /api/chats` \
Response:
```json
[
    {
        "id": "uuid",
        "name": "string",
        "owner": "uuid"
    }
]
```

### Создание чата
`POST /api/chats` \
Body:
```json
{
    "name": "string",
    "users": [
        "uuid"
    ]
}
```
Response:
```json
{
    "id": "uuid",
    "name": "string",
    "owner": "uuid"
}
```

### Получить чат по id
`GET /api/chats/{id}` \
Response:
```json
{
    "id": "uuid",
    "name": "string",
    "owner": "uuid"
}
```

### Получить список участников чата
`GET /api/chats/{id}/users` \
Response:
```json
[
    "uuid"
]
```

### Добавить участников чата (owner only)
`POST /api/chats/{id}/users/{userId}` \
Response: 204

### Удалить участников чата  (owner only)
`DELETE /api/chats/{id}/users/{userId}` \
Response: 204

### Замутить участника чата  (owner and admin only)
`PUT /api/chats/{id}/users/{userId}/mute` \
Response: 204

### Размутить участника чата  (owner and admin only)
`PUT /api/chats/{id}/users/{userId}/unmute` \
Response: 204

### Получить историю сообщений
`GET /api/chats/{id}/messages?limit=100&after={messageId}` \
Response:
```json
[
    {
        "id": "uuid",
        "author": "uuid",
        "content": "string",
        "created_at": "timestamp"
    }
]
```

# Websocket API
## Подключится к чату
`WS /ws/chats/{id}` \
Headers:
```
Authorization: Bearer JWT
```
