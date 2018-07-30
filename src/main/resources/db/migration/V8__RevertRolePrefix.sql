UPDATE users
    SET authority = 'ROLE_' || authority
    WHERE authority NOT LIKE 'ROLE_%';
