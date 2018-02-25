package com.decora.authentication;

import com.decora.DecoraWSException;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.user.SystemUser;
import com.decora.query.FindSystemUser;

import java.nio.charset.Charset;
import java.util.Base64;

public class AuthenticationManager {

    private final DatabaseService databaseService;

    public AuthenticationManager(final DatabaseService theDatabaseService) {
        databaseService = theDatabaseService;
    }

    public final SystemUser login(final String email, final String password) throws DecoraWSException {
        try {
            final SystemUser systemUser = databaseService.submit(new FindSystemUser(email));
            if (systemUser == null) {
                return null;
            } else {
                String decodedPwd = new String(Base64.getDecoder().decode(systemUser.getPassword().getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"));
                if (decodedPwd.equals(password)) {
                    return systemUser;
                } else {
                    return null;
                }
            }
        } catch (TransactionException e) {
            throw new DecoraWSException("Error finding user: " + email, e);
        }
    }
}
