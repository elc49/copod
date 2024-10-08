-- name: CountUsers :one
SELECT COUNT(*) FROM users;

-- name: CreateUserByPhone :one
INSERT INTO users (
  phone, username, avatar
) VALUES (
  $1, $2, $3
) RETURNING *;

-- name: GetUserByPhone :one
SELECT id, phone, has_farming_rights, has_poster_rights, created_at, updated_at FROM users
WHERE phone = $1 AND deleted_at IS NULL;

-- name: GetUserByID :one
SELECT id, phone, username, avatar, notification_tracking_id, has_farming_rights, has_poster_rights, created_at, updated_at FROM users
WHERE id = $1 AND deleted_at IS NULL;

-- name: SetUserFarmingRights :one
UPDATE users SET has_farming_rights = $1
WHERE id = $2
RETURNING *;

-- name: SetUserPosterRights :one
UPDATE users SET has_poster_rights = $1
WHERE id = $2
RETURNING *;

-- name: SetUserNotificationTrackingID :one
UPDATE users SET notification_tracking_id = $1
WHERE id = $2
RETURNING *;

-- name: ClearTestUsers :exec
DELETE FROM users;
