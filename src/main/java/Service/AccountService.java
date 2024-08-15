package Service;

import DAO.UserDAO;
import Model.Account;

public class AccountService {

    private final UserDAO userDAO = new UserDAO();

    public Account createAccount(Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }
        return userDAO.createAccount(account);
    }

    public Account getAccount(String username, String password) {
        return userDAO.getAccount(username, password);
    }
}
