UPDATE users
SET name = 'Admin PR Digital',
    email = 'admin@prdigital.ao'
WHERE email = 'admin@ponteshop.ao'
  AND NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@prdigital.ao');
