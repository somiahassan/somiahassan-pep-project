package Controller;
import io.javalin.Javalin;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;


/**
 * The SocialMediaController class defines the Javalin API for managing users and messages.
 * Endpoints are specified in the startAPI() method, which must be called to start the API.
 */
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
        this.objectMapper = new ObjectMapper();
    }
    /**
     * Starts the Javalin API and defines endpoints.
     * @return a Javalin app object.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create().start(8080);

        app.post("/register", this::registerAccountHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);

        return app;
    }

    private void registerAccountHandler(Context ctx) {
        try {
            Account account = objectMapper.readValue(ctx.body(), Account.class);
            Account createdAccount = accountService.createAccount(account);
            if (createdAccount != null) {
                ctx.status(201).json(createdAccount);
            } else {
                ctx.status(400).result("Invalid account data or username already exists.");
            }
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    private void loginHandler(Context ctx) {
        try {
            Account account = objectMapper.readValue(ctx.body(), Account.class);
            Account loggedInAccount = accountService.getAccount(account.getUsername(), account.getPassword());
            if (loggedInAccount != null) {
                ctx.status(200).json(loggedInAccount);
            } else {
                ctx.status(401).result("Invalid credentials.");
            }
        } catch (Exception e) {
            ctx.status(401).json(e.getMessage());
        }
    }

    private void postMessageHandler(Context ctx) {
        try {
            Message message = objectMapper.readValue(ctx.body(), Message.class);
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                ctx.status(201).json(createdMessage);
            } else {
                ctx.status(400).result("Invalid message data.");
            }
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    private void getAllMessagesHandler(Context ctx) {
        try {
            List<Message> messages = messageService.getAllMessages();
            ctx.status(200).json(messages);
        } catch (Exception e) {
            ctx.status(500).json(e.getMessage());
        }
    }

    private void getMessageByIdHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(messageId);
            if (message != null) {
                ctx.status(200).json(message);
            } else {
                ctx.status(404).result("Message not found.");
            }
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    private void deleteMessageHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            boolean result = messageService.deleteMessage(messageId);
            ctx.status(result ? 204 : 404);
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    private void updateMessageHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            @SuppressWarnings("unchecked")
            Map<String, Object> requestBody = objectMapper.readValue(ctx.body(), Map.class);
            String messageText = (String) requestBody.get("message_text");
            Message updatedMessage = messageService.updateMessage(messageId, messageText);
            if (updatedMessage != null) {
                ctx.status(200).json(updatedMessage);
            } else {
                ctx.status(400).result("Invalid message data or message not found.");
            }
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    private void getMessagesByUserHandler(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = messageService.getMessagesByUser(accountId);
            ctx.status(200).json(messages);
        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }
}