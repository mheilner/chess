### Data Table Schema for DAO's

```users
+-----------+            games
| user_id   |<--+     +------------+
| username  |   +---->| game_id    |
| password  |   |     | game_name  |
| email     |   |     | white_user_id |
+-----------+   |     | black_user_id |
                |     | game_state  |
                |     +------------+
                |
                |     auth_tokens
                |     +------------+
                +---->| token_id    |
                      | auth_token  |
                      | user_id     |
                      | creation_time |
                      | expiry_time |
                      +------------+
```