-- name: FarmRating :one
SELECT SUM(rate) AS rating FROM ratings
WHERE farm_id = $1;

-- name: FarmReviewers :one
SELECT COUNT(*) AS reviewers FROM ratings
WHERE farm_id = $1;
