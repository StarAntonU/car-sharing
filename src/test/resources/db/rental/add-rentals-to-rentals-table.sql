INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_active) VALUES
 (1, NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, 1, 2, TRUE),
 (2, NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), NULL, 1, 2, FALSE),
 (3, NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), 1, 2, FALSE);
